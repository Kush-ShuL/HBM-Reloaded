package com.hbm.machine.conveyorgrabber;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public record ConveyorGrabberControl(BlockPos position) implements CustomPacketPayload {
    public static final Type<ConveyorGrabberControl> TYPE=new Type<>(ResourceLocation.parse("hbm:crane_grabber_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf,ConveyorGrabberControl> CODEC=StreamCodec.of((buffer,value)->buffer.writeBlockPos(value.position),buffer->new ConveyorGrabberControl(buffer.readBlockPos()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(message,context)->context.enqueueWork(()->{var player=context.player();if(player.level().hasChunkAt(message.position)&&player.level().getBlockEntity(message.position)instanceof ConveyorGrabberEntity grabber){var data=new CompoundTag();data.putBoolean("whitelist",true);grabber.receiveControl(player,data);}}));}
}
