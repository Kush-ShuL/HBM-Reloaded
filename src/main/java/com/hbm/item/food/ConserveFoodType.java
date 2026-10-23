package com.hbm.item.food;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** Exact source EnumFoodType order, including the source's literal 075F (75F) pizza saturation. */
public enum ConserveFoodType {
    BEEF(8,.75F),TUNA(4,.75F),MYSTERY(6,.5F),PASHTET(4,.5F),CHEESE(3,1),
    SLIME(15,5),MILK(5,.25F),ASS(6,.75F),PIZZA(8,75),TUBE(2,.25F),
    TOMATO(4,.5F),ASBESTOS(7,1),BHOLE(10,1),HOTDOGS(5,.75F),LEFTOVERS(1,.1F),
    YOGURT(3,.5F),STEW(5,.5F),CHINESE(6,.1F),OIL(3,1),FIST(6,.75F),SPAM(8,1),
    FRIED(10,.75F),NAPALM(6,1),DIESEL(6,1),KEROSENE(6,1),RECURSION(1,1),BARK(2,1);
    public final int foodLevel;public final float saturation;
    ConserveFoodType(int foodLevel,float saturation){this.foodLevel=foodLevel;this.saturation=saturation;}
    public String id(){return name().toLowerCase(Locale.ROOT);}
    public static List<String> names(){return Arrays.stream(values()).map(ConserveFoodType::id).toList();}
}
