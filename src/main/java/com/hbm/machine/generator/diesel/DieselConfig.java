package com.hbm.machine.generator.diesel;

import com.hbm.fluid.FluidTraits.FuelGrade;
import java.math.BigInteger;
import java.util.EnumMap;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Actual diesel capacities and supported fuel-grade efficiencies in the shared machine spec. */
public final class DieselConfig {
    private static ModConfigSpec.ConfigValue<String> energyCapacityOption;
    private static ModConfigSpec.IntValue fuelCapacityOption;
    private static ModConfigSpec.DoubleValue mediumOption,highOption,aeroOption,gasOption;
    public static volatile BigInteger maxPower=BigInteger.valueOf(50000);
    public static volatile int fuelCapacity=16000;
    public static volatile EnumMap<FuelGrade,Double> efficiency=defaults();

    public static void define(ModConfigSpec.Builder builder){
        builder.comment("柴油发电机的私有储电、燃料槽容量与燃料等级效率设置。").push("dieselGenerator");
        energyCapacityOption=builder.comment("新建机器的储电容量，同时用于电网报告的最大储电量和输出上限；单位：HE；填写带引号的十进制正整数字符串，无固定整数上限；已保存机器的发电与界面容量保留。").define("energyCapacity","50000",DieselConfig::validCapacity);
        fuelCapacityOption=builder.comment("新建机器的燃料入口槽容量；单位：mB；范围：1～2147483647；已保存槽容量保留，重载设置不会重建流体槽。").defineInRange("fuelCapacity",16000,1,Integer.MAX_VALUE);
        builder.comment("每消耗1mB燃料，先将该燃料的燃烧能量整数除以1000，再乘对应等级效率并向零截断；低级燃料始终不可用。单位：倍；范围：非负有限数；效率为0或结果不大于0时，该燃料不能运行引擎。").push("efficiency");
        mediumOption=builder.comment("中级燃料的发电效率；单位：倍；范围：非负有限数；0禁用此等级燃料。").defineInRange("medium",.5D,0D,Double.MAX_VALUE);
        highOption=builder.comment("高级燃料的发电效率；单位：倍；范围：非负有限数；0禁用此等级燃料。").defineInRange("high",.75D,0D,Double.MAX_VALUE);
        aeroOption=builder.comment("航空燃料的发电效率；单位：倍；范围：非负有限数；0禁用此等级燃料。").defineInRange("aviation",.1D,0D,Double.MAX_VALUE);
        gasOption=builder.comment("气体燃料的发电效率；单位：倍；范围：非负有限数；默认0表示此等级燃料不可用。").defineInRange("gas",0D,0D,Double.MAX_VALUE);
        builder.pop();builder.pop();
    }

    public static void apply(){
        var next=defaults();
        next.put(FuelGrade.MEDIUM,mediumOption.get());
        next.put(FuelGrade.HIGH,highOption.get());
        next.put(FuelGrade.AERO,aeroOption.get());
        next.put(FuelGrade.GAS,gasOption.get());
        maxPower=new BigInteger(energyCapacityOption.get());
        fuelCapacity=fuelCapacityOption.get();
        efficiency=next;
    }

    private static boolean validCapacity(Object value){return value instanceof String text&&text.matches("[0-9]+")&&new BigInteger(text).signum()>0;}
    private static EnumMap<FuelGrade,Double> defaults(){
        var values=new EnumMap<FuelGrade,Double>(FuelGrade.class);
        values.put(FuelGrade.MEDIUM,.5D);values.put(FuelGrade.HIGH,.75D);values.put(FuelGrade.AERO,.1D);
        return values;
    }
    private DieselConfig(){}
}
