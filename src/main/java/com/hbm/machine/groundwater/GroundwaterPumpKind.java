package com.hbm.machine.groundwater;

public enum GroundwaterPumpKind {
    STEAM("pump_steam", "tileentity_steam_pump"), ELECTRIC("pump_electric", "tileentity_electric_pump");
    public final String id, legacyEntityId;
    GroundwaterPumpKind(String id,String legacyEntityId){this.id=id;this.legacyEntityId=legacyEntityId;}
    public int production(){var config=GroundwaterPumpSettings.values();return this==STEAM?config.steamSpeed():config.electricSpeed();}
}
