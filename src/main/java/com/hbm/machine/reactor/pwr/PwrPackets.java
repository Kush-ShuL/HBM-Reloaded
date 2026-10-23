package com.hbm.machine.reactor.pwr;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class PwrPackets {
    public record Control(BlockPos position,int target) implements CustomPacketPayload {
        public static final Type<Control> TYPE=new Type<>(ResourceLocation.parse("hbm:pwr_control"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Control> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.position);b.writeInt(p.target);},b->new Control(b.readBlockPos(),b.readInt()));
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public record Marker(BlockPos position,String label) implements CustomPacketPayload {
        public static final Type<Marker> TYPE=new Type<>(ResourceLocation.parse("hbm:pwr_marker"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Marker> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.position);b.writeUtf(p.label);},b->new Marker(b.readBlockPos(),b.readUtf()));
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public static void register(RegisterPayloadHandlersEvent event){var registrar=event.registrar("1");registrar.playToServer(Control.TYPE,Control.CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(!(player.containerMenu instanceof PwrMenu menu)||!menu.position().equals(packet.position)||!menu.stillValid(player)||!player.level().hasChunkAt(packet.position))return;if(player.level().getBlockEntity(packet.position) instanceof PwrControllerBlockEntity core)core.setLevel(packet.target);}));registrar.playToClient(Marker.TYPE,Marker.CODEC,(packet,context)->context.enqueueWork(()->com.hbm.client.reactor.pwr.PwrMarkers.add(packet)));}
    public static void marker(ServerPlayer player,BlockPos pos,String label){PacketDistributor.sendToPlayer(player,new Marker(pos,label));}
    private PwrPackets(){}
}
