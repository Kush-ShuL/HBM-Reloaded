package com.hbm.machine.poweredcondenser;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Actual powered-condenser capacities and consumption in the shared machine spec. */
public final class PoweredCondenserConfig {
    private static ModConfigSpec.LongValue energyCapacityOption;
    private static ModConfigSpec.IntValue inputCapacityOption,outputCapacityOption,powerConsumptionOption;
    public static volatile long maxPower=10000000L;
    public static volatile int inputTankSizeP=1000000,outputTankSizeP=1000000,powerConsumption=10;

    public static void define(ModConfigSpec.Builder builder){
        builder.comment("大功率冷凝器的私有电力、流体槽容量与耗电设置。").push("poweredCondenser");
        energyCapacityOption=builder.comment("最大储电量；单位：HE；范围：1～9223372036854775807。").defineInRange("energyCapacity",10000000L,1L,Long.MAX_VALUE);
        inputCapacityOption=builder.comment("新建机器的低压蒸汽入口槽容量；单位：mB；范围：1～2147483647；已保存槽容量保留。").defineInRange("inputCapacity",1000000,1,Integer.MAX_VALUE);
        outputCapacityOption=builder.comment("新建机器的水出口槽容量；单位：mB；范围：1～2147483647；已保存槽容量保留。").defineInRange("outputCapacity",1000000,1,Integer.MAX_VALUE);
        powerConsumptionOption=builder.comment("每转化1mB低压蒸汽的额定耗电；单位：HE/mB；范围：0～2147483647；0免耗电。").defineInRange("energyPerMb",10,0,Integer.MAX_VALUE);
        builder.pop();
    }

    public static void apply(){
        maxPower=energyCapacityOption.get();
        inputTankSizeP=inputCapacityOption.get();
        outputTankSizeP=outputCapacityOption.get();
        powerConsumption=powerConsumptionOption.get();
    }

    private PoweredCondenserConfig(){}
}
