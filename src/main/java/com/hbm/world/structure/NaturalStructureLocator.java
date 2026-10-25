package com.hbm.world.structure;

import com.hbm.HbmReloaded;
import com.hbm.world.structure.mixin.StructurePlacementAccess;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

/** Read-only native start prediction for the original named source scheduler; never requests a chunk. */
public final class NaturalStructureLocator {
    private static final int MAX_DISTANCE = 256;
    private static final ResourceKey<Structure> STRUCTURE = ResourceKey.create(Registries.STRUCTURE, HbmReloaded.id("legacy_natural_structures"));
    private static final ResourceKey<StructureSet> SET = ResourceKey.create(Registries.STRUCTURE_SET, HbmReloaded.id("legacy_natural_structures"));
    private static final List<String> SOURCE_NAMES = List.of(
            "abandoned_arcticstation",
            "aircraft_carrier",
            "beached_patrol",
            "broadcaster_tower",
            "bunker",
            "crane",
            "crashed_vertibird",
            "dead_dish_small",
            "desert_shack_1",
            "desert_shack_2",
            "desert_shack_3",
            "dish",
            "factory",
            "falloutshelter_house",
            "features",
            "forest_post",
            "forestchem",
            "labolatory",
            "lighthouse",
            "meteor_dungeon",
            "military_base",
            "oil_rig",
            "plane1",
            "plane2",
            "radio",
            "ruinA",
            "ruinB",
            "ruinC",
            "ruinD",
            "ruinE",
            "ruinF",
            "ruinG",
            "ruinH",
            "ruinI",
            "ruinJ",
            "spire",
            "submarine",
            "tower_base",
            "vertibird",
            "water_pump");

    public static List<String> names() { return SOURCE_NAMES; }

    /** Capture and every search slice use the server thread, serial with native resource replacement. */
    public static Optional<Search> capture(ServerLevel level) {
        if (!level.getServer().isSameThread()) throw new IllegalStateException("Structure locate capture requires the server thread");
        if (level.dimension() != Level.OVERWORLD || !NaturalStructures.weightedStructuresEnabled(level.structureManager().shouldGenerateStructures()))
            return Optional.empty();
        var registries = level.registryAccess();
        var holder = registries.registryOrThrow(Registries.STRUCTURE).getHolder(STRUCTURE);
        var set = registries.registryOrThrow(Registries.STRUCTURE_SET).get(SET);
        if (holder.isEmpty() || !(holder.get().value() instanceof LegacyNaturalStructure structure) || set == null
                || !(set.placement() instanceof RandomSpreadStructurePlacement spread)) return Optional.empty();
        // The real source scheduler has one native wrapper entry. Arbitrary weighted replacement sets
        // cannot safely be reported as this named scheduler and are rejected rather than guessed.
        if (set.structures().size() != 1 || set.structures().getFirst().structure().value() != structure)
            return Optional.empty();
        var chunks = level.getChunkSource();
        var state = chunks.getGeneratorState();
        var placements = state.getPlacementsForStructure(holder.get());
        // Flat generators may disable this set. Extra datapack placements require their own truthful mapping.
        if (placements.size() != 1 || placements.getFirst() != spread) return Optional.empty();
        var generator = chunks.getGenerator();
        var height = LevelHeightAccessor.create(level.getMinBuildHeight(), level.getHeight());
        return Optional.of(new Search(level.getServer(), registries, generator, generator.getBiomeSource(), chunks.randomState(),
                level.getStructureManager(), state, structure, spread, height, level.getSeed()));
    }

    public static final class Search {
        private final MinecraftServer server;
        private final RegistryAccess registries;
        private final ChunkGenerator generator;
        private final BiomeSource biomes;
        private final RandomState random;
        private final StructureTemplateManager templates;
        private final ChunkGeneratorStructureState state;
        private final LegacyNaturalStructure structure;
        private final RandomSpreadStructurePlacement placement;
        private final LevelHeightAccessor height;
        private final long seed;
        private final List<Integer> settings;

        private Search(MinecraftServer server, RegistryAccess registries, ChunkGenerator generator, BiomeSource biomes, RandomState random,
                       StructureTemplateManager templates, ChunkGeneratorStructureState state,
                       LegacyNaturalStructure structure, RandomSpreadStructurePlacement placement,
                       LevelHeightAccessor height, long seed) {
            this.server = server; this.registries = registries; this.generator = generator; this.biomes = biomes; this.random = random;
            this.templates = templates; this.state = state; this.structure = structure; this.placement = placement;
            this.height = height; this.seed = seed; this.settings = currentSettings();
        }

        private List<Integer> currentSettings() {
            var values = new java.util.ArrayList<Integer>();
            values.add(NaturalStructures.weightedStructuresEnabled() ? 1 : 0);
            values.add(placement.spacing()); values.add(placement.separation());
            NaturalStructures.SPAWNS.forEach(spawn -> values.add(NaturalStructures.weight(spawn)));
            return List.copyOf(values);
        }

        public boolean settingsUnchanged() { return settings.equals(currentSettings()); }

        public Cursor cursor(String name, int centerX, int centerZ) {
            if (!server.isSameThread()) throw new IllegalStateException("Structure locate cursor requires the server thread");
            if (!SOURCE_NAMES.contains(name)) throw new IllegalArgumentException("Unknown natural structure name");
            return new Cursor(name, centerX, centerZ);
        }

        /** Original center and all square-ring visits, including the source's duplicate corners. */
        public final class Cursor {
            private final String name;
            private final int centerX, centerZ;
            private int radius, ringIndex;
            private boolean finished;
            private ChunkPos result;

            private Cursor(String name, int centerX, int centerZ) {
                this.name = name; this.centerX = centerX; this.centerZ = centerZ;
            }

            public String name() { return name; }
            public ChunkPos result() { return result; }

            /** Soft budget: one native full-start construction can run past the deadline. */
            public boolean advance(int maxVisits, long deadlineNanos, AtomicBoolean cancelled) {
                if (!server.isSameThread()) throw new IllegalStateException("Structure locate slice requires the server thread");
                for (int visits = 0; !finished && visits < maxVisits
                        && (visits == 0 || System.nanoTime() < deadlineNanos); visits++) {
                    if (cancelled.get()) throw new CancellationException();
                    var candidate = next();
                    var match = matches(name, candidate.x, candidate.z, cancelled);
                    if (match == Match.VALID_START) { result = candidate; finished = true; }
                    if (radius >= MAX_DISTANCE) finished = true;
                    // At most one potentially expensive native start build per tick. An invalid
                    // start does not discard later candidates; the cursor resumes at the next visit.
                    if (match != Match.NO_SELECTION) break;
                }
                return finished;
            }

            private ChunkPos next() {
                if (radius == 0) { radius = 1; return new ChunkPos(centerX, centerZ); }
                int r = radius, index = ringIndex++;
                int sweepSize = 4 * r + 2;
                ChunkPos position;
                if (index < sweepSize) {
                    position = new ChunkPos(centerX - r + index / 2, centerZ + ((index & 1) == 0 ? -r : r));
                } else {
                    index -= sweepSize;
                    position = new ChunkPos(centerX + ((index & 1) == 0 ? -r : r), centerZ - r + index / 2);
                }
                if (ringIndex == 8 * r + 4) { radius++; ringIndex = 0; }
                return position;
            }
        }

        private enum Match { NO_SELECTION, INVALID_START, VALID_START }

        private Match matches(String name, int x, int z, AtomicBoolean cancelled) {
            if (cancelled.get() || Thread.currentThread().isInterrupted()) throw new CancellationException();
            if (!placement.isStructureChunk(state, x, z)) return Match.NO_SELECTION;
            if (!settingsUnchanged()) throw new CancellationException("Natural structure settings changed");
            var chunk = new ChunkPos(x, z);
            var context = new Structure.GenerationContext(registries, generator, biomes, random, templates,
                    seed, chunk, height, structure.biomes()::contains);
            var biome = StructureSurfaceBiomes.sample(context, chunk.getMinBlockX() + 8, chunk.getMinBlockZ() + 8);
            var selected = LegacyStructureSelection.choose(seed, chunk, biome, placement.spacing(), placement.separation(),
                    ((StructurePlacementAccess) placement).hbm$sourceSalt());
            if (selected == null || !selected.spawn().name().equals(name)) return Match.NO_SELECTION;
            // Use the actual registered structure's full start construction (including jigsaw/dependency
            // checks and biome filter), not just a selected weight or an unverified grid coordinate.
            // This constructs pieces only; it does not place them, access chunk status or write world data.
            return structure.generate(registries, generator, biomes, random, templates, seed, chunk, 0,
                    height, structure.biomes()::contains).isValid() ? Match.VALID_START : Match.INVALID_START;
        }
    }

    private NaturalStructureLocator() {}
}
