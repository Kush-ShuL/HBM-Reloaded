package com.hbm.item.pwrprinter;

import com.hbm.machine.reactor.pwr.PwrControllerBlockEntity;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Independent source snapshot sync, fragmented to respect NeoForge payload limits without dropping cells. */
public final class PwrPrinterPackets {
    private static final int CHUNK_CELLS=1024;
    public record SliceData(UUID job,BlockPos min,BlockPos max,Direction direction,int part,int parts,List<PwrPrintSnapshot.Cell> cells)implements CustomPacketPayload {
        public static final Type<SliceData> TYPE=new Type<>(ResourceLocation.parse("hbm:pwr_printer_snapshot"));
        public static final StreamCodec<RegistryFriendlyByteBuf,SliceData> CODEC=StreamCodec.of((buffer,value)->{
            buffer.writeUUID(value.job);buffer.writeBlockPos(value.min);buffer.writeBlockPos(value.max);buffer.writeEnum(value.direction);buffer.writeVarInt(value.part);buffer.writeVarInt(value.parts);buffer.writeVarInt(value.cells.size());
            for(var cell:value.cells){buffer.writeBlockPos(cell.position());buffer.writeVarInt(BuiltInRegistries.BLOCK.getId(cell.original()));}
        },buffer->{var job=buffer.readUUID();var min=buffer.readBlockPos();var max=buffer.readBlockPos();var direction=buffer.readEnum(Direction.class);int part=buffer.readVarInt(),parts=buffer.readVarInt(),count=buffer.readVarInt();if(count<0||count>CHUNK_CELLS||parts<1||part<0||part>=parts)throw new IllegalArgumentException("Invalid PWR printer snapshot fragment");var cells=new ArrayList<PwrPrintSnapshot.Cell>(count);for(int i=0;i<count;i++)cells.add(new PwrPrintSnapshot.Cell(buffer.readBlockPos(),BuiltInRegistries.BLOCK.byId(buffer.readVarInt())));return new SliceData(job,min,max,direction,part,parts,List.copyOf(cells));});
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(SliceData.TYPE,SliceData.CODEC,(packet,context)->context.enqueueWork(()->com.hbm.client.pwrprinter.PwrPrinterClient.receive(packet)));}
    public static void send(ServerLevel level,PwrControllerBlockEntity controller){
        var snapshot=PwrPrintSnapshot.capture(level,controller);var job=UUID.randomUUID();int parts=Math.max(1,(snapshot.cells().size()+CHUNK_CELLS-1)/CHUNK_CELLS);
        for(int part=0;part<parts;part++){int start=part*CHUNK_CELLS,end=Math.min(snapshot.cells().size(),start+CHUNK_CELLS);var packet=new SliceData(job,snapshot.min(),snapshot.max(),snapshot.direction(),part,parts,snapshot.cells().subList(start,end));for(var player:level.players())if(player.distanceToSqr(controller.getBlockPos().getX(),controller.getBlockPos().getY(),controller.getBlockPos().getZ())<10000)PacketDistributor.sendToPlayer(player,packet);}
    }
    private PwrPrinterPackets(){}
}
