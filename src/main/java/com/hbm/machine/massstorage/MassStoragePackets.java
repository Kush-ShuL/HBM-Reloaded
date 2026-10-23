package com.hbm.machine.massstorage;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Source radius32 display sync and position-authorized provide/toggle controls. */
public final class MassStoragePackets {
    public record State(BlockPos pos,int stockpile,boolean output,ItemStack filter) implements CustomPacketPayload {
        static final Type<State> TYPE=new Type<>(ResourceLocation.parse("hbm:mass_storage_state"));
        static final StreamCodec<RegistryFriendlyByteBuf,State> CODEC=StreamCodec.of((buffer,value)->{buffer.writeBlockPos(value.pos);buffer.writeInt(value.stockpile);buffer.writeBoolean(value.output);ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer,value.filter);},buffer->new State(buffer.readBlockPos(),buffer.readInt(),buffer.readBoolean(),ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer)));
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public record Control(BlockPos pos,boolean toggle,boolean stack) implements CustomPacketPayload {
        static final Type<Control> TYPE=new Type<>(ResourceLocation.parse("hbm:mass_storage_control"));
        static final StreamCodec<RegistryFriendlyByteBuf,Control> CODEC=StreamCodec.of((buffer,value)->{buffer.writeBlockPos(value.pos);buffer.writeBoolean(value.toggle);buffer.writeBoolean(value.stack);},buffer->new Control(buffer.readBlockPos(),buffer.readBoolean(),buffer.readBoolean()));
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    static void register(RegisterPayloadHandlersEvent event){var registrar=event.registrar("1");
        registrar.playToClient(State.TYPE,State.CODEC,(value,context)->context.enqueueWork(()->{if(context.player().level().getBlockEntity(value.pos) instanceof MassStorageBlockEntity storage){storage.setStockpile(value.stockpile);storage.output=value.output;storage.displayType=value.filter.copy();}}));
        registrar.playToServer(Control.TYPE,Control.CODEC,(value,context)->context.enqueueWork(()->{var player=context.player();if(player.level().hasChunkAt(value.pos)&&player.level().getBlockEntity(value.pos) instanceof MassStorageBlockEntity storage&&storage.hasPermission(player)){if(value.toggle)storage.toggleOutput();else storage.provide(value.stack);}}));
    }
    static void synchronize(MassStorageBlockEntity storage){if(storage.getLevel() instanceof ServerLevel level){var pos=storage.getBlockPos();PacketDistributor.sendToPlayersNear(level,null,pos.getX(),pos.getY(),pos.getZ(),32,new State(pos,storage.getStockpile(),storage.output,storage.getFilterType()));}}
    private MassStoragePackets(){}
}
