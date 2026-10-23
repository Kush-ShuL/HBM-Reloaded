package com.hbm.machine.reactor.watz.fuel;

import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/** All and only the actual Watz-reachable source Function variants, including BobMathUtil.squirt. */
public final class WatzFunction {
    private final int kind;private final double level,verticalOffset;private double divisor=1,offset;
    private WatzFunction(int kind,double level,double verticalOffset){this.kind=kind;this.level=level;this.verticalOffset=verticalOffset;}
    public static WatzFunction linear(double slope){return new WatzFunction(0,slope,0);}
    public static WatzFunction sqrt(double factor){return new WatzFunction(1,factor,0);}
    public static WatzFunction sqrtFalling(double fall){return sqrt(1D/fall).withOff(fall*fall);}
    public static WatzFunction quadratic(double factor,double add){return new WatzFunction(2,factor,add);}
    public WatzFunction withDiv(double value){divisor=value;return this;}public WatzFunction withOff(double value){offset=value;return this;}
    public double effonix(double input){double x=input/divisor+offset;return switch(kind){case 0->x*level;case 1->(Math.sqrt(x+1D/((x+2D)*(x+2D)))-1D/(x+2D))*level;default->x*x*level+verticalOffset;};}
    private String argument(){String x="x";if(divisor!=1)x+=" / "+String.format(Locale.US,"%,.1f",divisor);if(offset!=0)x+=" + "+String.format(Locale.US,"%,.1f",offset);return x;}
    public String label(){return switch(kind){case 0->argument()+" * "+String.format(Locale.US,"%,.1f",level);case 1->"sqrt("+argument()+") * "+String.format(Locale.US,"%,.3f",level);default->argument()+"² * "+String.format(Locale.US,"%,.1f",level)+(verticalOffset!=0?" + "+String.format(Locale.US,"%,.1f",verticalOffset):"");};}
    public Component danger(){return Component.translatable("tooltip.hbm.watz.fuel."+(kind==0?"linear":kind==1?"sqrt":"quadratic")).withStyle(kind==1?ChatFormatting.YELLOW:ChatFormatting.RED);}
}
