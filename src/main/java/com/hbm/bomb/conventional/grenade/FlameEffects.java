package com.hbm.bomb.conventional.grenade;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Source FlameCreator: one authored ParticleFlamethrower, five original palettes, 50-block packet range. */
public record FlameEffects(double x,double y,double z,int meta) implements CustomPacketPayload {
    public static final Type<FlameEffects> TYPE=new Type<>(ResourceLocation.parse("hbm:lingering_flame"));
    public static final StreamCodec<RegistryFriendlyByteBuf,FlameEffects> CODEC=StreamCodec.of((b,p)->{b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);b.writeInt(p.meta);},b->new FlameEffects(b.readDouble(),b.readDouble(),b.readDouble(),b.readInt()));
    @Override public Type<FlameEffects> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(p,c)->c.enqueueWork(()->com.hbm.client.bomb.conventional.grenade.LingeringFlameParticle.receive(p)));}
    public static void send(Level level,double x,double y,double z,int meta){if(level instanceof ServerLevel server){var packet=new FlameEffects(x,y,z,meta);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(x,y,z)<=2500)PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}}}
    public static void client(Level level,double x,double y,double z,int meta){if(level.isClientSide)com.hbm.client.bomb.conventional.grenade.LingeringFlameParticle.receive(new FlameEffects(x,y,z,meta));}
}
