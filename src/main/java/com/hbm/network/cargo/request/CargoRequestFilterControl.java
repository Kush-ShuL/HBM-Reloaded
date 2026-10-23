package com.hbm.network.cargo.request;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Actual original IControlReceiverFilter slot/stack operation; native server permissions andghost copy only. */
public record CargoRequestFilterControl(BlockPos position,CompoundTag data) implements CustomPacketPayload {
 public static final Type<CargoRequestFilterControl> TYPE=new Type<>(ResourceLocation.parse("hbm:drone_request_filter"));public static final StreamCodec<RegistryFriendlyByteBuf,CargoRequestFilterControl> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.position);b.writeNbt(p.data);},b->{var p=b.readBlockPos();var tag=b.readNbt();return new CargoRequestFilterControl(p,tag==null?new CompoundTag():tag);});public CargoRequestFilterControl{position=position.immutable();data=data.copy();}@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(p,c)->c.enqueueWork(()->{var player=c.player();if(player.level().hasChunkAt(p.position)&&player.level().getBlockEntity(p.position) instanceof DroneRequesterEntity requester&&requester.hasPermission(player))requester.control(p.data);}));}
}
