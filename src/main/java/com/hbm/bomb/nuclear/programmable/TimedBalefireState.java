package com.hbm.bomb.nuclear.programmable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.*;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original networkPackNT(250): timer/started/loaded synchronize independently of opening the menu. */
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD)
public record TimedBalefireState(BlockPos pos,int timer,boolean started,boolean loaded) implements CustomPacketPayload {
 public static final Type<TimedBalefireState> TYPE=new Type<>(ResourceLocation.parse("hbm:fstbmb_state"));
 public static final StreamCodec<RegistryFriendlyByteBuf,TimedBalefireState> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.pos);b.writeInt(p.timer);b.writeBoolean(p.started);b.writeBoolean(p.loaded);},b->new TimedBalefireState(b.readBlockPos(),b.readInt(),b.readBoolean(),b.readBoolean()));
 @Override public Type<TimedBalefireState> type(){return TYPE;}
 @SubscribeEvent public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(p,c)->c.enqueueWork(()->{var level=c.player().level();if(level.getBlockEntity(p.pos) instanceof TimedBalefireBlockEntity bomb){bomb.timer=p.timer;bomb.started=p.started;bomb.loaded=p.loaded;}}));}
 public static void send(TimedBalefireBlockEntity bomb){if(bomb.getLevel() instanceof ServerLevel level){var pos=bomb.getBlockPos();var packet=new TimedBalefireState(pos,bomb.timer,bomb.started,bomb.loaded);for(var player:level.players())if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<=250D*250D)PacketDistributor.sendToPlayer(player,packet);}}
}
