package com.hbm.block.ore;

import java.util.List;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/** Source-derived registration and drop rules; no inferred modern ore defaults. */
public record OreSpec(String id, String originalClass, String material, String sound, float hardness,
                      float explosionResistance, int light, boolean requiresTool, boolean canSpawn,
                      String drop, String alternateDrop, int alternateChance, int quantityMin,
                      int quantityRandom, String fortune, int dropMetadata, List<String> variants,
                      String rarity, boolean oilNeighbor, float radiation, boolean depth, boolean dragonProof) {
    public BlockBehaviour.Properties properties() {
        var properties = BlockBehaviour.Properties.of().mapColor(material.equals("sand") ? MapColor.SAND : material.equals("wood") ? MapColor.WOOD : MapColor.STONE)
                .strength(hardness, explosionResistance).lightLevel(state -> light)
                .sound(sound.equals("sand") ? SoundType.SAND : sound.equals("wood") ? SoundType.WOOD : SoundType.STONE);
        if (requiresTool) properties.requiresCorrectToolForDrops();
        if (!canSpawn) properties.isValidSpawn((state, world, pos, entity) -> false);
        if (radiation > 0) properties.randomTicks();
        if (originalClass.equals("BlockOutgas")) properties.randomTicks();
        if (originalClass.equals("BlockFissure")) properties.randomTicks();
        return properties;
    }

    public int ordinal(int metadata) { return variants.isEmpty() ? 0 : Math.abs(metadata % variants.size()); }
    public boolean multi() { return !variants.isEmpty(); }
}
