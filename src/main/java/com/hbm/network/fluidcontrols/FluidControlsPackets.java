package com.hbm.network.fluidcontrols;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Only the source close-window control packet; no forged server menu or extra computer interface. */
public record FluidControlsPackets(BlockPos position,CompoundTag data) implements CustomPacketPayload {
 public static final Type<FluidControlsPackets> TYPE=new Type<>(ResourceLocation.parse("hbm:fluid_pump_control"));
 public static final StreamCodec<RegistryFriendlyByteBuf,FluidControlsPackets> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.position);b.writeNbt(p.data);},b->{var pos=b.readBlockPos();var tag=b.readNbt();return new FluidControlsPackets(pos,tag==null?new CompoundTag():tag);});
 public FluidControlsPackets{position=position.immutable();data=data.copy();}@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(player.level().hasChunkAt(packet.position)&&player.level().getBlockEntity(packet.position) instanceof PumpEntity pump&&pump.hasPermission(player))pump.control(packet.data);}));}
}
