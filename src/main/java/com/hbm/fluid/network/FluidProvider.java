package com.hbm.fluid.network;

import com.hbm.fluid.HbmFluidType;

public interface FluidProvider extends FluidUser {

	public void useUpFluid(HbmFluidType type, int pressure, long amount);
	public default long getProviderSpeed(HbmFluidType type, int pressure) { return 1_000_000_000; }
	public long getFluidAvailable(HbmFluidType type, int pressure);
	
	public default int[] getProvidingPressureRange(HbmFluidType type) { return DEFAULT_PRESSURE_RANGE; }
}
