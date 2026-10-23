package com.hbm.item.lemonfood;

/** Exact remaining ItemLemon constructor/name/stack parameters exported from ModItems; do not add guessed effects. */
public enum SourceLemonFoodKind {
    LEMON("lemon","lemon",3,0.5F,64),
    MED_IPECAC("med_ipecac","med_ipecac",0,0.0F,64),
    MED_PTSD("med_ptsd","med_ptsd",0,0.0F,64),
    MED_SCHIZOPHRENIA("med_schizophrenia","med_schizophrenia",0,0.0F,64),
    LOOPS("loops","loops",4,0.25F,64),
    LOOP_STEW("loop_stew","loop_stew",10,0.5F,1),
    SPONGEBOB_MACARONI("spongebob_macaroni","spongebob_macaroni",5,1.0F,64),
    FOODITEM("fooditem","fooditem",2,5.0F,64),
    STATIC_SANDWICH("static_sandwich","static_sandwich",6,1.0F,64),
    CHEESE("cheese","cheese",5,0.75F,64),
    QUESADILLA("quesadilla","cheese_quesadilla",8,1.0F,64);
    public final String symbol,name;
    public final int nutrition,maxStack;
    public final float saturation;
    SourceLemonFoodKind(String symbol,String name,int nutrition,float saturation,int maxStack){this.symbol=symbol;this.name=name;this.nutrition=nutrition;this.saturation=saturation;this.maxStack=maxStack;}
}
