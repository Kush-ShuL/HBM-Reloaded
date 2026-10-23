package com.hbm.weapon.sedna.gun.flamer;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Actual three XFactoryFlamer ConfigWrapper defaults, evaluated dynamically through the existing receiver supplier. */
public final class FlamerSettings {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.DoubleValue FLAMER,TOPAZ,DAYBREAKER;
    static {var builder=new ModConfigSpec.Builder();
        FLAMER=builder.defineInRange(java.util.List.of("damage","flamer"),1D,-Double.MAX_VALUE,Double.MAX_VALUE);
        TOPAZ=builder.defineInRange(java.util.List.of("damage","topaz"),1.5D,-Double.MAX_VALUE,Double.MAX_VALUE);
        DAYBREAKER=builder.defineInRange(java.util.List.of("damage","daybreaker"),25D,-Double.MAX_VALUE,Double.MAX_VALUE);SPEC=builder.build();}
    public static Float GUN_FLAMER_DAMAGE(){return SPEC.isLoaded()?FLAMER.get().floatValue():1F;}
    public static Float GUN_TOPAZ_DAMAGE(){return SPEC.isLoaded()?TOPAZ.get().floatValue():1.5F;}
    public static Float GUN_DAYBREAKER_DAMAGE(){return SPEC.isLoaded()?DAYBREAKER.get().floatValue():25F;}
    private FlamerSettings(){}
}
