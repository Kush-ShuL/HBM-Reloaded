package com.hbm.machine.conveyorextraction;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Three original boolean-toggle actions; ghost filtering uses the native menu click protocol. */
public record CraneExtractionControl(BlockPos position,int action) implements CustomPacketPayload {
    public static final Type<CraneExtractionControl> TYPE=new Type<>(ResourceLocation.parse("hbm:crane_extraction_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf,CraneExtractionControl> CODEC=StreamCodec.of((buf,message)->{buf.writeBlockPos(message.position);buf.writeByte(message.action);},buf->new CraneExtractionControl(buf.readBlockPos(),buf.readUnsignedByte()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(message,context)->context.enqueueWork(()->{var player=context.player();if(!player.level().hasChunkAt(message.position))return;var tile=player.level().getBlockEntity(message.position);if(tile instanceof CraneExtractorEntity extractor&&extractor.hasPermission(player)&&message.action<2)extractor.toggle(message.action);else if(tile instanceof CraneInserterEntity inserter&&inserter.hasPermission(player)&&message.action==2)inserter.toggle();}));}
}
