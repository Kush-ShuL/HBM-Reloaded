package com.hbm.world.structure.templates.radio;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** The original optional channel/isOn NBT controls, with its exact distance<16 permission. */
public record RadioReceiverControl(BlockPos position,boolean channelChange,String channel,boolean isOn) implements CustomPacketPayload {
    public static final Type<RadioReceiverControl> TYPE=new Type<>(ResourceLocation.parse("hbm:radio_receiver_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf,RadioReceiverControl> CODEC=StreamCodec.of((buffer,value)->{buffer.writeBlockPos(value.position);buffer.writeBoolean(value.channelChange);if(value.channelChange)buffer.writeUtf(value.channel);else buffer.writeBoolean(value.isOn);},buffer->{var pos=buffer.readBlockPos();boolean channel=buffer.readBoolean();return new RadioReceiverControl(pos,channel,channel?buffer.readUtf():"",!channel&&buffer.readBoolean());});
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();var pos=packet.position;if(player.distanceToSqr(pos.getX()+.5D,pos.getY()+.5D,pos.getZ()+.5D)>=256D||!player.level().hasChunkAt(pos))return;if(player.level().getBlockEntity(pos) instanceof RadioReceiverBlockEntity radio){if(packet.channelChange)radio.channel=packet.channel;else radio.isOn=packet.isOn;radio.setChanged();}}));}
}
