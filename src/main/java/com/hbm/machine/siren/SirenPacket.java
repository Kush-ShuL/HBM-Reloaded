package com.hbm.machine.siren;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original TESirenPacket four ints plus boolean and original1500-block targetpoint at the block corner. */
public record SirenPacket(int x,int y,int z,int track,boolean active) implements CustomPacketPayload {
    public static final Type<SirenPacket> TYPE=new Type<>(ResourceLocation.parse("hbm:siren_track"));
    public static final StreamCodec<RegistryFriendlyByteBuf,SirenPacket> CODEC=StreamCodec.of((buffer,packet)->{buffer.writeInt(packet.x);buffer.writeInt(packet.y);buffer.writeInt(packet.z);buffer.writeInt(packet.track);buffer.writeBoolean(packet.active);},buffer->new SirenPacket(buffer.readInt(),buffer.readInt(),buffer.readInt(),buffer.readInt(),buffer.readBoolean()));
    public static Consumer<SirenPacket> CLIENT=packet->{};
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->CLIENT.accept(packet)));}
    public static void send(ServerLevel level,BlockPos pos,int track,boolean active){PacketDistributor.sendToPlayersNear(level,null,pos.getX(),pos.getY(),pos.getZ(),1500,new SirenPacket(pos.getX(),pos.getY(),pos.getZ(),track,active));}
}
