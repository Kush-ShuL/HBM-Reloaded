package com.hbm.fluid;

/** Original int millibuckets and pressure; this is not NeoForge's FluidStack. */
public final class FluidStack {
    public HbmFluidType type;
    public int fill;
    public int pressure;
    public FluidStack(HbmFluidType type, int fill) { this(type, fill, 0); }
    public FluidStack(HbmFluidType type, int fill, int pressure) { this.type = type; this.fill = fill; this.pressure = pressure; }
    @Deprecated public FluidStack(int fill, HbmFluidType type) { this(type, fill); }
}
