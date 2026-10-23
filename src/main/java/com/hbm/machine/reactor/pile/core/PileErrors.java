package com.hbm.machine.reactor.pile.core;
import net.minecraft.core.BlockPos;import net.minecraft.server.level.ServerPlayer;import net.minecraft.world.entity.player.Player;import net.minecraft.world.level.Level;
public final class PileErrors{public static void send(Level level,int x,int y,int z,String key,Player player){if(player instanceof ServerPlayer server)com.hbm.machine.reactor.pwr.PwrPackets.marker(server,new BlockPos(x,y,z),"error.hbm.pile."+key);}private PileErrors(){}}
