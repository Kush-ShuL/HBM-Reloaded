package com.hbm.bomb.conventional.grenade;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original HbmAnimationPacket EQUIP on rail0; recipient resolves the held grenade just as the source did. */
public record GrenadeEquipEffects(int rail) implements CustomPacketPayload {
    public static final Type<GrenadeEquipEffects> TYPE=new Type<>(ResourceLocation.parse("hbm:grenade_equip"));
    public static final StreamCodec<RegistryFriendlyByteBuf,GrenadeEquipEffects> CODEC=StreamCodec.of((b,p)->b.writeVarInt(p.rail),b->new GrenadeEquipEffects(b.readVarInt()));
    @Override public Type<GrenadeEquipEffects> type(){return TYPE;}
    public static void send(Player player){if(player instanceof ServerPlayer server)PacketDistributor.sendToPlayer(server,new GrenadeEquipEffects(0));}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(p,c)->c.enqueueWork(com.hbm.client.bomb.conventional.grenade.GrenadeAnimations::equip));}
}
