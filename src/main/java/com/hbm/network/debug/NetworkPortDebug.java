package com.hbm.network.debug;

import com.hbm.config.SourceServerConfig;
import com.hbm.energy.NodeConnection;
import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Source TilePort network icons from actual declared connection points and role calls. */
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD)
public record NetworkPortDebug(double x,double y,double z,double mx,double my,double mz,boolean fluid,int color) implements CustomPacketPayload {
    public static Consumer<NetworkPortDebug> CLIENT=packet->{};
    public static final Type<NetworkPortDebug> TYPE=new Type<>(ResourceLocation.parse("hbm:network_port_debug"));
    public static final StreamCodec<RegistryFriendlyByteBuf,NetworkPortDebug> CODEC=StreamCodec.of((out,p)->{
        out.writeDouble(p.x);out.writeDouble(p.y);out.writeDouble(p.z);out.writeDouble(p.mx);out.writeDouble(p.my);out.writeDouble(p.mz);out.writeBoolean(p.fluid);out.writeInt(p.color);
    },in->new NetworkPortDebug(in.readDouble(),in.readDouble(),in.readDouble(),in.readDouble(),in.readDouble(),in.readDouble(),in.readBoolean(),in.readInt()));
    @Override public Type<NetworkPortDebug> type(){return TYPE;}
    @SubscribeEvent public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->CLIENT.accept(packet)));}
    public static void emit(ServerLevel level,Iterable<NodeConnection> connections,boolean receive,boolean fluid,int color){
        if(!SourceServerConfig.DEBUG_PORT_VIEW.get())return;
        double motion=receive?-.1D:.1D;
        for(var connection:connections){
            var direction=connection.direction();var offset=connection.target().relative(direction);
            if(!receive)offset=offset.relative(direction.getOpposite());
            double x=offset.getX()+.5-direction.getStepX()*.5+level.random.nextDouble()*.5-.25;
            double y=offset.getY()+.5-direction.getStepY()*.5+level.random.nextDouble()*.5-.25;
            double z=offset.getZ()+.5-direction.getStepZ()*.5+level.random.nextDouble()*.5-.25;
            var packet=new NetworkPortDebug(x,y,z,direction.getStepX()*motion,direction.getStepY()*motion,direction.getStepZ()*motion,fluid,color);
            for(var player:level.players())if(player.distanceToSqr(x,y,z)<625)PacketDistributor.sendToPlayer(player,packet);
        }
    }
}
