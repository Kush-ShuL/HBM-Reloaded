package com.hbm.network.rtty.logic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Exact optional original NBT fields and <16-block control permission. */
public record RadioLogicControl(BlockPos position,CompoundTag data) implements CustomPacketPayload {
 public static final Type<RadioLogicControl> TYPE=new Type<>(ResourceLocation.parse("hbm:radio_logic_control"));public static final StreamCodec<RegistryFriendlyByteBuf,RadioLogicControl> CODEC=StreamCodec.of((buf,value)->{buf.writeBlockPos(value.position);buf.writeNbt(value.data);},buf->{var pos=buf.readBlockPos();var tag=buf.readNbt();return new RadioLogicControl(pos,tag==null?new CompoundTag():tag);});
 public RadioLogicControl{position=position.immutable();data=data.copy();}@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();var pos=packet.position;if(player.distanceToSqr(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5)>=256||!player.level().hasChunkAt(pos))return;if(player.level().getBlockEntity(pos) instanceof RadioLogicEntity logic)logic.control(packet.data);}));}
}
