package com.hbm.machine.reactor.iter.mhdt;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Actual MHD full-efficiency plasma threshold in the existing machine-settings spec. */
public final class MhdtConfig {
    private static ModConfigSpec.LongValue minimumPlasmaOption;
    public static volatile long minimumPlasma=5000000L;

    public static void define(ModConfigSpec.Builder builder){
        builder.comment("磁流体汽轮机的全功率等离子体阈值设置。").push("mhdTurbine");
        minimumPlasmaOption=builder.comment("本刻等离子体热量达到此值时不减半发电量，低于时先计算向下取整的1.35倍热量，再将结果整数除以2；同时决定转子速度上限和状态显示；单位：TU/t；范围：0～9223372036854775807；0仅取消阈值减半，冷却条件仍须满足。").defineInRange("minimumPlasma",5000000L,0L,Long.MAX_VALUE);
        builder.pop();
    }

    public static void apply(){minimumPlasma=minimumPlasmaOption.get();}
    private MhdtConfig(){}
}
