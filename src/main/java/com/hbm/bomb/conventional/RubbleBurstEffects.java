package com.hbm.bomb.conventional;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original ParticleBurstPacket: block destruction particles at integer coordinates, within 50 blocks. */
public record RubbleBurstEffects(int x,int y,int z,int state) implements CustomPacketPayload {
    public static final Type<RubbleBurstEffects> TYPE=new Type<>(ResourceLocation.parse("hbm:rubble_burst"));
    public static final StreamCodec<RegistryFriendlyByteBuf,RubbleBurstEffects> CODEC=StreamCodec.of((b,p)->{b.writeInt(p.x);b.writeInt(p.y);b.writeInt(p.z);b.writeVarInt(p.state);},b->new RubbleBurstEffects(b.readInt(),b.readInt(),b.readInt(),b.readVarInt()));
    @Override public Type<RubbleBurstEffects> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(p,c)->c.enqueueWork(()->com.hbm.client.bomb.conventional.RubbleBurstParticles.receive(p)));}
    public static void send(Level level,int x,int y,int z,BlockState material,double originX,double originY,double originZ){if(level instanceof ServerLevel server){var packet=new RubbleBurstEffects(x,y,z,Block.getId(material));for(var player:server.players())if(player.distanceToSqr(originX,originY,originZ)<=50D*50D)PacketDistributor.sendToPlayer(player,packet);}}
}
