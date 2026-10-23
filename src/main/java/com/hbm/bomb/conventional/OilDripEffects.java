package com.hbm.bomb.conventional;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Source oil sweat AuxParticle packet: one long-lived coal-block dust particle every five ticks, within 25 blocks. */
public record OilDripEffects(int entity) implements CustomPacketPayload {
    public static final Type<OilDripEffects> TYPE=new Type<>(ResourceLocation.parse("hbm:oil_drip"));
    public static final StreamCodec<RegistryFriendlyByteBuf,OilDripEffects> CODEC=StreamCodec.of((b,p)->b.writeVarInt(p.entity),b->new OilDripEffects(b.readVarInt()));
    @Override public Type<OilDripEffects> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(p,c)->c.enqueueWork(()->com.hbm.client.bomb.conventional.SmallExplosionEffects.sweat(p.entity)));}
    public static void send(LivingEntity entity){if(entity.level() instanceof ServerLevel server){var packet=new OilDripEffects(entity.getId());for(var player:server.players())if(player.distanceToSqr(entity)<=25D*25D)PacketDistributor.sendToPlayer(player,packet);}}
}
