package com.hbm.item.medicine;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Original PotionConfig and ServerConfig defaults; numeric potion IDs are registry names now. */
public final class MedicineConfig {
    public enum Sickness { OFF, NORMAL, TERRARIA }
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.EnumValue<Sickness> POTION_SICKNESS;
    public static final ModConfigSpec.BooleanValue ENABLE_MKU, TAINT_TRAILS;
    static {
        var builder=new ModConfigSpec.Builder();
        POTION_SICKNESS=builder.comment("药物不适模式：OFF 关闭，NORMAL 标准时长，TERRARIA 为标准时长的 12 倍。")
                .defineEnum(java.util.List.of("effects","potionSickness"),Sickness.OFF);
        ENABLE_MKU=builder.define(java.util.List.of("effects","mkuEnabled"),true);
        TAINT_TRAILS=builder.define(java.util.List.of("effects","taintTrailsEnabled"),false);
        SPEC=builder.build();
    }
    private MedicineConfig() {}
    static void register(ModContainer container) { container.registerConfig(ModConfig.Type.SERVER,SPEC,"hbm-medicines.toml"); }
}
