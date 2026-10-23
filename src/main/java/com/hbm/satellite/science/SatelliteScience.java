package com.hbm.satellite.science;

import com.hbm.registry.HbmContent;
import com.hbm.hazard.HbmHazards;
import com.hbm.satellite.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

/** Actual X5846 class12: demand-driven flight disks, persistent sensor work and a cargo-blocked FIFO assembler. */
public final class SatelliteScience extends SatelliteBase {
    public static final int COOLDOWN=15*60*20,SENSOR_DURATION=100*60*60*20;
    public long lastScience;
    public int sensorProgress,sensorCount,assemblerCount;
    public double assemblerProgress;
    public final List<AssemblerTask> assemblerTasks=new ArrayList<>();
    @Override public String getType(){return "SCIENCE_PROBE";}
    @Override public boolean hasData(ServerLevel world){
        if(super.hasData(world))return true;
        if(world.getGameTime()>lastScience+COOLDOWN){
            produceData(SatelliteDriveType.DISK_EMPTY,SatelliteDriveType.DISK_FLIGHTDATA);
            lastScience=world.getGameTime();markDirty();
        }
        return super.hasData(world);
    }
    @Override public void onPartDelivered(ServerLevel world,ItemStack part){
        if(part.getItem() instanceof com.hbm.satellite.item.SatelliteItem){
            var kind=com.hbm.satellite.item.SatelliteItem.kind(part);
            if(kind==SatelliteKind.SCIENCE_SENSOR)sensorCount++;
            if(kind==SatelliteKind.SCIENCE_ASSEMBLER)assemblerCount++;
            markDirty();return;
        }
        var recipe=SpaceAssemblerRecipes.getRecipe(world,part);
        if(recipe!=null){assemblerTasks.add(new AssemblerTask(recipe));markDirty();}
        // Source delivery receives the rocket's entire transferred stack; no second shrink/count multiplication here.
    }
    @Override public void onUpdateTick(ServerLevel world){
        if(sensorProgress<SENSOR_DURATION){
            if(sensorCount>0){sensorProgress+=sensorCount;markDirty();}
        }else{
            sensorProgress=0;produceData(SatelliteDriveType.DISK_EMPTY,SatelliteDriveType.DISK_ORBITDATA);markDirty();
        }
        if(assemblerCount>0&&requestableSlots.length<=0&&!assemblerTasks.isEmpty()){
            var task=assemblerTasks.getFirst();
            assemblerProgress+=(double)assemblerCount/task.duration;
            if(assemblerProgress>=1){
                var recipe=SpaceAssemblerRecipes.byName(world,task.recipe);
                if(recipe!=null){
                    requestableSlots=new ItemStack[recipe.outputItems().size()];
                    for(int i=0;i<requestableSlots.length;i++)requestableSlots[i]=SpaceAssemblerRecipes.collapse(recipe.outputItems().get(i));
                }
                assemblerProgress=0;assemblerTasks.removeFirst();markDirty();
            }
        }
    }
    @Override public Component[] getInfo(ServerLevel world){
        int cooldown=(int)(lastScience+COOLDOWN-world.getGameTime()),seconds=cooldown/20;
        return new Component[]{
            Component.translatable("item.satellite.science.name"),
            cooldown<=0?Component.translatable("satellite.ready"):Component.translatable("satellite.cooldown",seconds/60+"m"+seconds%60+"s"),
            sensorCount>0?Component.translatable("satellite.sensors",sensorCount):null,
            sensorCount>0?Component.translatable("satellite.pending",String.format(Locale.US,"%,d",SENSOR_DURATION-sensorProgress)):null,
            driveOutput==SatelliteDriveType.DISK_ORBITDATA?Component.translatable("satellite.data"):null,
            assemblerCount>0?Component.translatable("satellite.assemblers",assemblerCount):null,
            assemblerCount>0?Component.translatable("satellite.progress",(int)Math.round(assemblerProgress*100)+"%"):null,
            assemblerCount>0?Component.translatable("satellite.queue",assemblerTasks.size()):null
        };
    }
    @Override public void writeToNBT(CompoundTag nbt,HolderLookup.Provider lookup){
        super.writeToNBT(nbt,lookup);nbt.putLong("lastScience",lastScience);nbt.putInt("sensorProgress",sensorProgress);nbt.putInt("sensorCount",sensorCount);
        nbt.putInt("assemblerCount",assemblerCount);nbt.putDouble("assemblerProgress",assemblerProgress);nbt.putInt("taskCount",assemblerTasks.size());
        for(int i=0;i<assemblerTasks.size();i++)nbt.putString("task"+i,assemblerTasks.get(i).recipe);
    }
    @Override public void readFromNBT(CompoundTag nbt,HolderLookup.Provider lookup){
        super.readFromNBT(nbt,lookup);lastScience=nbt.getLong("lastScience");sensorProgress=nbt.getInt("sensorProgress");sensorCount=nbt.getInt("sensorCount");assemblerCount=nbt.getInt("assemblerCount");
        // The original writes assemblerProgress but never reads it. Keep that actual reload quirk, not a new persisted progress rule.
        assemblerTasks.clear();for(int i=0;i<nbt.getInt("taskCount");i++)assemblerTasks.add(new AssemblerTask(nbt.getString("task"+i)));
    }
    public final class AssemblerTask {
        public String recipe;public int duration;public ItemStack icon;
        public AssemblerTask(SpaceAssemblerRecipe value){
            if(value!=null){recipe=value.name();duration=value.duration();icon=value.icon();}
            else{recipe="null";duration=1;icon=new ItemStack(HbmContent.item("nothing"));}
        }
        public AssemblerTask(String name){this(SpaceAssemblerRecipes.loadedByName(name));}
    }
}
