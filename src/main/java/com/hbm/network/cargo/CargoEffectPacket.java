package com.hbm.network.cargo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public record CargoEffectPacket(double x,double y,double z,double dx,double dy,double dz,int color) implements CustomPacketPayload {
 public static final Type<CargoEffectPacket> TYPE=new Type<>(ResourceLocation.parse("hbm:drone_line"));public static final StreamCodec<RegistryFriendlyByteBuf,CargoEffectPacket> CODEC=StreamCodec.of((b,p)->{b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);b.writeDouble(p.dx);b.writeDouble(p.dy);b.writeDouble(p.dz);b.writeInt(p.color);},b->new CargoEffectPacket(b.readDouble(),b.readDouble(),b.readDouble(),b.readDouble(),b.readDouble(),b.readDouble(),b.readInt()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void send(ServerLevel level,double x,double y,double z,double dx,double dy,double dz,int color){PacketDistributor.sendToPlayersNear(level,null,x,y,z,150,new CargoEffectPacket(x,y,z,dx,dy,dz,color));}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(p,c)->c.enqueueWork(()->CargoVisuals.line.add(p.x,p.y,p.z,p.dx,p.dy,p.dz,p.color)));}
}
