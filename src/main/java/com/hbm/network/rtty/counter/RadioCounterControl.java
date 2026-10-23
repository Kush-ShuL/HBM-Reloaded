package com.hbm.network.rtty.counter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public record RadioCounterControl(BlockPos position,CompoundTag data) implements CustomPacketPayload {
 public static final Type<RadioCounterControl> TYPE=new Type<>(ResourceLocation.parse("hbm:radio_counter_control"));public static final StreamCodec<RegistryFriendlyByteBuf,RadioCounterControl> CODEC=StreamCodec.of((buf,p)->{buf.writeBlockPos(p.position);buf.writeNbt(p.data);},buf->{var pos=buf.readBlockPos();var tag=buf.readNbt();return new RadioCounterControl(pos,tag==null?new CompoundTag():tag);});public RadioCounterControl{position=position.immutable();data=data.copy();}@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(player.level().hasChunkAt(packet.position)&&player.level().getBlockEntity(packet.position) instanceof RadioCounterEntity counter&&counter.stillValid(player))counter.control(packet.data);}));}
}
