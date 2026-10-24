package com.hbm.machine.reactor.icf;

import net.neoforged.neoforge.common.ModConfigSpec;

/** The two actually consumed capacity coefficients of the source ICF laser controller. */
public final class IcfConfig {
    private static ModConfigSpec.IntValue capacitorOption,turboOption;
    public static volatile int capacitorPower=2500000,turboPower=5000000;

    public static void define(ModConfigSpec.Builder builder){
        builder.comment("ICF激光控制器容量系数。总容量为有效电容数量的平方根乘电容系数，加上有效涡轮与电容数量较小者的平方根乘涡轮系数，最后向零截断为整数HE。").push("icfLaser");
        capacitorOption=builder.comment("有效电容数量平方根对应的容量系数；单位：HE；范围：0～2147483647；0仅取消电容项贡献，不影响涡轮项。").defineInRange("capacitorCapacityFactor",2500000,0,Integer.MAX_VALUE);
        turboOption=builder.comment("有效涡轮与电容数量较小者的平方根对应的容量系数；单位：HE；范围：0～2147483647；0仅取消涡轮项贡献，不影响电容项。").defineInRange("turboCapacityFactor",5000000,0,Integer.MAX_VALUE);
        builder.pop();
    }

    public static void apply(){capacitorPower=capacitorOption.get();turboPower=turboOption.get();}
    private IcfConfig(){}
}
