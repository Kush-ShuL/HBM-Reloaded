package com.hbm.energy;

import java.math.BigInteger;

/** Native HE; exact methods are the network API, long methods adapt original fixed-capacity machines. */
public interface EnergyHandler extends EnergyConnector {
    long getPower();

    /** Machine storage owns its bounds, including original final-tick deficits. */
    void setPower(long power);

    long getMaxPower();

    default BigInteger getPowerExact() { return BigInteger.valueOf(getPower()); }
    default BigInteger getMaxPowerExact() { return BigInteger.valueOf(getMaxPower()); }
    default void setPowerExact(BigInteger power) { setPower(power.longValueExact()); }
}
