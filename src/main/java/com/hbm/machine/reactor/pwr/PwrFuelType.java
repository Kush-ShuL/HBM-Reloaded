package com.hbm.machine.reactor.pwr;

import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/** Exact ItemPWRFuel order and FunctionLogarithmic/FunctionSqrt(BobMathUtil.squirt). */
public enum PwrFuelType {
    MEU(5,600,true),HEU233(7.5,25,false),HEU235(7.5,22.5,false),MEN(7.5,675,true),HEN237(7.5,27.5,false),MOX(7.5,600,true),MEP(7.5,675,true),HEP239(10,22.5,false),HEP241(10,25,false),MEA(7.5,750,true),HEA242(10,25,false),HES326(12.5,27.5,false),HES327(12.5,30,false),BFB_AM_MIX(2.5,15,false,250000000),BFB_PU241(2.5,15,false,250000000);
    public final double heatEmission,level,yield;public final boolean logarithmic;
    PwrFuelType(double heat,double level,boolean logarithmic){this(heat,level,logarithmic,1000000000);}
    PwrFuelType(double heat,double level,boolean logarithmic,double yield){
        heatEmission=heat;this.level=level;this.logarithmic=logarithmic;
        // X5846 EnumPWRFuel's constructor never assigns its yield parameter.
        // Preserve the actual initialized lifetime, including both BFB variants.
        this.yield=1000000000D;
    }
    public double effonix(double flux){if(logarithmic)return Math.log10(flux/2500D+1D)*level;return (Math.sqrt(flux+1D/((flux+2D)*(flux+2D)))-1D/(flux+2D))*level;}
    public String label(){return logarithmic?"log10(x / 2,500.0 + 1.0) * "+String.format(Locale.US,"%,.1f",level):"sqrt(x) * "+String.format(Locale.US,"%,.3f",level);}
    public Component danger(){return Component.translatable(logarithmic?"tooltip.hbm.pwr.logarithmic":"tooltip.hbm.pwr.sqrt").withStyle(ChatFormatting.YELLOW);}
    public static PwrFuelType from(int metadata){return values()[Math.abs(metadata%values().length)];}
    public String serializedName(){return name().toLowerCase(Locale.ROOT);}
}
