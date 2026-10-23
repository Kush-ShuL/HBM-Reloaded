package com.hbm.energy;

import net.minecraft.core.Direction;

/** Side is the queried face of this machine, matching the original contract. */
public interface SidedEnergyConnector extends EnergyConnector {
    default boolean canConnect(Direction side) {
        return side != null;
    }
}
