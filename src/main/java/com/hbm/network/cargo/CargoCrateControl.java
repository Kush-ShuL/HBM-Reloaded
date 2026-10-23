package com.hbm.network.cargo;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public record CargoCrateControl(BlockPos position,CompoundTag data) implements CustomPacketPayload {
 public static final Type<CargoCrateControl> TYPE=new Type<>(ResourceLocation.parse("hbm:drone_crate_control"));public static final StreamCodec<RegistryFriendlyByteBuf,CargoCrateControl> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.position);b.writeNbt(p.data);},b->{var p=b.readBlockPos();var n=b.readNbt();return new CargoCrateControl(p,n==null?new CompoundTag():n);});public CargoCrateControl{position=position.immutable();data=data.copy();}@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(p,c)->c.enqueueWork(()->{var player=c.player();if(player.level().hasChunkAt(p.position)&&player.level().getBlockEntity(p.position) instanceof DroneCrateEntity crate&&crate.stillValid(player))crate.control(p.data);}));}
}
