package com.hbm.machine.turret.tauon;
import net.neoforged.neoforge.common.ModConfigSpec;
/** The actual RunningConfig wrapper XFactoryTurret.TURRET_TAUON_DAMAGE, default30F. */
public final class TauonTurretSettings {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue DAMAGE;
    static { var builder=new ModConfigSpec.Builder();DAMAGE=builder.defineInRange(java.util.List.of("damage","tauon"),30D,0,Float.MAX_VALUE);SPEC=builder.build(); }
    public static float damage() { return DAMAGE.get().floatValue(); }
    private TauonTurretSettings() {}
}
