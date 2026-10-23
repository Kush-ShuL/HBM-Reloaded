package com.hbm.machine.turret.jeremy;
import net.neoforged.neoforge.common.ModConfigSpec;
/** Original RunningConfig XFactoryTurret.TURRET_JEREMY_DAMAGE. */
public final class JeremyTurretSettings {
    public static final ModConfigSpec SPEC;public static final ModConfigSpec.DoubleValue DAMAGE;
    static{var b=new ModConfigSpec.Builder();DAMAGE=b.defineInRange(java.util.List.of("damage","jeremy"),50D,0,Float.MAX_VALUE);SPEC=b.build();}
    public static float damage(){return DAMAGE.get().floatValue();}private JeremyTurretSettings(){}
}
