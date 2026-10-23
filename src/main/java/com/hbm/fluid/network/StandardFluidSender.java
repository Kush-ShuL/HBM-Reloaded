package com.hbm.fluid.network;

import com.hbm.fluid.FluidTank;
import com.hbm.fluid.HbmFluidType;

/**
 * FluidProvider with standard implementation for fluid provision and fluid removal.
 * @author hbm
 */
public interface StandardFluidSender extends FluidProvider {

	public FluidTank[] getSendingTanks();

	@Override
	public default long getFluidAvailable(HbmFluidType type, int pressure) {
		long amount = 0;
		for(FluidTank tank : getSendingTanks()) {
			if(tank.getTankType() == type && tank.getPressure() == pressure) amount += tank.getFill();
		}
		return amount;
	}

	@Override
	public default void useUpFluid(HbmFluidType type, int pressure, long amount) {
		int tanks = 0;
		for(FluidTank tank : getSendingTanks()) {
			if(tank.getTankType() == type && tank.getPressure() == pressure) tanks++;
		}
		if(tanks > 1) {
			int firstRound = (int) Math.floor((double) amount / (double) tanks);
			for(FluidTank tank : getSendingTanks()) {
				if(tank.getTankType() == type && tank.getPressure() == pressure) {
					int toRem = Math.min(firstRound, tank.getFill());
					tank.setFill(tank.getFill() - toRem);
					amount -= toRem;
				}
			}
		}
		if(amount > 0) for(FluidTank tank : getSendingTanks()) {
			if(tank.getTankType() == type && tank.getPressure() == pressure) {
				int toRem = (int) Math.min(amount, tank.getFill());
				tank.setFill(tank.getFill() - toRem);
				amount -= toRem;
			}
		}
	}

	@Override
	public default int[] getProvidingPressureRange(HbmFluidType type) {
		int lowest = HIGHEST_VALID_PRESSURE;
		int highest = 0;

		for(FluidTank tank : getSendingTanks()) {
			if(tank.getTankType() == type) {
				if(tank.getPressure() < lowest) lowest = tank.getPressure();
				if(tank.getPressure() > highest) highest = tank.getPressure();
			}
		}

		return lowest <= highest ? new int[] {lowest, highest} : DEFAULT_PRESSURE_RANGE;
	}

	@Override
	public default long getProviderSpeed(HbmFluidType type, int pressure) {
		return 1_000_000_000;
	}
}
