package com.hbm.hazard;

import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;

/** Scoped lookup for decoding stored modern ItemStacks, isolated per executing thread. */
public final class HazardContext {
    private static final ThreadLocal<HolderLookup.Provider> LOOKUP=new ThreadLocal<>();
    private HazardContext(){}
    public static HolderLookup.Provider lookup(){return LOOKUP.get();}
    public static <T>T withLookup(HolderLookup.Provider lookup,Supplier<T> action){
        var previous=LOOKUP.get();LOOKUP.set(lookup);
        try{return action.get();}finally{if(previous==null)LOOKUP.remove();else LOOKUP.set(previous);}
    }
}
