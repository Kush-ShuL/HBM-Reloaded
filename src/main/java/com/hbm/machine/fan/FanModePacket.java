package com.hbm.machine.fan;

import java.util.function.IntConsumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Source PlayerInformPacket ID_FAN_MODE=10, replacing the same one-second message on each tool toggle. */
public record FanModePacket(int mode) implements CustomPacketPayload {
    public static final Type<FanModePacket> TYPE=new Type<>(ResourceLocation.parse("hbm:fan_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf,FanModePacket> CODEC=StreamCodec.of((buffer,packet)->buffer.writeVarInt(packet.mode),buffer->new FanModePacket(buffer.readVarInt()));
    public static IntConsumer clientMessage=mode->{};
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    static void register(RegisterPayloadHandlersEvent event) { event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{if(packet.mode>=0&&packet.mode<4)clientMessage.accept(packet.mode);})); }
    static void send(Player player,int mode) { if(player instanceof ServerPlayer server)PacketDistributor.sendToPlayer(server,new FanModePacket(mode)); }
}
