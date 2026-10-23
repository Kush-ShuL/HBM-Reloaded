package com.hbm.block.fluid;

/** Raw X5846/Forge fluid fields, deliberately separate from NTM pipe-fluid identities. */
public record WorldFluidSpec(String symbol, String fluid, String originalClass, String material,
                             String still, String flowing, int density, int viscosity,
                             int temperature, int luminosity, int quanta, int tickRate,
                             int publicTickRate, int renderPass, boolean finite, String bucket) {
    public int light(int metadata) {
        if (luminosity == 0) return 0;
        int amount = finite ? metadata : quanta - metadata - 1;
        return Math.max(0, Math.min(15, (int) (amount / (float) quanta * luminosity)));
    }
}
