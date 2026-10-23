package com.hbm.machine.turret.richard;
import net.neoforged.neoforge.common.ModConfigSpec;
/** The actual RunningConfig wrapper XFactoryTurret.TURRET_RICHARD_DAMAGE, default30F. */
public final class RichardTurretSettings {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue DAMAGE;
    static { var builder=new ModConfigSpec.Builder();DAMAGE=builder.defineInRange(java.util.List.of("damage","richard"),30D,0,Float.MAX_VALUE);SPEC=builder.build(); }
    public static float damage() { return DAMAGE.get().floatValue(); }
    private RichardTurretSettings() {}
}
