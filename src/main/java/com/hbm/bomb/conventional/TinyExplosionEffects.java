package com.hbm.bomb.conventional;

import com.hbm.registry.HbmSounds;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Source ExplosionEffectTiny: its own sound and original tinted vanillaExt large/small effect. */
public record TinyExplosionEffects(double x,double y,double z) implements CustomPacketPayload {
    public static final Type<TinyExplosionEffects> TYPE=new Type<>(ResourceLocation.parse("hbm:conventional_tiny_explosion"));
    public static final StreamCodec<RegistryFriendlyByteBuf,TinyExplosionEffects> CODEC=StreamCodec.of((b,p)->{b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);},b->new TinyExplosionEffects(b.readDouble(),b.readDouble(),b.readDouble()));
    @Override public Type<TinyExplosionEffects> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(p,c)->c.enqueueWork(()->com.hbm.client.bomb.conventional.TinyExplosionParticles.receive(p)));}
    public static void compose(Level level,double x,double y,double z){if(level instanceof ServerLevel server){level.playSound(null,x,y,z,HbmSounds.get("weapon.explosionTiny"),SoundSource.BLOCKS,15,1);var packet=new TinyExplosionEffects(x,y,z);for(var player:server.players())if(player.distanceToSqr(x,y,z)<=100D*100D)PacketDistributor.sendToPlayer(player,packet);}}
}
