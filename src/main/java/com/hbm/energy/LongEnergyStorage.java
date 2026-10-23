package com.hbm.energy;

import java.util.Objects;

/** A reusable long HE buffer; owners choose which network roles to expose. */
public final class LongEnergyStorage implements EnergyProvider, EnergyReceiver {
    private final long capacity;
    private final long receiveSpeed;
    private final long provideSpeed;
    private final ConnectionPriority priority;
    private long power;
    private boolean loaded = true;

    public LongEnergyStorage(long capacity) {
        this(capacity, 0, capacity, capacity, ConnectionPriority.NORMAL);
    }

    public LongEnergyStorage(long capacity, long initialPower, long receiveSpeed, long provideSpeed,
                             ConnectionPriority priority) {
        if (capacity < 0 || receiveSpeed < 0 || provideSpeed < 0) {
            throw new IllegalArgumentException("Negative HE capacity or speed");
        }
        this.capacity = capacity;
        this.receiveSpeed = receiveSpeed;
        this.provideSpeed = provideSpeed;
        this.priority = Objects.requireNonNull(priority);
        setPower(initialPower);
    }

    @Override public long getPower() { return power; }
    @Override public long getMaxPower() { return capacity; }
    @Override public long getReceiverSpeed() { return receiveSpeed; }
    @Override public long getProviderSpeed() { return provideSpeed; }
    @Override public ConnectionPriority getPriority() { return priority; }
    @Override public boolean isLoaded() { return loaded; }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    @Override
    public void setPower(long power) {
        if (power < 0 || power > capacity) throw new IllegalArgumentException("HE outside storage capacity");
        this.power = power;
    }
}
