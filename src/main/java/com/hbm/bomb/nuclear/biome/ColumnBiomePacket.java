package com.hbm.bomb.nuclear.biome;

import net.minecraft.network.*;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Complete replacement snapshot, including pristine samples required to hide native quart spillover. */
public record ColumnBiomePacket(ResourceKey<Level> dimension,int chunkX,int chunkZ,BiomePalette original,ColumnBiomeChunk.Targets targets)implements CustomPacketPayload {
    public static final Type<ColumnBiomePacket> TYPE=new Type<>(ResourceLocation.parse("hbm:column_biomes"));
    public static final StreamCodec<RegistryFriendlyByteBuf,ColumnBiomePacket> CODEC=StreamCodec.of((b,p)->{b.writeResourceLocation(p.dimension.location());b.writeInt(p.chunkX);b.writeInt(p.chunkZ);b.writeBoolean(p.original!=null);if(p.original!=null){p.original.encode(b);b.writeCollection(p.targets.palette(),FriendlyByteBuf::writeResourceLocation);b.writeVarInt(p.targets.bits());b.writeLongArray(p.targets.data());}},b->{var dimension=ResourceKey.create(Registries.DIMENSION,b.readResourceLocation());int x=b.readInt(),z=b.readInt();if(!b.readBoolean())return new ColumnBiomePacket(dimension,x,z,null,null);var original=BiomePalette.decode(b);var palette=b.readList(FriendlyByteBuf::readResourceLocation);int bits=b.readVarInt();var values=b.readLongArray(null,256);return new ColumnBiomePacket(dimension,x,z,original,new ColumnBiomeChunk.Targets(palette,bits,values));});
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->com.hbm.client.bomb.nuclear.biome.ColumnBiomeClient.receive(packet)));}
}
