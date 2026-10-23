package com.hbm.entity.mob.equipment;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Loaded values are read at each equipment callback; the config spec owns reloads. */
public final class MobEquipmentConfig {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue EQUIPMENT, SKELETON_WEAPONS;
    private static final ModConfigSpec.DoubleValue SOOT_REDUCTION;
    static {
        var builder = new ModConfigSpec.Builder();
        builder.push("equipment");
        EQUIPMENT = builder.comment("允许成年僵尸和骷髅获得额外装备，默认开启。关闭后不进行这组装备抽取。")
                .define("enabled", true);
        builder.pop().push("skeletonWeapons");
        SKELETON_WEAPONS = builder.comment("允许骷髅在装备抽取时选择枪械，默认开启；同时受 equipment.enabled 控制。")
                .define("enabled", true);
        SOOT_REDUCTION = builder.comment("选择骷髅枪械前，从环境煤烟数值减去此值。正数降低用于判定的煤烟，负数提高；单位为煤烟值，默认 0。")
                .defineInRange("sootReduction", 0D, -Double.MAX_VALUE, Double.MAX_VALUE);
        builder.pop();
        SPEC = builder.build();
    }
    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, SPEC, "hbm-mob-equipment-server.toml");
    }
    public static boolean equipmentEnabled() { return !SPEC.isLoaded() || EQUIPMENT.get(); }
    public static boolean skeletonWeaponsEnabled() { return !SPEC.isLoaded() || SKELETON_WEAPONS.get(); }
    public static double sootReduction() { return SPEC.isLoaded() ? SOOT_REDUCTION.get() : 0D; }
    private MobEquipmentConfig() {}
}
