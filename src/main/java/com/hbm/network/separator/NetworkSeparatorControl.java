package com.hbm.network.separator;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public record NetworkSeparatorControl(BlockPos position,CompoundTag data) implements CustomPacketPayload {
 public static final Type<NetworkSeparatorControl> TYPE=new Type<>(ResourceLocation.parse("hbm:network_separator_control"));public static final StreamCodec<RegistryFriendlyByteBuf,NetworkSeparatorControl> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.position);b.writeNbt(p.data);},b->{var p=b.readBlockPos();var tag=b.readNbt();return new NetworkSeparatorControl(p,tag==null?new CompoundTag():tag);});public NetworkSeparatorControl{position=position.immutable();data=data.copy();}@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(p,c)->c.enqueueWork(()->{var player=c.player();if(player.level().hasChunkAt(p.position)&&player.level().getBlockEntity(p.position) instanceof NetworkSeparatorEntity separator&&separator.hasPermission(player))separator.control(p.data);}));}
}
