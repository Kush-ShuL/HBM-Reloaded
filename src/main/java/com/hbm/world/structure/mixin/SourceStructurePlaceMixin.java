package com.hbm.world.structure.mixin;

import com.hbm.world.structure.LegacyGeneratedStructure;
import com.hbm.world.structure.NaturalStructures;
import com.hbm.world.structure.SourceDecoration;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.commands.PlaceCommand;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Explicit user placement shares the real source planner, while retaining vanilla placement. */
@Mixin(value = PlaceCommand.class, remap = false)
public abstract class SourceStructurePlaceMixin {
    @WrapOperation(method = "placeStructure", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/level/levelgen/structure/Structure;generate(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/biome/BiomeSource;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;JLnet/minecraft/world/level/ChunkPos;ILnet/minecraft/world/level/LevelHeightAccessor;Ljava/util/function/Predicate;)Lnet/minecraft/world/level/levelgen/structure/StructureStart;"))
    private static StructureStart hbm$explicitSourcePlan(
            Structure structure, RegistryAccess registries, ChunkGenerator generator, BiomeSource biomes,
            RandomState randomState, StructureTemplateManager templates, long seed, ChunkPos position,
            int references, LevelHeightAccessor height, Predicate<Holder<Biome>> validBiome,
            Operation<StructureStart> original, CommandSourceStack source,
            Holder.Reference<Structure> selected, BlockPos requestedPosition) {
        if (!(structure instanceof LegacyGeneratedStructure legacy))
            return original.call(structure, registries, generator, biomes, randomState, templates,
                    seed, position, references, height, validBiome);
        var level = source.getLevel();
        if (level.dimension() != Level.OVERWORLD
                || !NaturalStructures.oldBuildingsEnabled(level.structureManager().shouldGenerateStructures()))
            return StructureStart.INVALID_START;

        // Only an explicit /place may alter existing terrain. Finish its bounded input halo
        // outside the source lock, so no source worker ever waits on another source worker.
        for (int x = position.x - 4; x <= position.x + 4; x++)
            for (int z = position.z - 4; z <= position.z + 4; z++)
                level.getChunk(x, z, ChunkStatus.FULL, true);
        return SourceDecoration.planDirect(legacy, level, generator, position, references);
    }
}
