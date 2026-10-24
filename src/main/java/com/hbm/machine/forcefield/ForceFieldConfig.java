package com.hbm.machine.forcefield;

import java.math.BigInteger;
import net.neoforged.neoforge.common.ModConfigSpec;

/** All nine actually consumed forcefield settings in the sole machine-settings spec. */
public final class ForceFieldConfig {
    private static ModConfigSpec.ConfigValue<String> energyCapacityOption;
    private static ModConfigSpec.IntValue baseConsumptionOption,radiusConsumptionOption,healthConsumptionOption,baseRadiusOption,radiusUpgradeOption,healthUpgradeOption;
    private static ModConfigSpec.DoubleValue cooldownOption,regenerationOption;
    public static volatile BigInteger maxPower=BigInteger.valueOf(1000000);
    public static volatile int baseCon=1000,radCon=500,shCon=250,baseRadius=16,radUpgrade=16,shUpgrade=50;
    public static volatile double cooldownModif=1D,healthRegenModif=1D;

    public static void define(ModConfigSpec.Builder builder){
        builder.comment("力场发生器的储电、运行耗电、升级增量与恢复设置。").push("forceField");
        energyCapacityOption=builder.comment("最大储电量，也用于接收速率上限和创造电池充电目标；单位：HE；填写带引号的十进制非负整数字符串，无固定整数上限。").define("energyCapacity","1000000",ForceFieldConfig::validCapacity);
        builder.comment("每刻运行耗电由基础值与各类升级件数对应的增量相加。").push("energyPerTick");
        baseConsumptionOption=builder.comment("不含升级的基础运行耗电；单位：HE/t；范围：0～2147483647；0仅免基础部分耗电。").defineInRange("base",1000,0,Integer.MAX_VALUE);
        radiusConsumptionOption=builder.comment("每件范围升级增加的运行耗电；单位：HE/t/件；范围：0～2147483647；0免此增量耗电。").defineInRange("perRadiusUpgrade",500,0,Integer.MAX_VALUE);
        healthConsumptionOption=builder.comment("每件生命升级增加的运行耗电；单位：HE/t/件；范围：0～2147483647；0免此增量耗电。").defineInRange("perHealthUpgrade",250,0,Integer.MAX_VALUE);
        builder.pop();
        builder.comment("力场半径与生命升级增量；基础最大生命仍为100。").push("shape");
        baseRadiusOption=builder.comment("不含范围升级的基础半径；单位：方块；范围：0～2147483647；0只表示基础半径为0。").defineInRange("baseRadius",16,0,Integer.MAX_VALUE);
        radiusUpgradeOption=builder.comment("每件范围升级增加的半径；单位：方块/件；范围：0～2147483647；0不增加半径。").defineInRange("radiusPerUpgrade",16,0,Integer.MAX_VALUE);
        healthUpgradeOption=builder.comment("每件生命升级在基础100之上增加的最大生命；单位：生命值/件；范围：0～2147483647；0不增加最大生命。").defineInRange("healthPerUpgrade",50,0,Integer.MAX_VALUE);
        builder.pop();
        builder.comment("力场崩溃后的冷却与自动生命恢复。").push("recovery");
        cooldownOption=builder.comment("崩溃冷却为100刻加半径乘此倍率，保持浮点计算后向零截断；单位：倍；范围：非负有限数；0仍有基础100刻冷却。").defineInRange("cooldownScale",1D,0D,Double.MAX_VALUE);
        regenerationOption=builder.comment("冷却结束后每刻先将最大生命整数除以100，再乘此倍率加到当前生命并向零截断，最多恢复到最大生命；单位：倍；范围：非负有限数；0仅停止自动生命恢复。").defineInRange("regenerationScale",1D,0D,Double.MAX_VALUE);
        builder.pop();builder.pop();
    }

    public static void apply(){
        maxPower=new BigInteger(energyCapacityOption.get());
        baseCon=baseConsumptionOption.get();radCon=radiusConsumptionOption.get();shCon=healthConsumptionOption.get();
        baseRadius=baseRadiusOption.get();radUpgrade=radiusUpgradeOption.get();shUpgrade=healthUpgradeOption.get();
        cooldownModif=cooldownOption.get();healthRegenModif=regenerationOption.get();
    }

    private static boolean validCapacity(Object value){return value instanceof String text&&text.matches("[0-9]+")&&new BigInteger(text).signum()>=0;}
    private ForceFieldConfig(){}
}
