package com.hbm.api.machine;

import java.util.List;
import java.math.BigInteger;

/** Server-readable machine state shared by optional information overlays. */
public interface MachineView {
    long energyStored();

    long energyCapacity();

    /** Arbitrary-precision views; original fixed-capacity machines keep their long adapters. */
    default BigInteger energyStoredExact() {
        return this instanceof com.hbm.energy.EnergyHandler he ? he.getPowerExact() : BigInteger.valueOf(energyStored());
    }
    default BigInteger energyCapacityExact() {
        return this instanceof com.hbm.energy.EnergyHandler he ? he.getMaxPowerExact() : BigInteger.valueOf(energyCapacity());
    }

    default List<TankView> tanks() {
        return List.of();
    }

    default List<ProgressView> progressBars() {
        return List.of();
    }

    record ProgressView(String name, double value, double maximum, int color) {}

    record TankView(String name, long amount, long capacity, boolean literal, int color) {
        public TankView(String name, long amount, long capacity, boolean literal) { this(name, amount, capacity, literal, 0x62A9DC); }
        public TankView(String name, long amount, long capacity) { this(name, amount, capacity, false); }
        public TankView(com.hbm.fluid.HbmFluidType type, long amount, long capacity) {
            this(type.getConditionalName(), amount, capacity, type.hasLocalizedOverride(), type.getColor());
        }
    }
}
