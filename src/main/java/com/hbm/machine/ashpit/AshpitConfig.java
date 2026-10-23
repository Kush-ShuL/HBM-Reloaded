package com.hbm.machine.ashpit;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Ash production thresholds in the single shared machine-settings spec. */
public final class AshpitConfig {
    private static final String[] KEYS={"wood","coal","misc","fly","soot"};
    private static final String[] LABELS={"木灰","煤灰","杂灰","飞灰","烟灰"};
    private static final int[] DEFAULTS={2000,2000,2000,2000,8000};
    private static final ModConfigSpec.IntValue[] OPTIONS=new ModConfigSpec.IntValue[5];
    static final String[] NAMES={"Wood","Coal","Misc","Fly","Soot"};
    public static volatile int[] THRESHOLDS=DEFAULTS.clone();

    public static void define(ModConfigSpec.Builder builder){
        builder.comment("灰坑的五路产灰阈值；每达到一次阈值可产出一个对应灰粉，需有可用输出槽位。").push("ashpit");
        builder.comment("燃料类累计基础燃烧刻数，飞灰和烟灰累计烟囱接收的烟气量。").push("thresholds");
        for(int i=0;i<KEYS.length;i++){
            String unit=i<3?"燃料基础燃烧刻（未乘燃烧时长倍率）":"mB烟气";
            OPTIONS[i]=builder.comment("产出一个"+LABELS[i]+"所需累计量；单位："+unit+"；范围：1～2147483647。").defineInRange(KEYS[i],DEFAULTS[i],1,Integer.MAX_VALUE);
        }
        builder.pop();builder.pop();
    }

    public static void apply(){
        int[] next=new int[OPTIONS.length];
        for(int i=0;i<next.length;i++)next[i]=OPTIONS[i].get();
        THRESHOLDS=next;
    }

    private AshpitConfig(){}
}
