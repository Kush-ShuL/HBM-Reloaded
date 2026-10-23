package com.hbm.machine.turret;
import net.neoforged.neoforge.common.ModConfigSpec;
/** Reloadable XFactoryTurret/ServerConfig damage wrappers; ammunition still applies its real damage multiplier. */
public final class TurretSettings {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.DoubleValue BROWN,EDWIN;
    static {var b=new ModConfigSpec.Builder();BROWN=b.defineInRange(java.util.List.of("damage","brown"),5D,-Double.MAX_VALUE,Double.MAX_VALUE);EDWIN=b.defineInRange(java.util.List.of("damage","edwin"),5D,-Double.MAX_VALUE,Double.MAX_VALUE);SPEC=b.build();}
    public static float brownDamage(){return SPEC.isLoaded()?BROWN.get().floatValue():5F;}
    public static float edwinDamage(){return SPEC.isLoaded()?EDWIN.get().floatValue():5F;}
    private TurretSettings(){}
}
