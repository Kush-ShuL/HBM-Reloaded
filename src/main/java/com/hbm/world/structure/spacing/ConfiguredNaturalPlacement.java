package com.hbm.world.structure.spacing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

/** A native random-spread placement; no duplicate candidate/locate/random engine. */
public final class ConfiguredNaturalPlacement extends RandomSpreadStructurePlacement {
    public static final MapCodec<ConfiguredNaturalPlacement> CODEC =
            RecordCodecBuilder.<ConfiguredNaturalPlacement>mapCodec(instance -> placementCodec(instance)
                    .and(instance.group(
                            Codec.intRange(0, 4096).fieldOf("spacing").forGetter(ConfiguredNaturalPlacement::jsonSpacing),
                            Codec.intRange(0, 4096).fieldOf("separation").forGetter(ConfiguredNaturalPlacement::jsonSeparation),
                            RandomSpreadType.CODEC.optionalFieldOf("spread_type", RandomSpreadType.LINEAR)
                                    .forGetter(ConfiguredNaturalPlacement::spreadType)))
                    .apply(instance, ConfiguredNaturalPlacement::new))
                    .validate(value -> value.jsonSpacing() <= value.jsonSeparation()
                            ? DataResult.error(() -> "Spacing has to be larger than separation")
                            : DataResult.success(value));

    private record Runtime(StructureSpacingConfig.Settings settings, RandomSpreadStructurePlacement placement) {}
    private volatile Runtime runtime;

    public ConfiguredNaturalPlacement(Vec3i locateOffset, StructurePlacement.FrequencyReductionMethod frequencyMethod,
                                      float frequency, int salt, Optional<StructurePlacement.ExclusionZone> exclusion,
                                      int spacing, int separation, RandomSpreadType spreadType) {
        super(locateOffset, frequencyMethod, frequency, salt, exclusion, spacing, separation, spreadType);
    }

    private int jsonSpacing() { return super.spacing(); }
    private int jsonSeparation() { return super.separation(); }

    private RandomSpreadStructurePlacement effective() {
        var settings = StructureSpacingConfig.current();
        var result = runtime;
        if (result == null || result.settings != settings) {
            result = new Runtime(settings, new RandomSpreadStructurePlacement(locateOffset(),
                    frequencyReductionMethod(), frequency(), salt(), exclusionZone(), settings.spacing(),
                    settings.separation(), spreadType()));
            runtime = result;
        }
        return result.placement;
    }

    @Override public int spacing() { return effective().spacing(); }
    @Override public int separation() { return effective().separation(); }
    @Override public ChunkPos getPotentialStructureChunk(long seed, int chunkX, int chunkZ) {
        return effective().getPotentialStructureChunk(seed, chunkX, chunkZ);
    }
    @Override public StructurePlacementType<?> type() { return StructureSpacingConfig.PLACEMENT.get(); }
}
