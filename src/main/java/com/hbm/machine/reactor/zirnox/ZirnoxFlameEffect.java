package com.hbm.machine.reactor.zirnox;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Typed transport of the original rbmkflame/maxAge particle payload, source75-block radius. */
public record ZirnoxFlameEffect(double x,double y,double z,int maxAge) implements CustomPacketPayload {
    public static final Type<ZirnoxFlameEffect> TYPE=new Type<>(ResourceLocation.parse("hbm:reactor_rbmk_flame"));
    public static final StreamCodec<RegistryFriendlyByteBuf,ZirnoxFlameEffect> CODEC=StreamCodec.of((b,p)->{b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);b.writeVarInt(p.maxAge);},b->new ZirnoxFlameEffect(b.readDouble(),b.readDouble(),b.readDouble(),b.readVarInt()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(value,context)->context.enqueueWork(()->com.hbm.machine.reactor.zirnox.client.ZirnoxClient.flame(value)));}
    public static void send(ServerLevel level,double x,double y,double z,int maxAge){var packet=new ZirnoxFlameEffect(x,y,z,maxAge);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(level.getServer(),packet)){for(var player:level.players())if(player.distanceToSqr(x,y,z)<75*75)PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}}
}
