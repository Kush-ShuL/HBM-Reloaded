package com.hbm.worldgen.ore;

/** DungeonToolbox.generateOre arguments, before the new optional negative-Y layer. */
public record OreProfile(String symbol, String config, int count, int size, int minY, int variance, boolean enabled) { }
