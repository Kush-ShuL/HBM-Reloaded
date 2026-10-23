package com.hbm.energy;

import java.math.BigInteger;

public interface EnergyReceiver extends EnergyHandler {
    /** Returns the amount rejected, as IEnergyReceiverMK2.transferPower did. */
    default long transferPower(long power) {
        if (power < 0) throw new IllegalArgumentException("Negative HE transfer");
        long stored = getPower();
        long capacity = getMaxPower();
        if (capacity < 0 || capacity < stored) throw new IllegalStateException("Invalid HE storage");
        long accepted = java.math.BigInteger.valueOf(capacity).subtract(java.math.BigInteger.valueOf(stored))
                .min(java.math.BigInteger.valueOf(power)).longValueExact();
        setPower(stored + accepted);
        return power - accepted;
    }

    default long getReceiverSpeed() {
        return getMaxPower();
    }

    default BigInteger getReceiverSpeedExact() { return BigInteger.valueOf(getReceiverSpeed()); }

    /** Exact native transfer; preserve custom legacy receiver behavior when its amount fits a long. */
    default BigInteger transferPowerExact(BigInteger power) {
        if (power.signum() < 0) throw new IllegalArgumentException("Negative HE transfer");
        if (power.bitLength() < 64) return BigInteger.valueOf(transferPower(power.longValueExact()));
        BigInteger stored = getPowerExact(), capacity = getMaxPowerExact();
        if (capacity.signum() < 0 || capacity.compareTo(stored) < 0) throw new IllegalStateException("Invalid HE storage");
        BigInteger accepted = power.min(capacity.subtract(stored));
        setPowerExact(stored.add(accepted));
        return power.subtract(accepted);
    }

    default ConnectionPriority getPriority() {
        return ConnectionPriority.NORMAL;
    }
}
