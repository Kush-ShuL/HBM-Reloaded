package com.hbm.machine.reactor.rbmk.boiler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Genuine rbmksteam visual and independent original100-block TargetPoint. */
public record RbmkBoilerEffects(double x,double y,double z)implements CustomPacketPayload {
    public static final Type<RbmkBoilerEffects> TYPE=new Type<>(ResourceLocation.parse("hbm:rbmk_steam"));public static final StreamCodec<RegistryFriendlyByteBuf,RbmkBoilerEffects> CODEC=StreamCodec.of((b,p)->{b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);},b->new RbmkBoilerEffects(b.readDouble(),b.readDouble(),b.readDouble()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(IEventBus bus){bus.addListener((RegisterPayloadHandlersEvent event)->event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->com.hbm.client.reactor.rbmk.boiler.RbmkBoilerClient.steam(packet))));}
    public static void steam(ServerLevel world,double x,double y,double z,double ox,double oy,double oz){var packet=new RbmkBoilerEffects(x,y,z);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(world.getServer(),packet)){for(var player:world.players())if(player.distanceToSqr(ox,oy,oz)<10000)PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}}
}
