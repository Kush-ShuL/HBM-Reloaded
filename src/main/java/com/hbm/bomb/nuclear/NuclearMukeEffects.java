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

/** Original muke/tinytot/bf AuxParticle composition, keeping effect and distribution origins independent. */
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD)
public record NuclearMukeEffects(double x,double y,double z,String effect,boolean balefire) implements CustomPacketPayload {
    public static final Type<NuclearMukeEffects> TYPE=new Type<>(ResourceLocation.parse("hbm:muke_effects"));
    public static final StreamCodec<RegistryFriendlyByteBuf,NuclearMukeEffects> CODEC=StreamCodec.of((b,p)->{b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);b.writeUtf(p.effect,16);b.writeBoolean(p.balefire);},b->new NuclearMukeEffects(b.readDouble(),b.readDouble(),b.readDouble(),b.readUtf(16),b.readBoolean()));
    @Override public Type<NuclearMukeEffects> type(){return TYPE;}
    @SubscribeEvent public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(p,c)->c.enqueueWork(()->com.hbm.client.bomb.nuclear.MukeClient.receive(p)));}
    public static void send(Level level,double x,double y,double z,String type,boolean bf){sendAtOrigin(level,x,y,z,type,bf,x,y,z);}
    public static void sendAtOrigin(Level level,double x,double y,double z,String type,boolean bf,double ox,double oy,double oz){if(level instanceof ServerLevel server){var packet=new NuclearMukeEffects(x,y,z,type,bf);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(ox,oy,oz)<=250D*250D)PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}}}
}
