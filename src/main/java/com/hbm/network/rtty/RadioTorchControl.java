package com.hbm.network.rtty;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Original optional p/m/c/m0..15 NBT controls and exact distance<16 permission; no invented menu/container. */
public record RadioTorchControl(BlockPos position,CompoundTag data) implements CustomPacketPayload {
 public static final Type<RadioTorchControl> TYPE=new Type<>(ResourceLocation.parse("hbm:radio_torch_control"));
 public static final StreamCodec<RegistryFriendlyByteBuf,RadioTorchControl> CODEC=StreamCodec.of((buf,value)->{buf.writeBlockPos(value.position);buf.writeNbt(value.data);},buf->{var pos=buf.readBlockPos();var tag=buf.readNbt();return new RadioTorchControl(pos,tag==null?new CompoundTag():tag);});
 public RadioTorchControl{position=position.immutable();data=data.copy();}@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();var pos=packet.position;if(player.distanceToSqr(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5)>=256||!player.level().hasChunkAt(pos))return;if(player.level().getBlockEntity(pos) instanceof RadioTorchEntity radio)radio.control(packet.data);}));}
}
