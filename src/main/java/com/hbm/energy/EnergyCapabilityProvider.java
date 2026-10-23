package com.hbm.energy;

import net.minecraft.core.Direction;

/** Lets a block entity expose a composed buffer or conductor for a queried face. */
public interface EnergyCapabilityProvider {
    /** Return null for a disconnected face. */
    EnergyConnector energyConnector(Direction side);
}
