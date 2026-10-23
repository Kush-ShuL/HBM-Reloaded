package com.hbm.satellite.types;
import com.hbm.satellite.*;import api.hbm.redstoneoverradio.IRORInteractive;import net.minecraft.server.level.ServerLevel;import net.minecraft.network.chat.Component;import java.util.*;
public final class SatelliteResonator extends SatelliteBase {
 @Override public String getType(){return "XEN_RELAY";}
 @Override public Component[] getInfo(ServerLevel world){return new Component[]{Component.translatable("item.satellite.xenium_resonator.name")};}

 @Override public void onCoordAction(ServerLevel world,net.minecraft.world.entity.player.Player player,int x,int y,int z){if(!(player instanceof net.minecraft.server.level.ServerPlayer actual))return;world.playSound(null,actual.getX(),actual.getY(),actual.getZ(),net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT,net.minecraft.sounds.SoundSource.PLAYERS,1F,1F);actual.stopRiding();world.getChunk(x>>4,z>>4);if(y<0)y=world.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,x,z);actual.connection.teleport(x+.5D,y,z+.5D,actual.getYRot(),actual.getXRot());world.playSound(null,actual.getX(),actual.getY(),actual.getZ(),net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT,net.minecraft.sounds.SoundSource.PLAYERS,1F,1F);}

}
