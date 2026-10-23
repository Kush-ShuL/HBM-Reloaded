package com.hbm.machine.turret.fritz;
import net.neoforged.neoforge.common.ModConfigSpec;
/** XFactoryTurret's actual reloadable ServerConfig damage multiplier, default1. */
public final class FritzTurretSettings {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.DoubleValue DAMAGE;
    static{var builder=new ModConfigSpec.Builder();DAMAGE=builder.defineInRange(java.util.List.of("damage","fritz"),1D,-Double.MAX_VALUE,Double.MAX_VALUE);SPEC=builder.build();}
    public static float damage(){return SPEC.isLoaded()?DAMAGE.get().floatValue():1F;}
    private FritzTurretSettings(){}
}
