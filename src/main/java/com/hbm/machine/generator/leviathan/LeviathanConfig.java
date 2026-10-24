package com.hbm.machine.generator.leviathan;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Actual Leviathan inlet/outlet initialization and turbine conversion efficiency. */
public final class LeviathanConfig {
    private static ModConfigSpec.IntValue inputCapacityOption,outputCapacityOption;
    private static ModConfigSpec.DoubleValue efficiencyOption;
    public static volatile int inputTankSize=1000000000,outputTankSize=1000000000;
    public static volatile double efficiency=.85D;

    public static void define(ModConfigSpec.Builder builder){
        builder.comment("利维坦汽轮机的流体槽初始容量与转化效率设置；每刻取用全部可转化流体，仍受出口剩余空间限制。").push("leviathan");
        inputCapacityOption=builder.comment("新建机器的蒸汽入口槽容量；单位：mB；范围：1～2147483647；已保存槽容量保留，换档不缩放容量，重载不重建槽。").defineInRange("inputCapacity",1000000000,1,Integer.MAX_VALUE);
        outputCapacityOption=builder.comment("新建机器的冷却产物出口槽容量；单位：mB；范围：1～2147483647；已保存槽容量保留，换档不缩放容量，重载不重建槽。").defineInRange("outputCapacity",1000000000,1,Integer.MAX_VALUE);
        efficiencyOption=builder.comment("流体自身的汽轮机冷却效率再乘此倍率；单位：倍；范围：非负有限数；0不转化流体、不产生本刻电量。").defineInRange("efficiency",.85D,0D,Double.MAX_VALUE);
        builder.pop();
    }

    public static void apply(){
        inputTankSize=inputCapacityOption.get();
        outputTankSize=outputCapacityOption.get();
        efficiency=efficiencyOption.get();
    }

    private LeviathanConfig(){}
}
