package com.hbm.machine.fensu;

import java.math.BigInteger;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Same full bigint source snapshot sent BEFORE this tick's item charging and delta update, to the original100-block range. */
public final class FensuPackets {
    public record State(BlockPos pos,BigInteger power,BigInteger delta,short redLow,short redHigh,byte priority,boolean muffled,boolean tilted)implements CustomPacketPayload {
        public static final Type<State> TYPE=new Type<>(ResourceLocation.parse("hbm:fensu_state"));
        public static final StreamCodec<RegistryFriendlyByteBuf,State> CODEC=StreamCodec.of((buffer,state)->{buffer.writeBlockPos(state.pos);buffer.writeByteArray(state.power.toByteArray());buffer.writeByteArray(state.delta.toByteArray());buffer.writeShort(state.redLow);buffer.writeShort(state.redHigh);buffer.writeByte(state.priority);buffer.writeBoolean(state.muffled);buffer.writeBoolean(state.tilted);},buffer->new State(buffer.readBlockPos(),new BigInteger(buffer.readByteArray(Integer.MAX_VALUE)),new BigInteger(buffer.readByteArray(Integer.MAX_VALUE)),buffer.readShort(),buffer.readShort(),buffer.readByte(),buffer.readBoolean(),buffer.readBoolean()));
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(State.TYPE,State.CODEC,(state,context)->context.enqueueWork(()->com.hbm.client.fensu.FensuClient.receive(state)));}
    static void send(FensuBlockEntity machine){if(machine.getLevel() instanceof ServerLevel level){var p=machine.getBlockPos();var packet=new State(p,machine.storedPower(),machine.delta,machine.redLow,machine.redHigh,(byte)machine.getPriority().ordinal(),machine.muffled,machine.tilted);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(level.getServer(),packet)){for(var player:level.players())if(player.distanceToSqr(p.getX(),p.getY(),p.getZ())<=10000)PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}}}
    private FensuPackets(){}
}
