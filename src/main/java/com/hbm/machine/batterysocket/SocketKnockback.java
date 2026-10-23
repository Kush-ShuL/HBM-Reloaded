package com.hbm.machine.batterysocket;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original ExplosionKnockbackPacket's three additive float components. */
public record SocketKnockback(float x,float y,float z) implements CustomPacketPayload {
    public static final Type<SocketKnockback> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("hbm","socket_knockback"));
    public static final StreamCodec<ByteBuf,SocketKnockback> CODEC=StreamCodec.of((buffer,value)->{buffer.writeFloat(value.x);buffer.writeFloat(value.y);buffer.writeFloat(value.z);},buffer->new SocketKnockback(buffer.readFloat(),buffer.readFloat(),buffer.readFloat()));
    @Override public Type<SocketKnockback> type() {return TYPE;}
    static void register(RegisterPayloadHandlersEvent event) {event.registrar("1").playToClient(TYPE,CODEC,(value,context)->context.enqueueWork(()->{var player=context.player();player.setDeltaMovement(player.getDeltaMovement().add(value.x,value.y,value.z));}));}
}
