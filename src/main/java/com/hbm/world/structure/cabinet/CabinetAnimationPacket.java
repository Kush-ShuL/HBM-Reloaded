package com.hbm.world.structure.cabinet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source BufPacket timer/playersUsing pair, every tick and only within its original 25-block range. */
public record CabinetAnimationPacket(BlockPos pos,int timer,int users)implements CustomPacketPayload {
 public static final Type<CabinetAnimationPacket> TYPE=new Type<>(FilingCabinetContent.id("file_cabinet_animation"));
 public static final StreamCodec<RegistryFriendlyByteBuf,CabinetAnimationPacket> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.pos);b.writeInt(p.timer);b.writeInt(p.users);},b->new CabinetAnimationPacket(b.readBlockPos(),b.readInt(),b.readInt()));
 @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(p,context)->context.enqueueWork(()->{if(context.player().level().getBlockEntity(p.pos)instanceof FilingCabinetBlockEntity cabinet)cabinet.receiveAnimation(p.timer,p.users);}));}
 static void send(FilingCabinetBlockEntity cabinet,int timer,int users){if(cabinet.getLevel()instanceof ServerLevel level){var pos=cabinet.getBlockPos();var packet=new CabinetAnimationPacket(pos,timer,users);for(var player:level.players())if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<=625)PacketDistributor.sendToPlayer(player,packet);}}
}
