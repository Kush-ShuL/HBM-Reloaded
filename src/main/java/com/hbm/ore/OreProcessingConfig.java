package com.hbm.ore;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Server-authoritative mining and extraction options; equal yields are the default. */
public final class OreProcessingConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue RAW_YIELD_PERCENT;
    public static final ModConfigSpec.IntValue BLOCK_YIELD_PERCENT;
    public static final ModConfigSpec.BooleanValue APPLY_FORTUNE;
    public static final ModConfigSpec.DoubleValue FORTUNE_BONUS_MULTIPLIER;

    static {
        var builder = new ModConfigSpec.Builder();
        builder.comment("HBM ore extraction / HBM 矿石加工。Defaults deliberately give raw ore and ore blocks equal output.",
                "产率默认相同。若需要原矿比粗矿多产出 10%，显式设置 rawYieldPercent=100、blockYieldPercent=110。",
                "Percentages apply once when an ore/raw ore enters smelting or metal extraction; intermediate dust is not multiplied again.")
                .push("processing");
        RAW_YIELD_PERCENT = builder.comment("粗矿加工产率，相对于基础加工配方，单位：%。100 表示基础产量。")
                .defineInRange("rawYieldPercent", 100, 1, 10000);
        BLOCK_YIELD_PERCENT = builder.comment("原矿方块加工产率，相对于基础加工配方，单位：%。110 表示基础产量的 110%。")
                .defineInRange("blockYieldPercent", 100, 1, 10000);
        builder.pop().push("mining");
        APPLY_FORTUNE = builder.comment("粗矿掉落是否受时运影响，默认关闭。","晶体、宝石、煤等直接材料掉落不受此项影响。")
                .define("applyFortune", false);
        FORTUNE_BONUS_MULTIPLIER = builder.comment("Multiplier for the EXTRA drops granted by Fortune, only when applyFortune=true. / 启用时运后的额外掉落倍率。",
                "1.0 retains the normal Fortune bonus; 0.0 removes the bonus but keeps the base raw ore.")
                .defineInRange("fortuneBonusMultiplier", 1.0, 0.0, 100.0);
        builder.pop();
        SPEC = builder.build();
    }

    private OreProcessingConfig() {}

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, SPEC, "hbm-ores.toml");
    }
}
