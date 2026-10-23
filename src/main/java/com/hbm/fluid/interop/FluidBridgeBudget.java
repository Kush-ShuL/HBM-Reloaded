package com.hbm.fluid.interop;

import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import java.util.*;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Optional external and native graph operations share the source per-type/per-pressure rates. */
public final class FluidBridgeBudget {
    private record Key(HbmFluidType type,int pressure,boolean input){}
    private static final class Tick{long time;final Map<Key,Long> spent=new HashMap<>();Tick(long time){this.time=time;}}
    private static final Map<FluidUser,Tick> OWNERS=new WeakHashMap<>();
    private static synchronized long remaining(FluidUser owner,HbmFluidType type,int pressure,boolean input,long maximum){
        if(!FluidInteropConfig.enabled())return maximum;maximum=Math.max(maximum,0);
        if(!(owner instanceof BlockEntity block)||block.getLevel()==null)return maximum;
        long now=block.getLevel().getGameTime();var tick=OWNERS.computeIfAbsent(owner,key->new Tick(now));if(tick.time!=now){tick.time=now;tick.spent.clear();}
        return Math.max(0,maximum-tick.spent.getOrDefault(new Key(type,pressure,input),0L));
    }
    private static synchronized void used(FluidUser owner,HbmFluidType type,int pressure,boolean input,long amount){
        if(!FluidInteropConfig.enabled()||amount<=0||!(owner instanceof BlockEntity block)||block.getLevel()==null)return;
        long now=block.getLevel().getGameTime();var tick=OWNERS.computeIfAbsent(owner,key->new Tick(now));if(tick.time!=now){tick.time=now;tick.spent.clear();}
        tick.spent.merge(new Key(type,pressure,input),amount,(a,b)->Long.MAX_VALUE-a<b?Long.MAX_VALUE:a+b);
    }
    public static long receiving(FluidReceiver receiver,HbmFluidType type,int pressure){return remaining(receiver,type,pressure,true,receiver.getReceiverSpeed(type,pressure));}
    public static long providing(FluidProvider provider,HbmFluidType type,int pressure){return remaining(provider,type,pressure,false,provider.getProviderSpeed(type,pressure));}
    public static void received(FluidReceiver receiver,HbmFluidType type,int pressure,long amount){used(receiver,type,pressure,true,amount);}
    public static void provided(FluidProvider provider,HbmFluidType type,int pressure,long amount){used(provider,type,pressure,false,amount);}
    private FluidBridgeBudget(){}
}
