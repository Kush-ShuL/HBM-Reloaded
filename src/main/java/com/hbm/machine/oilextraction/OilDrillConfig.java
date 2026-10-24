package com.hbm.machine.oilextraction;

import java.util.EnumMap;
import java.util.Map;
import net.neoforged.neoforge.common.ModConfigSpec;

/** The two real oil-drill variants, applied to the existing source-sized Values interface. */
public final class OilDrillConfig {
    public record Values(int power,int consumption,int delay,int oil,int gasMin,int gasMax,double drain){}
    private record Options(ModConfigSpec.IntValue power,ModConfigSpec.IntValue consumption,ModConfigSpec.IntValue delay,ModConfigSpec.IntValue oil,ModConfigSpec.ConfigValue<String> gas,ModConfigSpec.DoubleValue drain){}
    private static final Map<OilDrillKind,Options> OPTIONS=new EnumMap<>(OilDrillKind.class);
    private static volatile Map<OilDrillKind,Values> values=defaults();
    public static Values get(OilDrillKind kind){return values.get(kind);}

    public static void define(ModConfigSpec.Builder builder){
        builder.comment("井架和抽油机的储电、基础耗电、检测间隔及每次抽采产出设置；两机型独立设置。").push("oilExtraction");
        for(var kind:OilDrillKind.values()){
            builder.comment(kind==OilDrillKind.WELL?"井架设置。":"抽油机设置。").push(kind==OilDrillKind.WELL?"derrick":"pumpjack");
            var power=builder.comment("最大储电量；单位：HE；范围：0～2147483647；降低容量后按机器充电流程限制现有储能。").defineInRange("energyCapacity",kind.power,0,Integer.MAX_VALUE);
            var consumption=builder.comment("满足运行条件期间每刻的基础耗电，仍由速度、节能和超频升级调整，并非只在抽采成功时耗电；单位：HE/t；范围：0～2147483647；0免基础耗电。").defineInRange("energyPerTick",kind.consumption,0,Integer.MAX_VALUE);
            var delay=builder.comment("钻探与抽采检测的基础间隔，仍由升级调整，实际间隔至少1刻；单位：刻；范围：0～2147483647。").defineInRange("operationInterval",kind.delay,0,Integer.MAX_VALUE);
            var oil=builder.comment("每次成功抽采油岩产生的石油，超出槽空位的部分舍弃；单位：mB/次；范围：0～2147483647；0不产石油，仍可能耗电和耗尽油岩。").defineInRange("oilPerOperation",kind.oil,0,Integer.MAX_VALUE);
            var gas=builder.comment("每次成功抽采油岩产生的天然气均匀随机区间；单位：mB/次；填写带引号的最小值..最大值，两端均包含；端点0～2147483647且最小值不大于最大值，包含的整数个数不超过2147483647；超出槽空位的部分舍弃。").define("gasPerOperation",kind.gasMin+".."+kind.gasMax,OilDrillConfig::validGasRange);
            var drain=builder.comment("每次成功抽采后油岩变为空油岩的概率；单位：概率；范围：0～1的有限数；0从不耗尽，1每次耗尽，独立于本次油气产出量。").defineInRange("depletionChance",kind.drain,0D,1D);
            OPTIONS.put(kind,new Options(power,consumption,delay,oil,gas,drain));
            builder.pop();
        }
        builder.pop();
    }

    public static void apply(){
        var next=new EnumMap<OilDrillKind,Values>(OilDrillKind.class);
        for(var kind:OilDrillKind.values()){
            var option=OPTIONS.get(kind);var gas=parseGasRange(option.gas.get());
            next.put(kind,new Values(option.power.get(),option.consumption.get(),option.delay.get(),option.oil.get(),gas[0],gas[1],option.drain.get()));
        }
        values=Map.copyOf(next);
    }

    private static boolean validGasRange(Object value){
        if(!(value instanceof String text)||!text.matches("[0-9]+\\.\\.[0-9]+"))return false;
        try{var range=parseGasRange(text);return range[0]>=0&&range[1]>=range[0]&&(long)range[1]-range[0]+1<=Integer.MAX_VALUE;}
        catch(NumberFormatException error){return false;}
    }
    private static int[] parseGasRange(String text){var parts=text.split("\\.\\.",-1);return new int[]{Integer.parseInt(parts[0]),Integer.parseInt(parts[1])};}
    private static Map<OilDrillKind,Values> defaults(){
        var initial=new EnumMap<OilDrillKind,Values>(OilDrillKind.class);
        for(var kind:OilDrillKind.values())initial.put(kind,new Values(kind.power,kind.consumption,kind.delay,kind.oil,kind.gasMin,kind.gasMax,kind.drain));
        return Map.copyOf(initial);
    }
    private OilDrillConfig(){}
}
