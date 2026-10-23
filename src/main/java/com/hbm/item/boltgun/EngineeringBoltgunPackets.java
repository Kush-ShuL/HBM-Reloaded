package com.hbm.item.boltgun;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Source two independent packets: warm vanillaExt50range and owner's9slot RECOIL rail0. */
public record EngineeringBoltgunPackets(double x,double y,double z,int slot,boolean offhand) implements CustomPacketPayload {
    public static final Type<EngineeringBoltgunPackets> TYPE=new Type<>(ResourceLocation.parse("hbm:engineering_boltgun_effect"));
    public static final StreamCodec<RegistryFriendlyByteBuf,EngineeringBoltgunPackets> CODEC=StreamCodec.of((buffer,packet)->{buffer.writeDouble(packet.x);buffer.writeDouble(packet.y);buffer.writeDouble(packet.z);buffer.writeInt(packet.slot);buffer.writeBoolean(packet.offhand);},buffer->new EngineeringBoltgunPackets(buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),buffer.readInt(),buffer.readBoolean()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->com.hbm.client.boltgun.EngineeringBoltgunClient.receive(packet)));}
    static void effect(Level world,double x,double y,double z,double ox,double oy,double oz){if(world instanceof ServerLevel server){var packet=new EngineeringBoltgunPackets(x,y,z,-1,false);for(var player:server.players())if(player.distanceToSqr(ox,oy,oz)<=2500)PacketDistributor.sendToPlayer(player,packet);}}
    static void animate(Player player,InteractionHand hand){if(player instanceof ServerPlayer server)PacketDistributor.sendToPlayer(server,new EngineeringBoltgunPackets(0,0,0,player.getInventory().selected,hand==InteractionHand.OFF_HAND));}
}
