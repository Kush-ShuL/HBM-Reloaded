package com.hbm.energy;

import java.math.BigInteger;

public interface EnergyProvider extends EnergyHandler {
    default void usePower(long power) {
        long stored = getPower();
        if (power < 0 || power > stored) throw new IllegalArgumentException("Unavailable HE requested");
        setPower(stored - power);
    }

    default long getProviderSpeed() {
        return getMaxPower();
    }

    default BigInteger getProviderSpeedExact() { return BigInteger.valueOf(getProviderSpeed()); }

    default void usePowerExact(BigInteger power) {
        if (power.signum() < 0 || power.compareTo(getPowerExact()) > 0)
            throw new IllegalArgumentException("Unavailable HE requested");
        if (power.bitLength() < 64) usePower(power.longValueExact());
        else setPowerExact(getPowerExact().subtract(power));
    }
}
