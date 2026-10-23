package com.hbm.machine.foundry.crucible;

import com.hbm.fuel.ModuleBurnTime;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Crucible and firebox settings defined in the single shared machine spec. */
public final class CrucibleConfig {
    // A fixed material block is nine 72-quantum ingots; building the spec needs no material-registry initialization.
    private static final int DEFAULT_CAPACITY=16*9*72;
    private static final String[] KEYS={"logs","wood","coal","lignite","coke","solidFuel","rocketFuel","balefire"};
    private static final String[] LABELS={"原木","木制品","煤","褐煤","焦炭","固体燃料","火箭燃料","妖火燃料"};
    private static final double[] DEFAULT_TIME={1D,1D,1.25D,1.25D,1.25D,1.5D,1.5D,.5D};
    private static final double[] DEFAULT_HEAT={1D,1D,2D,2D,2D,3D,5D,15D};
    private static final ModConfigSpec.DoubleValue[] TIME=new ModConfigSpec.DoubleValue[8];
    private static final ModConfigSpec.DoubleValue[] HEAT=new ModConfigSpec.DoubleValue[8];
    private static ModConfigSpec.IntValue recipeCapacityOption,wasteCapacityOption,processHeatOption,heatCapacityOption;
    private static ModConfigSpec.IntValue fireboxBaseHeatOption,fireboxHeatCapacityOption;
    private static ModConfigSpec.DoubleValue diffusionOption,fireboxBurnTimeOption;

    public static volatile int recipeCapacity=DEFAULT_CAPACITY,wasteCapacity=DEFAULT_CAPACITY;
    public static volatile int processHeat=20000,maxHeat=100000,fireboxBaseHeat=100,fireboxMaxHeat=100000;
    public static volatile double diffusion=.25D,fireboxTimeMultiplier=1D;
    public static volatile ModuleBurnTime BURN=defaults();

    /** Called before the existing shared builder is built; it does not register another spec. */
    public static void define(ModConfigSpec.Builder builder){
        builder.comment("坩埚的材料容量、熔炼热量和热交换设置。材料量单位q：72q为1锭，648q为1块。").push("crucible");
        recipeCapacityOption=builder.comment("配方材料槽容量；单位：q；范围：1～1073741823；默认10368q（16块）。").defineInRange("recipeCapacity",DEFAULT_CAPACITY,1,Integer.MAX_VALUE/2);
        wasteCapacityOption=builder.comment("副产品材料槽容量；单位：q；范围：1～1073741823；默认10368q（16块）。").defineInRange("wasteCapacity",DEFAULT_CAPACITY,1,Integer.MAX_VALUE/2);
        processHeatOption=builder.comment("熔炼一个物品所需累积热量；单位：TU；范围：1～2147483647；不是处理刻数。").defineInRange("processHeat",20000,1,Integer.MAX_VALUE);
        heatCapacityOption=builder.comment("最大储热量，同时决定开始熔炼所需的半满热量阈值；单位：TU；范围：1～2147483647。").defineInRange("heatCapacity",100000,1,Integer.MAX_VALUE);
        diffusionOption=builder.comment("每刻从下方热源吸取的热差比例；单位：倍；范围：非负有限数；0停止吸热，1吸取本刻全部可用热差。").defineInRange("diffusion",.25D,0D,Double.MAX_VALUE);
        builder.pop();

        builder.comment("火箱储热和燃料设置。").push("firebox");
        fireboxBaseHeatOption=builder.comment("燃烧期间每刻基础产热量，另乘对应燃料热值倍率；单位：TU/刻；范围：0～2147483647。").defineInRange("baseHeat",100,0,Integer.MAX_VALUE);
        fireboxBurnTimeOption=builder.comment("所有燃料的额外燃烧时长倍率，在对应燃料时长倍率之后计算；单位：倍；范围：非负有限数；0产生零时长。").defineInRange("burnTimeMultiplier",1D,0D,Double.MAX_VALUE);
        fireboxHeatCapacityOption=builder.comment("火箱最大储热量，储满后暂停燃烧倒计时；单位：TU；范围：1～2147483647。").defineInRange("heatCapacity",100000,1,Integer.MAX_VALUE);
        builder.comment("按燃料类别调整燃烧时长和每刻热值。").push("burnModule");
        builder.comment("燃烧时长倍率；单位：倍；范围：非负有限数。").push("time");
        for(int i=0;i<KEYS.length;i++)TIME[i]=builder.comment(LABELS[i]+"的燃烧时长倍率；单位：倍；范围：非负有限数。").defineInRange(KEYS[i],DEFAULT_TIME[i],0D,Double.MAX_VALUE);
        builder.pop();
        builder.comment("每刻热值倍率；单位：倍；范围：非负有限数。").push("heat");
        for(int i=0;i<KEYS.length;i++)HEAT[i]=builder.comment(LABELS[i]+"的每刻热值倍率；单位：倍；范围：非负有限数。").defineInRange(KEYS[i],DEFAULT_HEAT[i],0D,Double.MAX_VALUE);
        builder.pop();builder.pop();builder.pop();
    }

    /** Existing config loading/reloading calls this once; active fuel and machine NBT are retained. */
    public static void apply(){
        recipeCapacity=recipeCapacityOption.get();
        wasteCapacity=wasteCapacityOption.get();
        processHeat=processHeatOption.get();
        maxHeat=heatCapacityOption.get();
        diffusion=diffusionOption.get();
        fireboxBaseHeat=fireboxBaseHeatOption.get();
        fireboxTimeMultiplier=fireboxBurnTimeOption.get();
        fireboxMaxHeat=fireboxHeatCapacityOption.get();
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

    private CrucibleConfig(){}
}
