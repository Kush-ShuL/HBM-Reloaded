package com.hbm.weapon.sedna.gun.legendary;
import net.neoforged.neoforge.common.ModConfigSpec;
/** The three original reloadable ServerConfig wrappers, evaluated through actual receivers. */
public final class LegendaryGunSettings {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.DoubleValue LILMAC,BROKEN,SEXY;
    static{var builder=new ModConfigSpec.Builder();LILMAC=builder.defineInRange(java.util.List.of("damage","lilmac"),30D,-Double.MAX_VALUE,Double.MAX_VALUE);BROKEN=builder.defineInRange(java.util.List.of("damage","broken"),48D,-Double.MAX_VALUE,Double.MAX_VALUE);SEXY=builder.defineInRange(java.util.List.of("damage","sexy"),64D,-Double.MAX_VALUE,Double.MAX_VALUE);SPEC=builder.build();}
    public static Float lilmac(){return SPEC.isLoaded()?LILMAC.get().floatValue():30F;}
    public static Float broken(){return SPEC.isLoaded()?BROKEN.get().floatValue():48F;}
    public static Float sexy(){return SPEC.isLoaded()?SEXY.get().floatValue():64F;}
    private LegendaryGunSettings(){}
}
