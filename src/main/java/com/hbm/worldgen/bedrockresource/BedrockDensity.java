package com.hbm.worldgen.bedrockresource;

import com.hbm.fluid.FluidStack;
import com.hbm.fluid.HbmFluids;
import com.hbm.worldgen.deposit.LegacyPerlin2D;
import java.util.Random;
import net.minecraft.ChatFormatting;

/** Original ItemBedrockOreBase density field: independent of world seed, quantized to chunk center. */
public final class BedrockDensity {
    private static final LegacyPerlin2D LEVEL=new LegacyPerlin2D(new Random(2114043),4);
    private static final LegacyPerlin2D[] ORES=new LegacyPerlin2D[BedrockOreType.values().length];
    static{for(var type:BedrockOreType.values())ORES[type.ordinal()]=new LegacyPerlin2D(new Random(2082127+type.ordinal()),4);}
    public static double getOreLevel(int x,int z,BedrockOreType type){
        x=((x>>4)<<4)+8;z=((z>>4)<<4)+8;
        return Math.max(0D,Math.min(2D,Math.abs(LEVEL.sample(x*.01D,z*.01D)*ORES[type.ordinal()].sample(x*.01D,z*.01D))*.05D));
    }
    public static double[] levels(int x,int z){var result=new double[ORES.length];for(var type:BedrockOreType.values())result[type.ordinal()]=getOreLevel(x,z,type);return result;}
    public static double average(double[] levels){double total=0;for(double value:levels)total+=value;return total/BedrockOreType.values().length;}
    public static int getTier(double density){return density>1.5D?4:density>1D?3:density>.75D?2:1;}
    /** This is the existing private HBM fluid type, never an external fluid capability. */
    public static FluidStack getBoreFluid(double density){return density>1.5D?new FluidStack(HbmFluids.SOLVENT,2000):density>1D?new FluidStack(HbmFluids.SULFURIC_ACID,1000):density>.75D?new FluidStack(HbmFluids.WATER,1000):null;}
    public static String translateDensity(double density){return "item.ore_density_scanner."+(density<=.1D?"verypoor":density<=.35D?"poor":density<=.75D?"low":density>=1.9D?"excellent":density>=1.65D?"veryhigh":density>=1.25D?"high":"moderate");}
    public static ChatFormatting getColor(double density){return density<=.1D?ChatFormatting.DARK_RED:density<=.35D?ChatFormatting.RED:density<=.75D?ChatFormatting.GOLD:density>2D?ChatFormatting.LIGHT_PURPLE:density>=1.9D?ChatFormatting.AQUA:density>=1.65D?ChatFormatting.BLUE:density>=1.25D?ChatFormatting.GREEN:ChatFormatting.YELLOW;}
    public static double displayed(double amount){return ((int)(amount*100))/100D;}
    private BedrockDensity(){}
}
