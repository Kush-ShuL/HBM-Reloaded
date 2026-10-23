package com.hbm.block.falling;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/** Source registration fields; hardness and old getExplosionResistance values stay unchanged. */
public record FallingSpec(String id, String originalClass, String material, String sound, String texture,
                          float hardness, float explosionResistance, boolean requiresTool, boolean beaconBase) {
    public BlockBehaviour.Properties properties() {
        var properties = BlockBehaviour.Properties.of().mapColor(switch (material) {
            case "sand" -> MapColor.SAND;
            case "ground" -> MapColor.DIRT;
            case "wood" -> MapColor.WOOD;
            case "iron" -> MapColor.METAL;
            default -> MapColor.STONE;
        }).strength(hardness, explosionResistance).sound(switch (sound) {
            case "sand" -> SoundType.SAND;
            case "gravel" -> SoundType.GRAVEL;
            case "metal" -> SoundType.METAL;
            case "wood" -> SoundType.WOOD;
            default -> SoundType.STONE;
        });
        if (requiresTool) properties.requiresCorrectToolForDrops();
        return properties;
    }
    public boolean hazardous() { return originalClass.equals("BlockHazardFalling"); }
}
