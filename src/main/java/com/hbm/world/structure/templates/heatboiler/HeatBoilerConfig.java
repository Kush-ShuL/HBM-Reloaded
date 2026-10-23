package com.hbm.world.structure.templates.heatboiler;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Heat-boiler options in the single shared machine spec. */
public final class HeatBoilerConfig {
    private static ModConfigSpec.IntValue heatCapacityOption;
    private static ModConfigSpec.DoubleValue diffusionOption;
    private static ModConfigSpec.BooleanValue canExplodeOption;
    public static volatile int maxHeat=3200000;
    public static volatile double diffusion=.1D;
    public static volatile boolean canExplode=true;

    public static void define(ModConfigSpec.Builder builder){
        builder.comment("热锅炉的储热、下方热源交换和出口满载保护设置。").push("heatBoiler");
        heatCapacityOption=builder.comment("最大储热量；单位：TU；范围：1～2147483647。").defineInRange("heatCapacity",3200000,1,Integer.MAX_VALUE);
        diffusionOption=builder.comment("每刻从下方热源吸取的热差比例，结果向上取整并受剩余储热空间限制；单位：倍；范围：非负有限数；0停止吸热。").defineInRange("diffusion",.1D,0D,Double.MAX_VALUE);
        canExplodeOption=builder.comment("蒸汽输出空间耗尽时是否允许锅炉爆裂；true允许，false禁止；不影响其它来源的伤害。").define("canExplode",true);
        builder.pop();
    }

    public static void apply(){
        maxHeat=heatCapacityOption.get();
        diffusion=diffusionOption.get();
        canExplode=canExplodeOption.get();
    }

    private HeatBoilerConfig(){}
}
