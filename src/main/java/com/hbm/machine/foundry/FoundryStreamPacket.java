package com.hbm.machine.foundry;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source auxiliary foundry stream packet: clients receive only the visual event, never standard fluid data. */
public record FoundryStreamPacket(double x,double y,double z,int color,Direction direction,float length,float base,float offset) implements CustomPacketPayload {
 public static Consumer<FoundryStreamPacket> CLIENT_HANDLER=packet->{};
 public static final Type<FoundryStreamPacket> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("hbm","foundry_stream"));
 public static final StreamCodec<RegistryFriendlyByteBuf,FoundryStreamPacket> CODEC=StreamCodec.of((buf,p)->{buf.writeDouble(p.x);buf.writeDouble(p.y);buf.writeDouble(p.z);buf.writeInt(p.color);buf.writeByte(p.direction.get3DDataValue());buf.writeFloat(p.length);buf.writeFloat(p.base);buf.writeFloat(p.offset);},buf->new FoundryStreamPacket(buf.readDouble(),buf.readDouble(),buf.readDouble(),buf.readInt(),Direction.from3DDataValue(buf.readByte()),buf.readFloat(),buf.readFloat(),buf.readFloat()));
 @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->CLIENT_HANDLER.accept(packet)));}
 public static void send(ServerLevel level,Vec3 pos,int color,Direction direction,float length,float base,float offset){PacketDistributor.sendToPlayersNear(level,null,pos.x,pos.y,pos.z,50,new FoundryStreamPacket(pos.x,pos.y,pos.z,color,direction,length,base,offset));}
}
