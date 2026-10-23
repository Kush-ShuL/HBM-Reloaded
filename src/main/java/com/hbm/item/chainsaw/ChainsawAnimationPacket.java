package com.hbm.item.chainsaw;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original HbmAnimationPacket ToolAnimation.SWING on rail0; the existing animation backend remains unique. */
public record ChainsawAnimationPacket() implements CustomPacketPayload {
    public static Runnable CLIENT=()->{};
    public static final Type<ChainsawAnimationPacket> TYPE=new Type<>(ResourceLocation.parse("hbm:chainsaw_tool_animation"));
    public static final StreamCodec<RegistryFriendlyByteBuf,ChainsawAnimationPacket> CODEC=StreamCodec.unit(new ChainsawAnimationPacket());
    @Override public Type<ChainsawAnimationPacket> type() { return TYPE; }
    public static void send(ServerPlayer player) { PacketDistributor.sendToPlayer(player,new ChainsawAnimationPacket()); }
    public static void register(RegisterPayloadHandlersEvent event) { event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(CLIENT)); }
}
