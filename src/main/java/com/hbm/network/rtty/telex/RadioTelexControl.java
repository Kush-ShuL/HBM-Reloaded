package com.hbm.network.rtty.telex;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public record RadioTelexControl(BlockPos position,CompoundTag data) implements CustomPacketPayload {
 public static final Type<RadioTelexControl> TYPE=new Type<>(ResourceLocation.parse("hbm:radio_telex_control"));public static final StreamCodec<RegistryFriendlyByteBuf,RadioTelexControl> CODEC=StreamCodec.of((buf,p)->{buf.writeBlockPos(p.position);buf.writeNbt(p.data);},buf->{var pos=buf.readBlockPos();var data=buf.readNbt();return new RadioTelexControl(pos,data==null?new CompoundTag():data);});public RadioTelexControl{position=position.immutable();data=data.copy();}@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();var pos=packet.position;if(player.distanceToSqr(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5)<256&&player.level().hasChunkAt(pos)&&player.level().getBlockEntity(pos) instanceof RadioTelexEntity telex)telex.control(packet.data);}));}
}
