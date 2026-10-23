package com.hbm.weapon.sedna.gun;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Actual server-configurable source values; reloadable suppliers are evaluated through Receiver, never copied constants. */
public final class GunSettings {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ENABLE_GUNS;
    public static final ModConfigSpec.DoubleValue GUN_PEPPERBOX_DAMAGE;
    static {var builder=new ModConfigSpec.Builder();ENABLE_GUNS=builder.comment("启用枪械。").define(java.util.List.of("general","enabled"),true);GUN_PEPPERBOX_DAMAGE=builder.comment("胡椒盒手枪的伤害参数。").defineInRange(java.util.List.of("damage","pepperbox"),5D,-Double.MAX_VALUE,Double.MAX_VALUE);SPEC=builder.build();}
    public static boolean enabled(){return !SPEC.isLoaded()||ENABLE_GUNS.get();}
    public static Float pepperboxDamage(){return SPEC.isLoaded()?GUN_PEPPERBOX_DAMAGE.get().floatValue():5F;}
    private GunSettings(){}
}
