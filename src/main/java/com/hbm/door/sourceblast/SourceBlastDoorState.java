package com.hbm.door.sourceblast;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Actual TEVaultPacket type0: source pre-transition clock pulse plus every-tick state, within250 blocks. */
public record SourceBlastDoorState(BlockPos pos,boolean opening,int state,boolean startClock)implements CustomPacketPayload {
    public static final Type<SourceBlastDoorState> TYPE=new Type<>(ResourceLocation.parse("hbm:source_blast_door"));
    public static final StreamCodec<RegistryFriendlyByteBuf,SourceBlastDoorState> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.pos);b.writeBoolean(p.opening);b.writeInt(p.state);b.writeBoolean(p.startClock);},b->new SourceBlastDoorState(b.readBlockPos(),b.readBoolean(),b.readInt(),b.readBoolean()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(p,context)->context.enqueueWork(()->{if(context.player().level().getBlockEntity(p.pos)instanceof SourceBlastDoorEntity door)door.receive(p.opening,p.state,p.startClock);}));}
    static void send(SourceBlastDoorEntity door,boolean clock){if(door.getLevel()instanceof ServerLevel level){var pos=door.getBlockPos();var packet=new SourceBlastDoorState(pos,door.isOpening,door.state,clock);for(var player:level.players())if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<=62500)PacketDistributor.sendToPlayer(player,packet);}}
}
