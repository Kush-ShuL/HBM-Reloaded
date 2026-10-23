package com.hbm.bomb.conventional;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Source ExplosionLarge cloud/radial AuxParticle packets with the original 250-block range. */
public record SmokeBurstEffects(double x,double y,double z,int count,boolean radial) implements CustomPacketPayload {
    public static final Type<SmokeBurstEffects> TYPE=new Type<>(ResourceLocation.parse("hbm:conventional_smoke_burst"));
    public static final StreamCodec<RegistryFriendlyByteBuf,SmokeBurstEffects> CODEC=StreamCodec.of((b,p)->{b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);b.writeInt(p.count);b.writeBoolean(p.radial);},b->new SmokeBurstEffects(b.readDouble(),b.readDouble(),b.readDouble(),b.readInt(),b.readBoolean()));
    @Override public Type<SmokeBurstEffects> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(p,c)->c.enqueueWork(()->com.hbm.client.bomb.conventional.SmokeBurstParticles.receive(p)));}
    public static void cloud(Level level,double x,double y,double z,int count){send(level,new SmokeBurstEffects(x,y,z,count,false));}
    public static void radial(Level level,double x,double y,double z,int count){send(level,new SmokeBurstEffects(x,y,z,count,true));}
    private static void send(Level level,SmokeBurstEffects packet){if(level instanceof ServerLevel server)for(var player:server.players())if(player.distanceToSqr(packet.x,packet.y,packet.z)<=250D*250D)PacketDistributor.sendToPlayer(player,packet);}
}
