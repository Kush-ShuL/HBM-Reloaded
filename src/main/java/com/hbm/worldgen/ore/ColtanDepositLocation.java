package com.hbm.worldgen.ore;

import java.util.Random;

/** One source seed+5 Gaussian center shared by the existing ore placement and its actual compass. */
public record ColtanDepositLocation(int x, int z) {
    public static ColtanDepositLocation fromSeed(long seed) {
        var random = new Random(seed + 5);
        return new ColtanDepositLocation((int) (random.nextGaussian() * 1500), (int) (random.nextGaussian() * 1500));
    }
}
