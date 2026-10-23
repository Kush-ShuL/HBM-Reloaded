package com.hbm.machine.coolingtower;

import java.util.EnumMap;
import java.util.Map;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Passive condenser and both tower capacities in the single shared machine spec. */
public final class CoolingTowerConfig {
    private static final Map<CoolingTowerKind,ModConfigSpec.IntValue[]> OPTIONS=new EnumMap<>(CoolingTowerKind.class);
    private static volatile Map<CoolingTowerKind,int[]> SIZES=defaults();

    private static Map<CoolingTowerKind,int[]> defaults(){
        var result=new EnumMap<CoolingTowerKind,int[]>(CoolingTowerKind.class);
        for(var kind:CoolingTowerKind.values())result.put(kind,new int[]{kind.capacity,kind.capacity});
        return Map.copyOf(result);
    }

    public static int capacity(CoolingTowerKind kind,boolean input){
        var sizes=SIZES.get(kind);
        return sizes==null?kind.capacity:sizes[input?0:1];
    }

    public static void define(ModConfigSpec.Builder builder){
        builder.comment("被动冷凝器的私有流体槽容量。").push("condenser");
        defineTanks(builder,CoolingTowerKind.CONDENSER);
        builder.pop();
        builder.comment("冷却塔的私有流体槽容量。").push("coolingTower");
        builder.comment("小型冷却塔。").push("small");
        defineTanks(builder,CoolingTowerKind.SMALL);
        builder.pop();
        builder.comment("大型冷却塔。").push("large");
        defineTanks(builder,CoolingTowerKind.LARGE);
        builder.pop();builder.pop();
    }

    private static void defineTanks(ModConfigSpec.Builder builder,CoolingTowerKind kind){
        var input=builder.comment("新建机器的低压蒸汽入口槽容量；单位：mB；范围：1～2147483647；已保存槽容量保留。").defineInRange("inputCapacity",kind.capacity,1,Integer.MAX_VALUE);
        var output=builder.comment("新建机器的水出口槽容量；单位：mB；范围：1～2147483647；已保存槽容量保留。").defineInRange("outputCapacity",kind.capacity,1,Integer.MAX_VALUE);
        OPTIONS.put(kind,new ModConfigSpec.IntValue[]{input,output});
    }

    public static void apply(){
        var next=new EnumMap<CoolingTowerKind,int[]>(CoolingTowerKind.class);
        for(var kind:CoolingTowerKind.values()){
            var values=OPTIONS.get(kind);
            next.put(kind,new int[]{values[0].get(),values[1].get()});
        }
        SIZES=Map.copyOf(next);
    }

    private CoolingTowerConfig(){}
}
