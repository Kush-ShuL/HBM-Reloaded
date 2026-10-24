package com.hbm.bomb.conventional;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original foamSplash packet has no range value: all source particles begin at the same point. */
public record FoamSplashEffects(double x,double y,double z,int count) implements CustomPacketPayload {
    public static final Type<FoamSplashEffects> TYPE=new Type<>(ResourceLocation.parse("hbm:foam_splash"));
    public static final StreamCodec<RegistryFriendlyByteBuf,FoamSplashEffects> CODEC=StreamCodec.of((b,p)->{b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);b.writeInt(p.count);},b->new FoamSplashEffects(b.readDouble(),b.readDouble(),b.readDouble(),b.readInt()));
    @Override public Type<FoamSplashEffects> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(p,c)->c.enqueueWork(()->com.hbm.client.bomb.conventional.FoamSplashParticles.receive(p)));}
    public static void send(Level level,double x,double y,double z,int count){if(level instanceof ServerLevel server){var packet=new FoamSplashEffects(x,y,z,count);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(x,y,z)<=250D*250D)PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}}}
}
