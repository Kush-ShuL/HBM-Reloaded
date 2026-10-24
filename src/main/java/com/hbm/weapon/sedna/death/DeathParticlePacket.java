package com.hbm.weapon.sedna.death;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Typed equivalents of the three source AuxParticle payloads. */
public record DeathParticlePacket(Kind kind,int entityId,double x,double y,double z,int count,float scale,float brightness,float force,boolean gib,int gibType,int divider)implements CustomPacketPayload {
    public enum Kind{ASHES,SKELETON,GIBLETS}
    public static final Type<DeathParticlePacket> TYPE=new Type<>(ResourceLocation.parse("hbm:sedna_death_particles"));
    public static final StreamCodec<RegistryFriendlyByteBuf,DeathParticlePacket> CODEC=StreamCodec.of((b,p)->{b.writeEnum(p.kind);b.writeInt(p.entityId);b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);b.writeInt(p.count);b.writeFloat(p.scale);b.writeFloat(p.brightness);b.writeFloat(p.force);b.writeBoolean(p.gib);b.writeInt(p.gibType);b.writeInt(p.divider);},b->new DeathParticlePacket(b.readEnum(Kind.class),b.readInt(),b.readDouble(),b.readDouble(),b.readDouble(),b.readInt(),b.readFloat(),b.readFloat(),b.readFloat(),b.readBoolean(),b.readInt(),b.readInt()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->com.hbm.client.weapon.death.DeathEffectsClient.receive(packet)));}
    public static void ashes(Entity entity,int count,float scale){send(entity,new DeathParticlePacket(Kind.ASHES,entity.getId(),entity.getX(),entity.getY(),entity.getZ(),count,scale,0,0,false,0,0),100);}
    public static void skeleton(Entity entity,float brightness){send(entity,new DeathParticlePacket(Kind.SKELETON,entity.getId(),entity.getX(),entity.getY(),entity.getZ(),0,0,brightness,0,false,0,0),100);}
    public static void skeletonGib(Entity entity,float force){send(entity,new DeathParticlePacket(Kind.SKELETON,entity.getId(),entity.getX(),entity.getY(),entity.getZ(),0,0,1,force,true,0,0),100);}
    public static void giblets(Entity entity,int type,int divider){send(entity,new DeathParticlePacket(Kind.GIBLETS,entity.getId(),entity.getX(),entity.getY()+entity.getBbHeight()*.5,entity.getZ(),0,0,0,0,false,type,divider),150);}
    private static void send(Entity entity,DeathParticlePacket packet,int range){if(entity.level()instanceof ServerLevel level)try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(level.getServer(),packet)){for(var player:level.players())if(player.distanceToSqr(packet.x,packet.y,packet.z)<=range*range)PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}}
}
