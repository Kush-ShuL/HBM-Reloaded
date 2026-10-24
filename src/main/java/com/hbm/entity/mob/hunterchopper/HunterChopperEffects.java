package com.hbm.entity.mob.hunterchopper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source AuxParticlePacketNT(type=exhaust,mode=meteor,count10,width1), with original100-block send radius. */
public record HunterChopperEffects(double x,double y,double z)implements CustomPacketPayload {
 public static final Type<HunterChopperEffects> TYPE=new Type<>(ResourceLocation.parse("hbm:hunter_chopper_exhaust"));public static final StreamCodec<RegistryFriendlyByteBuf,HunterChopperEffects> CODEC=StreamCodec.of((b,p)->{b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);},b->new HunterChopperEffects(b.readDouble(),b.readDouble(),b.readDouble()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 static void register(RegisterPayloadHandlersEvent e){e.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->com.hbm.client.entity.hunterchopper.ChopperClientEffects.exhaust(packet.x,packet.y,packet.z)));}
 public static void exhaust(ServerLevel world,double x,double y,double z){var packet=new HunterChopperEffects(x,y,z);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(world.getServer(),packet)){for(var player:world.players())if(player.distanceToSqr(x,y,z)<10000)PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}}
}
