package com.hbm.machine.turbine;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Actual industrial Mk2 inlet/outlet capacities and turbine-conversion efficiency. */
public final class IndustrialTurbineConfig {
    private static ModConfigSpec.IntValue inputCapacityOption,outputCapacityOption;
    private static ModConfigSpec.DoubleValue efficiencyOption;
    public static volatile int inputTankSize=750000,outputTankSize=3000000;
    public static volatile double efficiency=1D;

    public static void define(ModConfigSpec.Builder builder){
        builder.comment("工业汽轮机的私有流体槽初始容量与转化效率设置；每刻取用可用入口流体的20%仍按机器本身规则计算。").push("industrialTurbine");
        inputCapacityOption=builder.comment("新建机器在普通蒸汽档的入口槽容量；单位：mB；范围：1～2147483647；已保存槽容量保留，换档仍逐档整数除以10、回环乘1000，重载不重建槽。").defineInRange("inputCapacity",750000,1,Integer.MAX_VALUE);
        outputCapacityOption=builder.comment("新建机器在普通蒸汽档的出口槽容量；单位：mB；范围：1～2147483647；已保存槽容量保留，换档仍逐档整数除以10、回环乘1000，重载不重建槽。").defineInRange("outputCapacity",3000000,1,Integer.MAX_VALUE);
        efficiencyOption=builder.comment("流体自身的汽轮机冷却效率再乘此倍率，转化能量进入飞轮；单位：倍；范围：非负有限数；0停止新的流体转化，已有飞轮仍按惯性释放能量。").defineInRange("efficiency",1D,0D,Double.MAX_VALUE);
        builder.pop();
    }

    public static void apply(){
        inputTankSize=inputCapacityOption.get();
        outputTankSize=outputCapacityOption.get();
        efficiency=efficiencyOption.get();
    }

    private IndustrialTurbineConfig(){}
}
