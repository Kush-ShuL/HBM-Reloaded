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

/** Source AuxParticle rbmkmush: one unlit30frame billboard, with independent original TargetPoint. */
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD)
public record RbmkMushEffects(double x,double y,double z,float scale) implements CustomPacketPayload {
    public static final Type<RbmkMushEffects> TYPE=new Type<>(ResourceLocation.parse("hbm:rbmk_mush"));
    public static final StreamCodec<RegistryFriendlyByteBuf,RbmkMushEffects> CODEC=StreamCodec.of((b,p)->{b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);b.writeFloat(p.scale);},b->new RbmkMushEffects(b.readDouble(),b.readDouble(),b.readDouble(),b.readFloat()));
    @Override public Type<RbmkMushEffects> type(){return TYPE;}
    @SubscribeEvent public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(p,c)->c.enqueueWork(()->com.hbm.client.bomb.nuclear.RbmkMushParticle.receive(p)));}
    public static void send(Level level,double x,double y,double z,float scale){sendAtOrigin(level,x,y,z,scale,x,y,z);}
    public static void sendAtOrigin(Level level,double x,double y,double z,float scale,double ox,double oy,double oz){if(level instanceof ServerLevel server){var packet=new RbmkMushEffects(x,y,z,scale);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(ox,oy,oz)<=250D*250D)PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}}}
}
