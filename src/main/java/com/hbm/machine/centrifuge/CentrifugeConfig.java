package com.hbm.machine.centrifuge;

import com.hbm.config.GeneralModes;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Actual solid-centrifuge parameters; recipe mode follows the existing sole GeneralModes. */
public final class CentrifugeConfig {
    private static ModConfigSpec.IntValue capacityOption,progressOption,consumptionOption;
    public static volatile int maxPower=100000,processingSpeed=200,baseConsumption=200;

    public static void define(ModConfigSpec.Builder builder){
        builder.comment("实心离心机的储电、处理进度阈值与基础耗电设置；气体离心机为独立机器。").push("centrifuge");
        capacityOption=builder.comment("最大储电量；单位：HE；范围：0～2147483647；仍按机器原有电池与电网流程处理储能。").defineInRange("energyCapacity",100000,0,Integer.MAX_VALUE);
        progressOption=builder.comment("每次处理所需进度，未升级时每刻增加1，升级后按实际速度增加；达到阈值只完成一次并清零进度，堵输出或停机仍清进度；单位：进度单位；范围：1～2147483647。").defineInRange("progressRequired",200,1,Integer.MAX_VALUE);
        consumptionOption=builder.comment("基础处理扣电，仍按速度、超频与节能升级进行整数计算；单位：HE/t；范围：0～2147483647；按上一刻已有进度扣电，首个进度刻不扣电；0免处理扣电但运行仍要求储能大于0。").defineInRange("energyPerTick",200,0,Integer.MAX_VALUE);
        builder.pop();
    }

    public static void apply(){maxPower=capacityOption.get();processingSpeed=progressOption.get();baseConsumption=consumptionOption.get();}
    public static boolean lbs(){return GeneralModes.lbsm("LBSM_recipeSimpleCentrifuge");}
    private CentrifugeConfig(){}
}
