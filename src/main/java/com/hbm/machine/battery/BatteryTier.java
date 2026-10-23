package com.hbm.machine.battery;

/** Capacities and artwork from X5846 ModBlocks' five MachineBattery instances. */
public enum BatteryTier {
    POTATO("machine_battery_potato", 10_000L, "battery_potato_front", "battery_potato_top", "battery_potato_side"),
    STANDARD("machine_battery", 1_000_000L, "battery_front_alt", "battery_top", "battery_side_alt"),
    LITHIUM("machine_lithium_battery", 50_000_000L, "battery_lithium_front", "battery_lithium_top", "battery_lithium_side"),
    SCHRABIDIUM("machine_schrabidium_battery", 25_000_000_000L, "battery_schrabidium_front", "battery_schrabidium_top", "battery_schrabidium_side"),
    DINEUTRONIUM("machine_dineutronium_battery", 1_000_000_000_000L, "battery_dineutronium_front", "battery_dineutronium_top", "battery_dineutronium_side");

    private final String id;
    private final long capacity;
    private final String frontTexture;
    private final String topTexture;
    private final String sideTexture;

    BatteryTier(String id, long capacity, String frontTexture, String topTexture, String sideTexture) {
        this.id = id;
        this.capacity = capacity;
        this.frontTexture = frontTexture;
        this.topTexture = topTexture;
        this.sideTexture = sideTexture;
    }

    public String id() { return id; }
    public long capacity() { return capacity; }
    public long receiverSpeed() { return capacity / 200L; }
    public long providerSpeed() { return capacity / 600L; }
    public String frontTexture() { return frontTexture; }
    public String topTexture() { return topTexture; }
    public String sideTexture() { return sideTexture; }
}
