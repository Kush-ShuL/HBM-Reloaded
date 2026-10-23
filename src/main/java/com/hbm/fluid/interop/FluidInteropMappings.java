package com.hbm.fluid.interop;

import com.hbm.fluid.*;
import java.util.*;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/** Explicit identity mapping only: names and common tags never imply compatibility. */
public final class FluidInteropMappings {
    private static final Map<HbmFluidType,Fluid> EXPORTS=new IdentityHashMap<>();
    private static final Map<Fluid,HbmFluidType> IMPORTS=new IdentityHashMap<>();
    static {register(HbmFluids.WATER,Fluids.WATER);register(HbmFluids.WATER,Fluids.FLOWING_WATER);EXPORTS.put(HbmFluids.WATER,Fluids.WATER);
        register(HbmFluids.LAVA,Fluids.LAVA);register(HbmFluids.LAVA,Fluids.FLOWING_LAVA);EXPORTS.put(HbmFluids.LAVA,Fluids.LAVA);}
    public static synchronized void register(HbmFluidType nativeType,Fluid standard){
        Objects.requireNonNull(nativeType);Objects.requireNonNull(standard);
        if(nativeType==HbmFluids.NONE||standard==Fluids.EMPTY)throw new IllegalArgumentException("Empty fluid has no interop identity");
        var previous=IMPORTS.get(standard);if(previous!=null&&previous!=nativeType)throw new IllegalArgumentException("Standard fluid already represents "+previous.getName());
        IMPORTS.put(standard,nativeType);EXPORTS.put(nativeType,standard);
    }
    static synchronized void fallback(HbmFluidType nativeType,Fluid standard){
        IMPORTS.put(standard,nativeType);EXPORTS.putIfAbsent(nativeType,standard);
    }
    public static synchronized Fluid toNeo(HbmFluidType type){return FluidInteropConfig.enabled()?EXPORTS.get(type):null;}
    public static synchronized HbmFluidType fromNeo(Fluid fluid){return FluidInteropConfig.enabled()?IMPORTS.get(fluid):null;}
    public static HbmFluidType fromNeo(net.neoforged.neoforge.fluids.FluidStack stack){
        // Native fluids have neither arbitrary component payloads nor a standard pressure component.
        return stack.isEmpty()||!stack.getComponentsPatch().isEmpty()?null:fromNeo(stack.getFluid());
    }
    public static net.neoforged.neoforge.fluids.FluidStack stack(HbmFluidType type,int amount){
        var fluid=toNeo(type);return fluid==null||amount<=0?net.neoforged.neoforge.fluids.FluidStack.EMPTY:new net.neoforged.neoforge.fluids.FluidStack(fluid,amount);
    }
    private FluidInteropMappings(){}
}
