package com.hbm.door.generic;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Exact original3 animation bytes, everyserver tick within100blocks, no client controls or guessed progress. */
public record GenericDoorStatePacket(BlockPos pos,byte state,byte skin,boolean bounding)implements CustomPacketPayload {
    public static final Type<GenericDoorStatePacket>TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("hbm","generic_door_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf,GenericDoorStatePacket>CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.pos);b.writeByte(p.state);b.writeByte(p.skin);b.writeBoolean(p.bounding);},b->new GenericDoorStatePacket(b.readBlockPos(),b.readByte(),b.readByte(),b.readBoolean()));
    @Override public Type<? extends CustomPacketPayload>type(){return TYPE;}
    static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(p,context)->context.enqueueWork(()->{if(context.player().level().getBlockEntity(p.pos)instanceof GenericDoorBlockEntity door)door.receive(p.state,p.skin,p.bounding);}));}
    static void send(GenericDoorBlockEntity door){if(door.getLevel()instanceof ServerLevel level){var pos=door.getBlockPos();var packet=new GenericDoorStatePacket(pos,door.state,door.skinIndex,door.shouldUseBB);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(level.getServer(),packet)){for(var player:level.players())if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<=10000)PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}}}
}
