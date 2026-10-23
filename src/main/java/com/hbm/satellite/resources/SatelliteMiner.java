package com.hbm.satellite.resources;

import com.hbm.satellite.*;
import com.hbm.world.structure.loot.LegacyStructureLoot;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

/** Source resource satellites keep their own progress/cargo; selection uses the sole real ItemPool kernel/config. */
public class SatelliteMiner extends SatelliteBase {
    private static final Map<Class<? extends SatelliteMiner>,String> CARGO=new ConcurrentHashMap<>();
    static {registerCargo(SatelliteMiner.class,"POOL_SAT_MINER");}
    public double progress;
    public static final double SPEED=1D/(15*60*20);
    public static void registerCargo(Class<? extends SatelliteMiner> type,String pool){CARGO.put(type,pool);}
    public String getCargo(){return CARGO.get(getClass());}
    public static String getCargoForItem(ItemStack stack){var sat=SatelliteRegistry.createFromItem(stack);return sat instanceof SatelliteMiner miner?miner.getCargo():null;}
    protected String itemKind(){return "miner_astro";}
    @Override public String getType(){return "ASTEROID_MINER";}
    @Override public Component[] getInfo(ServerLevel world){return new Component[]{Component.translatable("item.satellite."+itemKind()+".name"),Component.translatable("satellite.minerprogress",Math.round(progress*100)+"%")};}
    @Override public void onUpdateTick(ServerLevel world){
        if(requestableSlots.length<=0){
            progress+=SPEED;
            if(progress>=1D){
                progress=0D;
                // Original obtains pool before nextInt(6); sample() retains nextInt(weight)+nextInt(1).
                LegacyStructureLoot.requireReady(world,getCargo());
                int amount=10+world.random.nextInt(6);requestableSlots=new ItemStack[amount];
                for(int i=0;i<amount;i++)requestableSlots[i]=LegacyStructureLoot.sample(world,getCargo(),world.random);
                markDirty();
            }
            if(world.getGameTime()%1200==0)markDirty();
        }
    }
    @Override public void writeToNBT(CompoundTag tag,HolderLookup.Provider lookup){super.writeToNBT(tag,lookup);tag.putDouble("progress",progress);}
    @Override public void readFromNBT(CompoundTag tag,HolderLookup.Provider lookup){super.readFromNBT(tag,lookup);progress=tag.getDouble("progress");}
}
