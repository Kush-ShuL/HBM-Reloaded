package com.hbm.bomb.conventional;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original ExplosionCreator values, including its signed-byte cloud/debris parameters and retry narrowing. */
public record LargeConventionalEffects(double x,double y,double z,int clouds,float scale,float speed,float wave,int debris,int debrisSize,int retries,float velocity,float deviation,float vertical,float soundRange) implements CustomPacketPayload {
    public static final Type<LargeConventionalEffects> TYPE=new Type<>(ResourceLocation.parse("hbm:conventional_large_explosion"));
    public static final StreamCodec<RegistryFriendlyByteBuf,LargeConventionalEffects> CODEC=StreamCodec.of((b,p)->{b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);b.writeByte(p.clouds);b.writeFloat(p.scale);b.writeFloat(p.speed);b.writeFloat(p.wave);b.writeByte(p.debris);b.writeByte(p.debrisSize);b.writeShort((byte)p.retries);b.writeFloat(p.velocity);b.writeFloat(p.deviation);b.writeFloat(p.vertical);b.writeFloat(p.soundRange);},b->new LargeConventionalEffects(b.readDouble(),b.readDouble(),b.readDouble(),b.readByte(),b.readFloat(),b.readFloat(),b.readFloat(),b.readByte(),b.readByte(),b.readShort(),b.readFloat(),b.readFloat(),b.readFloat(),b.readFloat()));
    @Override public Type<LargeConventionalEffects> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(p,c)->c.enqueueWork(()->com.hbm.client.bomb.conventional.LargeExplosionEffects.receive(p)));}
    public static void small(Level level,double x,double y,double z){send(level,new LargeConventionalEffects(x,y,z,10,2,.5F,25,5,8,20,.75F,1,-2,150));}
    public static void standard(Level level,double x,double y,double z){send(level,new LargeConventionalEffects(x,y,z,15,5,1,45,10,16,50,1,3,-2,200));}
    public static void large(Level level,double x,double y,double z){send(level,new LargeConventionalEffects(x,y,z,30,6.5F,2,65,25,16,50,1.25F,3,-2,350));}
    private static void send(Level level,LargeConventionalEffects packet){if(level instanceof ServerLevel server){double radius=Math.max(300,(int)packet.soundRange);for(var player:server.players())if(player.distanceToSqr(packet.x,packet.y,packet.z)<=radius*radius)PacketDistributor.sendToPlayer(player,packet);}}
}
