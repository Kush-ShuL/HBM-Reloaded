package com.hbm.fluid.network;

import com.hbm.fluid.HbmFluidType;

import com.hbm.energy.ConnectionPriority;

public interface FluidReceiver extends FluidUser {

	/** Sends fluid of the desired type and pressure to the receiver, returns the remainder */
	public long transferFluid(HbmFluidType type, int pressure, long amount);
	public default long getReceiverSpeed(HbmFluidType type, int pressure) { return 1_000_000_000; }
	public long getDemand(HbmFluidType type, int pressure);
	
	public default int[] getReceivingPressureRange(HbmFluidType type) { return DEFAULT_PRESSURE_RANGE; }
	
	public default ConnectionPriority getFluidPriority() {
		return ConnectionPriority.NORMAL;
	}
}
