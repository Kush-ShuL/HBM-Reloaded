package com.hbm.machine.reactor;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
/** Original MobConfig switch controlling the actual reactor-meltdown radMark trigger. */
public final class ReactorConfig {
    private static final ModConfigSpec.Builder BUILDER=new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue ELEMENTALS=BUILDER.comment("启用熔毁后的元素生物遭遇；反应堆熔毁时会标记附近玩家。").define("enableElementals",true);
    public static final ModConfigSpec.IntValue ELEMENTAL_DELAY=BUILDER.comment("元素生物遭遇的检查间隔，单位：游戏刻。").defineInRange("elementalDelay",30*60*60,1,Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue ELEMENTAL_CHANCE=BUILDER.comment("遭遇触发概率的分母：每次检查的概率为 1/此值。").defineInRange("elementalChance",2,1,Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue ELEMENTAL_AMOUNT=BUILDER.comment("每次遭遇生成的元素生物数量；第一只为生命值 360 的首领。").defineInRange("elementalAmount",10,0,Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue ELEMENTAL_DISTANCE=BUILDER.comment("预留的元素生物距离参数；当前遭遇距离由袭击距离设置决定。").defineInRange("elementalAttackDistance",32,0,Integer.MAX_VALUE);
    public static final ModConfigSpec SPEC=BUILDER.build();
    private ReactorConfig(){}
    public static void register(ModContainer container){container.registerConfig(ModConfig.Type.COMMON,SPEC,"hbm-reactors.toml");}
}
