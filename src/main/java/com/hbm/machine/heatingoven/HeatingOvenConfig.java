package com.hbm.machine.heatingoven;

import com.hbm.fuel.ModuleBurnTime;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Heating-oven settings defined in the single shared machine spec. */
public final class HeatingOvenConfig {
    private static final String[] KEYS={"logs","wood","coal","lignite","coke","solidFuel","rocketFuel","balefire"};
    private static final String[] LABELS={"原木","木制品","煤","褐煤","焦炭","固体燃料","火箭燃料","妖火燃料"};
    private static final double[] DEFAULT_TIME={1D,1D,1.25D,1.25D,1.25D,1.5D,1.5D,.5D};
    private static final double[] DEFAULT_HEAT={1D,1D,2D,2D,2D,3D,5D,15D};
    private static final ModConfigSpec.DoubleValue[] TIME=new ModConfigSpec.DoubleValue[8];
    private static final ModConfigSpec.DoubleValue[] HEAT=new ModConfigSpec.DoubleValue[8];
    private static ModConfigSpec.IntValue baseHeatOption,heatCapacityOption;
    private static ModConfigSpec.DoubleValue burnTimeOption,heatEfficiencyOption;

    public static volatile int baseHeat=500,maxHeat=500000;
    public static volatile double timeMultiplier=.125D,heatEfficiency=.5D;
    public static volatile ModuleBurnTime BURN=defaults();

    /** Extends the existing builder without registering another config or another fuel backend. */
    public static void define(ModConfigSpec.Builder builder){
        builder.comment("加热炉的燃烧、储热和下方热源利用设置。").push("heatingOven");
        baseHeatOption=builder.comment("燃烧期间每刻基础产热量，另乘对应燃料热值倍率；单位：TU/刻；范围：0～2147483647。").defineInRange("baseHeat",500,0,Integer.MAX_VALUE);
        burnTimeOption=builder.comment("所有燃料的额外燃烧时长倍率，在对应燃料时长倍率之后计算；单位：倍；范围：非负有限数；0产生零时长。").defineInRange("burnTimeMultiplier",.125D,0D,Double.MAX_VALUE);
        heatEfficiencyOption=builder.comment("从下方热源扣除的热量按此倍率计入本炉，小数截断；单位：倍；范围：非负有限数；0不获得热量，1为等量吸热。").defineInRange("heatPullEfficiency",.5D,0D,Double.MAX_VALUE);
        heatCapacityOption=builder.comment("最大储热量；储满后暂停燃烧倒计时，并限制从下方热源提取的热量；单位：TU；范围：1～2147483647。").defineInRange("heatCapacity",500000,1,Integer.MAX_VALUE);
        builder.comment("按燃料类别调整燃烧时长和每刻热值。").push("burnModule");
        builder.comment("燃烧时长倍率；单位：倍；范围：非负有限数。").push("time");
        for(int i=0;i<KEYS.length;i++)TIME[i]=builder.comment(LABELS[i]+"的燃烧时长倍率；单位：倍；范围：非负有限数。").defineInRange(KEYS[i],DEFAULT_TIME[i],0D,Double.MAX_VALUE);
        builder.pop();
        builder.comment("每刻热值倍率；单位：倍；范围：非负有限数。").push("heat");
        for(int i=0;i<KEYS.length;i++)HEAT[i]=builder.comment(LABELS[i]+"的每刻热值倍率；单位：倍；范围：非负有限数。").defineInRange(KEYS[i],DEFAULT_HEAT[i],0D,Double.MAX_VALUE);
        builder.pop();builder.pop();builder.pop();
    }

    /** Invoked by the existing shared config event; running fuel retains its captured time and heat. */
    public static void apply(){
        baseHeat=baseHeatOption.get();
        timeMultiplier=burnTimeOption.get();
        heatEfficiency=heatEfficiencyOption.get();
        maxHeat=heatCapacityOption.get();
        var next=defaults();
        next.setLogTimeMod(TIME[0].get()).setLogHeatMod(HEAT[0].get());
        next.setWoodTimeMod(TIME[1].get()).setWoodHeatMod(HEAT[1].get());
        next.setCoalTimeMod(TIME[2].get()).setCoalHeatMod(HEAT[2].get());
        next.setLigniteTimeMod(TIME[3].get()).setLigniteHeatMod(HEAT[3].get());
        next.setCokeTimeMod(TIME[4].get()).setCokeHeatMod(HEAT[4].get());
        next.setSolidTimeMod(TIME[5].get()).setSolidHeatMod(HEAT[5].get());
        next.setRocketTimeMod(TIME[6].get()).setRocketHeatMod(HEAT[6].get());
        next.setBalefireTimeMod(TIME[7].get()).setBalefireHeatMod(HEAT[7].get());
        BURN=next;
    }

    private static ModuleBurnTime defaults(){return new ModuleBurnTime()
            .setLigniteTimeMod(1.25).setCoalTimeMod(1.25).setCokeTimeMod(1.25)
            .setSolidTimeMod(1.5).setRocketTimeMod(1.5).setBalefireTimeMod(.5)
            .setLigniteHeatMod(2).setCoalHeatMod(2).setCokeHeatMod(2)
            .setSolidHeatMod(3).setRocketHeatMod(5).setBalefireHeatMod(15);}

    private HeatingOvenConfig(){}
}
