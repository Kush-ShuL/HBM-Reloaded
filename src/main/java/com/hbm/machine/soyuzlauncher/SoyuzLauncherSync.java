package com.hbm.machine.soyuzlauncher;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original250block transport; authoritative transient countdown calibrates client visuals without syncing inventory. */
public record SoyuzLauncherSync(BlockPos pos,CompoundTag data) implements CustomPacketPayload {
    public static final Type<SoyuzLauncherSync> TYPE=new Type<>(SoyuzLauncherContent.id("soyuz_launcher_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf,SoyuzLauncherSync> CODEC=StreamCodec.of((buffer,packet)->{buffer.writeBlockPos(packet.pos);buffer.writeNbt(packet.data);},buffer->new SoyuzLauncherSync(buffer.readBlockPos(),buffer.readNbt()));
    public SoyuzLauncherSync{pos=pos.immutable();data=data.copy();}
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var level=context.player().level();if(level.hasChunkAt(packet.pos)&&level.getBlockEntity(packet.pos) instanceof SoyuzLauncherBlockEntity machine)machine.receiveClient(packet.data);}));}
    static void send(ServerLevel server,SoyuzLauncherBlockEntity machine){var data=machine.clientState();var comparison=data.copy();comparison.remove("countdown");if(comparison.equals(machine.lastSync)&&server.getGameTime()%20!=0)return;machine.lastSync=comparison;var packet=new SoyuzLauncherSync(machine.getBlockPos(),data);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(machine.getBlockPos().getX(),machine.getBlockPos().getY(),machine.getBlockPos().getZ())<250*250)PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}}
}
