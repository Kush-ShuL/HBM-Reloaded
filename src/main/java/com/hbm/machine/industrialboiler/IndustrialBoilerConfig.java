package com.hbm.machine.industrialboiler;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Industrial heat-boiler options in the single shared machine spec. */
public final class IndustrialBoilerConfig {
    private static ModConfigSpec.IntValue heatCapacityOption;
    private static ModConfigSpec.DoubleValue diffusionOption;
    public static volatile int maxHeat=12800000;
    public static volatile double diffusion=.1D;

    public static void define(ModConfigSpec.Builder builder){
        builder.comment("工业热锅炉的储热和下方热源交换设置。").push("industrialBoiler");
        heatCapacityOption=builder.comment("最大储热量；单位：TU；范围：1～2147483647。").defineInRange("heatCapacity",12800000,1,Integer.MAX_VALUE);
        diffusionOption=builder.comment("每刻从下方热源吸取的热差比例，结果向上取整并受剩余储热空间限制；单位：倍；范围：非负有限数；0停止吸热。").defineInRange("diffusion",.1D,0D,Double.MAX_VALUE);
        builder.pop();
    }

    public static void apply(){
        maxHeat=heatCapacityOption.get();
        diffusion=diffusionOption.get();
    }

    private IndustrialBoilerConfig(){}
}
