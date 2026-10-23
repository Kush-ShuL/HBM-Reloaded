package com.hbm.bomb.nuclear;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Original AuxParticle plasmablast fields and its 150-block AT distribution radius. */
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD)
public record NuclearPlasmaBlastEffects(double x,double y,double z,float r,float g,float b,float scale,float pitch,float yaw) implements CustomPacketPayload {
 public static final Type<NuclearPlasmaBlastEffects> TYPE=new Type<>(ResourceLocation.parse("hbm:nuclear_plasma_blast"));
 public static final StreamCodec<RegistryFriendlyByteBuf,NuclearPlasmaBlastEffects> CODEC=StreamCodec.of((out,p)->{out.writeDouble(p.x);out.writeDouble(p.y);out.writeDouble(p.z);out.writeFloat(p.r);out.writeFloat(p.g);out.writeFloat(p.b);out.writeFloat(p.scale);out.writeFloat(p.pitch);out.writeFloat(p.yaw);},in->new NuclearPlasmaBlastEffects(in.readDouble(),in.readDouble(),in.readDouble(),in.readFloat(),in.readFloat(),in.readFloat(),in.readFloat(),in.readFloat(),in.readFloat()));
 @Override public Type<NuclearPlasmaBlastEffects> type(){return TYPE;}
 @SubscribeEvent public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->com.hbm.client.bomb.nuclear.NuclearPlasmaBlastParticle.receive(packet)));}
 public static void send(Level level,double x,double y,double z,float r,float g,float b,float scale,float pitch,float yaw){send(level,x,y,z,r,g,b,scale,pitch,yaw,150D);}
 public static void send(Level level,double x,double y,double z,float r,float g,float b,float scale,float pitch,float yaw,double range){sendAtOrigin(level,x,y,z,r,g,b,scale,pitch,yaw,range,x,y,z);}
 public static void sendAtOrigin(Level level,double x,double y,double z,float r,float g,float b,float scale,float pitch,float yaw,double range,double originX,double originY,double originZ){if(level instanceof ServerLevel server){var packet=new NuclearPlasmaBlastEffects(x,y,z,r,g,b,scale,pitch,yaw);for(var player:server.players())if(player.distanceToSqr(originX,originY,originZ)<=range*range)PacketDistributor.sendToPlayer(player,packet);}}
}
