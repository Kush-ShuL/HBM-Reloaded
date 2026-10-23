package com.hbm.satellite.resources;

import com.hbm.advancement.HbmAdvancements;
import com.hbm.satellite.*;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

/** Actual single-use Gerald/Horizons payload; never replaced with a generic nuke. */
public final class SatelliteHorizons extends SatelliteBase {
    public static final String CMD_FIRE="fire",CMD_CANFIRE="settarget";
    boolean used;
    @Override public String getType(){return "PAYLOAD_UNKNOWN";}
    @Override public Component[] getInfo(ServerLevel world){return new Component[]{Component.translatable("item.sat_gerald.name"),Component.translatable(used?"satellite.spent":"satellite.ready")};}
    @Override public void onOrbit(ServerLevel world,double x,double y,double z){super.onOrbit(world,x,y,z);for(var player:world.players())HbmAdvancements.trigger(player,"horizonsStart");}
    // Original overrides intentionally omit super: only used survives; targets/TX/base cargo reset on load.
    @Override public void writeToNBT(CompoundTag tag,HolderLookup.Provider lookup){tag.putBoolean("used",used);}
    @Override public void readFromNBT(CompoundTag tag,HolderLookup.Provider lookup){used=tag.getBoolean("used");}
    @Override public void onCommandImpl(ServerLevel world,String...command){if(command.length==0)return;if(command[0].equals(CMD_FIRE)){theHorizons(world,targetX,targetZ);return;}if(command[0].equals(CMD_CANFIRE))tx=Boolean.toString(!used).toUpperCase(Locale.US);}
    @Override public void onCoordAction(ServerLevel world,Player player,int x,int y,int z){setTarget(x,z);theHorizons(world,x,z);}
    public void theHorizons(ServerLevel world,int x,int z){
        if(used)return;used=true;SatelliteSavedData.getData(world).setDirty();
        var tom=new EntityTom(world);tom.setPos(x+.5,600,z+.5);
        world.getChunk(x>>4,z>>4);world.addFreshEntity(tom);
        for(var player:world.players())HbmAdvancements.trigger(player,"horizonsEnd");
        world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Horizons has been activated.").withStyle(ChatFormatting.RED),false);
    }
}
