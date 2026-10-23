package com.hbm.item.tool;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source tau payload: count sparks and one hadron flash, radius50. */
public record WeldingParticlePacket(double x,double y,double z,int count,boolean small)implements CustomPacketPayload{
    public static final Type<WeldingParticlePacket> TYPE=new Type<>(ResourceLocation.parse("hbm:welding_particles"));
    public static final StreamCodec<RegistryFriendlyByteBuf,WeldingParticlePacket> CODEC=StreamCodec.of((b,p)->{b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);b.writeByte(p.count);b.writeBoolean(p.small);},b->new WeldingParticlePacket(b.readDouble(),b.readDouble(),b.readDouble(),b.readByte(),b.readBoolean()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->com.hbm.client.particle.WeldingEffectsClient.receive(packet)));}
    public static void send(Level level,Vec3 point,int count,boolean small){var packet=new WeldingParticlePacket(point.x,point.y,point.z,count,small);if(level instanceof ServerLevel server)for(var player:server.players())if(player.distanceToSqr(point)<2500)PacketDistributor.sendToPlayer(player,packet);}
}
