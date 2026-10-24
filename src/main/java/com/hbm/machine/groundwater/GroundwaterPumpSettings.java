package com.hbm.machine.groundwater;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Actual groundwater-pump altitude, ground scan and water-yield settings. */
public final class GroundwaterPumpSettings {
    public record Values(int groundHeight,int groundDepth,int steamSpeed,int electricSpeed){}
    private static ModConfigSpec.IntValue maxHeightOption,groundDepthOption,steamWaterOption,electricWaterOption;
    private static volatile Values values=new Values(70,4,1000,10000);
    public static Values values(){return values;}

    public static void define(ModConfigSpec.Builder builder){
        builder.comment("蒸汽和电力地下水泵的运行高度、地基检测与产水设置；机器仍要求所在维度具有天光。").push("groundwaterPumps");
        maxHeightOption=builder.comment("泵核心的Y坐标小于或等于此值时才可运行；单位：方块高度；范围：-2147483648～2147483647，允许负Y。").defineInRange("maxHeight",70,Integer.MIN_VALUE,Integer.MAX_VALUE);
        groundDepthOption=builder.comment("向核心下方检测的地基层数，每层检测3×3方块，首层须为完整方块且有效土壤至少占一半；单位：层；范围：0～2147483647；0跳过地基方块扫描，仍要求维度具有天光。").defineInRange("groundDepth",4,0,Integer.MAX_VALUE);
        steamWaterOption=builder.comment("蒸汽泵每次成功运行的产水量；单位：mB/t；范围：0～21474836；新建水槽容量为此值的100倍，重载不重建已有槽；0产水为0，已有槽未满时仍可能消耗蒸汽。").defineInRange("steamWaterPerTick",1000,0,Integer.MAX_VALUE/100);
        electricWaterOption=builder.comment("电力泵每次成功运行的产水量；单位：mB/t；范围：0～21474836；新建水槽容量为此值的100倍，重载不重建已有槽；0产水为0，已有槽未满时仍可能消耗电力。").defineInRange("electricWaterPerTick",10000,0,Integer.MAX_VALUE/100);
        builder.pop();
    }

    public static void apply(){
        values=new Values(maxHeightOption.get(),groundDepthOption.get(),steamWaterOption.get(),electricWaterOption.get());
    }

    private GroundwaterPumpSettings(){}
}
