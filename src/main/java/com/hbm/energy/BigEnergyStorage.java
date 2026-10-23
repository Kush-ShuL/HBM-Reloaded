package com.hbm.energy;

import java.math.BigInteger;
import java.util.Objects;

/** Arbitrary-precision native HE storage. Legacy long access fails explicitly when out of range. */
public final class BigEnergyStorage implements EnergyProvider, EnergyReceiver {
    private final BigInteger capacity, receiveSpeed, provideSpeed;
    private final ConnectionPriority priority;
    private BigInteger power;
    private boolean loaded = true;

    public BigEnergyStorage(BigInteger capacity) {
        this(capacity, BigInteger.ZERO, capacity, capacity, ConnectionPriority.NORMAL);
    }

    public BigEnergyStorage(BigInteger capacity, BigInteger initialPower, BigInteger receiveSpeed,
                            BigInteger provideSpeed, ConnectionPriority priority) {
        if (capacity.signum() < 0 || receiveSpeed.signum() < 0 || provideSpeed.signum() < 0)
            throw new IllegalArgumentException("Negative HE capacity or speed");
        this.capacity = capacity; this.receiveSpeed = receiveSpeed; this.provideSpeed = provideSpeed;
        this.priority = Objects.requireNonNull(priority);
        setPowerExact(initialPower);
    }

    @Override public BigInteger getPowerExact() { return power; }
    @Override public BigInteger getMaxPowerExact() { return capacity; }
    @Override public BigInteger getReceiverSpeedExact() { return receiveSpeed; }
    @Override public BigInteger getProviderSpeedExact() { return provideSpeed; }
    @Override public ConnectionPriority getPriority() { return priority; }
    @Override public boolean isLoaded() { return loaded; }
    public void setLoaded(boolean loaded) { this.loaded = loaded; }

    @Override public void setPowerExact(BigInteger power) {
        if (power.signum() < 0 || power.compareTo(capacity) > 0) throw new IllegalArgumentException("HE outside storage capacity");
        this.power = power;
    }

    @Override public BigInteger transferPowerExact(BigInteger offered) {
        if (offered.signum() < 0) throw new IllegalArgumentException("Negative HE transfer");
        BigInteger accepted = offered.min(capacity.subtract(power));
        setPowerExact(power.add(accepted));
        return offered.subtract(accepted);
    }

    @Override public void usePowerExact(BigInteger amount) {
        if (amount.signum() < 0 || amount.compareTo(power) > 0) throw new IllegalArgumentException("Unavailable HE requested");
        setPowerExact(power.subtract(amount));
    }

    /** Decimal persistence remains exact for every supported magnitude. */
    public String serializePower() { return power.toString(); }
    public void deserializePower(String saved) { setPowerExact(new BigInteger(saved)); }

    @Override public long getPower() { return power.longValueExact(); }
    @Override public long getMaxPower() { return capacity.longValueExact(); }
    @Override public long getReceiverSpeed() { return receiveSpeed.longValueExact(); }
    @Override public long getProviderSpeed() { return provideSpeed.longValueExact(); }
    @Override public void setPower(long power) { setPowerExact(BigInteger.valueOf(power)); }
    @Override public long transferPower(long offered) { return transferPowerExact(BigInteger.valueOf(offered)).longValueExact(); }
    @Override public void usePower(long amount) { usePowerExact(BigInteger.valueOf(amount)); }
}
