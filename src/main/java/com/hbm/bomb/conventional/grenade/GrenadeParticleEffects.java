package com.hbm.bomb.conventional.grenade;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source haze AuxParticle packet, preserving its unshifted 150-block distribution origin. */
public record GrenadeParticleEffects(double x,double y,double z) implements CustomPacketPayload {
 public static final Type<GrenadeParticleEffects> TYPE=new Type<>(ResourceLocation.parse("hbm:grenade_haze"));
 public static final StreamCodec<RegistryFriendlyByteBuf,GrenadeParticleEffects> CODEC=StreamCodec.of((b,p)->{b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);},b->new GrenadeParticleEffects(b.readDouble(),b.readDouble(),b.readDouble()));
 @Override public Type<GrenadeParticleEffects> type(){return TYPE;}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(p,c)->c.enqueueWork(()->com.hbm.client.bomb.conventional.grenade.GrenadeParticleEffects.haze(p.x,p.y,p.z)));}
 public static void sendHaze(Level level,double x,double y,double z,double ox,double oy,double oz){if(level instanceof ServerLevel server)for(var player:server.players())if(player.distanceToSqr(ox,oy,oz)<=22500)PacketDistributor.sendToPlayer(player,new GrenadeParticleEffects(x,y,z));}
}
