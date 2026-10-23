package com.hbm.machine.turret.rotary;
import net.neoforged.neoforge.common.ModConfigSpec;
/** The two original reloadable ServerConfig damage wrappers. */
public final class RotaryTurretSettings {
    public static final ModConfigSpec SPEC;private static final ModConfigSpec.DoubleValue CHEKHOV,FRIENDLY;
    static{var b=new ModConfigSpec.Builder();CHEKHOV=b.defineInRange(java.util.List.of("damage","chekhov"),10D,-Double.MAX_VALUE,Double.MAX_VALUE);FRIENDLY=b.defineInRange(java.util.List.of("damage","friendly"),5D,-Double.MAX_VALUE,Double.MAX_VALUE);SPEC=b.build();}
    public static float damage(RotaryTurretKind kind){return kind==RotaryTurretKind.CHEKHOV?(SPEC.isLoaded()?CHEKHOV.get().floatValue():10F):(SPEC.isLoaded()?FRIENDLY.get().floatValue():5F);}
    private RotaryTurretSettings(){}
}
