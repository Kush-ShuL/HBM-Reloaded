package com.hbm.machine.oilfracking;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Actual fracker settings, keeping its inherited source-sized Values interface. */
public final class FrackingConfig {
    public record Values(int power,int consumption,int solution,int delay,int oil,int gasMin,int gasMax,double drain,int bedrockOil,int bedrockGasMin,int bedrockGasMax,int destructionRange){}
    private static ModConfigSpec.IntValue powerOption,consumptionOption,solutionOption,delayOption,oilOption,bedrockOilOption,spreadOption;
    private static ModConfigSpec.ConfigValue<String> gasOption,bedrockGasOption;
    private static ModConfigSpec.DoubleValue drainOption;
    private static volatile Values values=new Values(5000000,5000,10,20,1000,100,500,.02D,100,10,50,75);
    public static Values get(){return values;}

    public static void define(ModConfigSpec.Builder builder){
        builder.comment("压裂塔的储电、基础耗电、压裂液、采样间隔与油岩产出设置。").push("frackingTower");
        powerOption=builder.comment("最大储电量；单位：HE；范围：0～2147483647；降低容量后按机器充电流程限制现有储能。").defineInRange("energyCapacity",5000000,0,Integer.MAX_VALUE);
        consumptionOption=builder.comment("满足运行条件期间每刻的基础耗电，仍由升级调整，并非只在抽采成功时耗电；单位：HE/t；范围：0～2147483647；0免基础耗电。").defineInRange("energyPerTick",5000,0,Integer.MAX_VALUE);
        solutionOption=builder.comment("每次成功抽采普通或基岩油岩消耗的压裂液；单位：mB/次；范围：0～2147483647；不足此量时不能抽采，0免压裂液消耗但仍会改造地表。").defineInRange("solutionPerOperation",10,0,Integer.MAX_VALUE);
        delayOption=builder.comment("钻探与抽采检测的基础间隔，仍由升级调整，实际间隔至少1刻；单位：刻；范围：0～2147483647。").defineInRange("operationInterval",20,0,Integer.MAX_VALUE);
        spreadOption=builder.comment("每次成功抽采后十次地表退化采样的高斯散布宽度，不是最大半径；单位：方块；范围：0～2147483647；0仍在塔核心同一列采样十次，不会关闭地表改造。").defineInRange("surfaceSpread",75,0,Integer.MAX_VALUE);
        builder.comment("普通油岩的每次产出与耗尽概率。").push("oilRock");
        oilOption=builder.comment("每次成功抽采普通油岩产生的石油，超出槽空位的部分舍弃；单位：mB/次；范围：0～2147483647；0不产石油但仍可能耗电、耗压裂液并耗尽油岩。").defineInRange("oilPerOperation",1000,0,Integer.MAX_VALUE);
        gasOption=builder.comment("每次普通油岩产出的天然气均匀随机区间；单位：mB/次；填写带引号的最小值..最大值，两端均包含；端点0～2147483647且最小值不大于最大值，含整数个数不超过2147483647；超出槽空位的部分舍弃。").define("gasPerOperation","100..500",FrackingConfig::validGasRange);
        drainOption=builder.comment("每次成功抽采普通油岩后变为空油岩的概率，独立于本次油气产出；单位：概率；范围：0～1的有限数；0从不耗尽，1每次耗尽；基岩油岩不受此项影响。").defineInRange("depletionChance",.02D,0D,1D);
        builder.pop();
        builder.comment("基岩油岩的每次产出；基岩油岩不会耗尽。").push("bedrockOil");
        bedrockOilOption=builder.comment("每次成功抽采基岩油岩产生的石油，超出槽空位的部分舍弃；单位：mB/次；范围：0～2147483647；0不产石油但仍可能耗电、耗压裂液并改造地表。").defineInRange("oilPerOperation",100,0,Integer.MAX_VALUE);
        bedrockGasOption=builder.comment("每次基岩油岩产出的天然气均匀随机区间；单位：mB/次；填写带引号的最小值..最大值，两端均包含；端点0～2147483647且最小值不大于最大值，含整数个数不超过2147483647；超出槽空位的部分舍弃。").define("gasPerOperation","10..50",FrackingConfig::validGasRange);
        builder.pop();builder.pop();
    }

    public static void apply(){
        var gas=parseGasRange(gasOption.get());var bedrockGas=parseGasRange(bedrockGasOption.get());
        values=new Values(powerOption.get(),consumptionOption.get(),solutionOption.get(),delayOption.get(),oilOption.get(),gas[0],gas[1],drainOption.get(),bedrockOilOption.get(),bedrockGas[0],bedrockGas[1],spreadOption.get());
    }

    private static boolean validGasRange(Object value){
        if(!(value instanceof String text)||!text.matches("[0-9]+\\.\\.[0-9]+"))return false;
        try{var range=parseGasRange(text);return range[0]>=0&&range[1]>=range[0]&&(long)range[1]-range[0]+1<=Integer.MAX_VALUE;}
        catch(NumberFormatException error){return false;}
    }
    private static int[] parseGasRange(String text){var parts=text.split("\\.\\.",-1);return new int[]{Integer.parseInt(parts[0]),Integer.parseInt(parts[1])};}
    private FrackingConfig(){}
}
