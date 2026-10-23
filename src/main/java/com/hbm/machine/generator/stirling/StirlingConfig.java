package com.hbm.machine.generator.stirling;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Actual Stirling heat intake, energy efficiency and overspeed-tolerance settings. */
public final class StirlingConfig {
    private static ModConfigSpec.DoubleValue diffusionOption,efficiencyOption;
    private static ModConfigSpec.IntValue overspeedLimitOption;
    public static volatile double diffusion=.1D,efficiency=.5D;
    public static volatile int overspeedLimit=300;

    public static void define(ModConfigSpec.Builder builder){
        builder.comment("斯特林发电机的吸热、发电效率和连续超速容忍设置。").push("stirling");
        diffusionOption=builder.comment("每刻抽取下方热源储热量的比例，结果截断为整数；单位：倍；范围：非负有限数；0不吸热。").defineInRange("diffusion",.1D,0D,Double.MAX_VALUE);
        efficiencyOption=builder.comment("普通和钢制机型的发电量为本刻热量乘此效率，结果截断为整数；单位：HE/TU；范围：非负有限数；创造机型仍按1HE/TU发电。").defineInRange("efficiency",.5D,0D,Double.MAX_VALUE);
        overspeedLimitOption=builder.comment("普通和钢制机型连续超速超过此刻数后齿轮脱落；单位：刻；范围：0～2147483647；0表示首次超速即脱落，创造机型不受影响。").defineInRange("overspeedLimit",300,0,Integer.MAX_VALUE);
        builder.pop();
    }

    public static void apply(){
        diffusion=diffusionOption.get();
        efficiency=efficiencyOption.get();
        overspeedLimit=overspeedLimitOption.get();
    }

    private StirlingConfig(){}
}
