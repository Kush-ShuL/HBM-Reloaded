package com.hbm.machine.generator.steamengine;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Actual steam-engine parameters in the shared machine-settings spec. */
public final class SteamEngineConfig {
    private static ModConfigSpec.IntValue steamCapacityOption,spentCapacityOption;
    private static ModConfigSpec.DoubleValue efficiencyOption;
    public static volatile int steamCapacity=2000,spentCapacity=20;
    public static volatile double efficiency=.85D;

    public static void define(ModConfigSpec.Builder builder){
        builder.comment("蒸汽机的私有流体槽容量与发电效率设置。").push("steamEngine");
        steamCapacityOption=builder.comment("新建蒸汽机的蒸汽入口槽容量；单位：mB；范围：1～2147483647；已保存槽容量保留。").defineInRange("steamCapacity",2000,1,Integer.MAX_VALUE);
        spentCapacityOption=builder.comment("新建蒸汽机的低压蒸汽出口槽容量；单位：mB；范围：1～2147483647；已保存槽容量保留。").defineInRange("spentSteamCapacity",20,1,Integer.MAX_VALUE);
        efficiencyOption=builder.comment("实际发电量另乘此效率及流体自身的汽轮机冷却效率；单位：倍；范围：非负有限数；0不发电。").defineInRange("efficiency",.85D,0D,Double.MAX_VALUE);
        builder.pop();
    }

    public static void apply(){
        steamCapacity=steamCapacityOption.get();
        spentCapacity=spentCapacityOption.get();
        efficiency=efficiencyOption.get();
    }

    private SteamEngineConfig(){}
}
