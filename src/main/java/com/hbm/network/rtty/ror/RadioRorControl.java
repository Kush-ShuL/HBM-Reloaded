package com.hbm.network.rtty.ror;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public record RadioRorControl(BlockPos position,CompoundTag data) implements CustomPacketPayload {
 public static final Type<RadioRorControl> TYPE=new Type<>(ResourceLocation.parse("hbm:radio_ror_control"));public static final StreamCodec<RegistryFriendlyByteBuf,RadioRorControl> CODEC=StreamCodec.of((buf,p)->{buf.writeBlockPos(p.position);buf.writeNbt(p.data);},buf->{var pos=buf.readBlockPos();var tag=buf.readNbt();return new RadioRorControl(pos,tag==null?new CompoundTag():tag);});public RadioRorControl{position=position.immutable();data=data.copy();}@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();var pos=packet.position;if(player.distanceToSqr(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5)<256&&player.level().hasChunkAt(pos)&&player.level().getBlockEntity(pos) instanceof RadioRorEntity radio)radio.control(packet.data);}));}
}
