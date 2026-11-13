package com.hbm.satellite.types;

import com.hbm.satellite.*;
import com.hbm.satellite.item.SatelliteItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

/** Upstream orbital missile platform: two missiles per delivery, four stored, 50% interception chance. */
public final class SatelliteASAT extends SatelliteBase {
    public int missilesLoaded;
    @Override public String getType(){return "ORBITAL_MISSILE_PLATFORM";}
    @Override public Component[] getInfo(ServerLevel level){return new Component[]{Component.translatable("item.hbm.satellite_asat"),Component.translatable("satellite.missilesLoaded",missilesLoaded)};}
    @Override public void onPartDelivered(ServerLevel level,ItemStack part){
        if(part.getItem() instanceof SatelliteItem&&SatelliteItem.kind(part)==SatelliteKind.ASAT_AMMO){missilesLoaded=Math.clamp(missilesLoaded+2,0,4);markDirty();}
    }
    @Override public void writeToNBT(CompoundTag tag,HolderLookup.Provider lookup){super.writeToNBT(tag,lookup);tag.putInt("missilesLoaded",missilesLoaded);}
    @Override public void readFromNBT(CompoundTag tag,HolderLookup.Provider lookup){super.readFromNBT(tag,lookup);missilesLoaded=tag.getInt("missilesLoaded");}
    @Override public void onCommandImpl(ServerLevel level,String...cmd){
        if(cmd.length==0)return;
        if(cmd[0].equals("getmissilecount")){tx=Integer.toString(missilesLoaded);return;}
        if(!cmd[0].equals("fire")||cmd.length!=2)return;
        if(missilesLoaded<=0){tx="NO AMMO";return;}
        var data=SatelliteSavedData.getData(level);
        Integer frequency=SatelliteRegistry.frequencyFromTargetId(level,cmd[1]);
        if(frequency!=null&&level.random.nextBoolean()){
            data.remove(frequency);
            com.hbm.satellite.SatelliteInterceptEffect.broadcast(level);
            tx="TARGET DESTROYED";
        }else tx="TARGET MISSED";
        missilesLoaded--;markDirty();
    }
}
