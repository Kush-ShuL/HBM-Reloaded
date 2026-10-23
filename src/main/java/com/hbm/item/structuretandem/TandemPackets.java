package com.hbm.item.structuretandem;

import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Real standalone source GUI, with an authenticated interaction instead of a fabricated inventory/container. */
public final class TandemPackets {
    public static Consumer<Open> CLIENT_OPEN=packet->{};
    private record Session(UUID token,ResourceLocation dimension,BlockPos pos){}
    private static final Map<ServerPlayer,Session> SESSIONS=new WeakHashMap<>();
    public record Open(BlockPos pos,UUID token,CompoundTag settings) implements CustomPacketPayload {
        public static final Type<Open> TYPE=new Type<>(ResourceLocation.parse("hbm:structure_tandem_open"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Open> CODEC=StreamCodec.of((buffer,value)->{buffer.writeBlockPos(value.pos);buffer.writeUUID(value.token);buffer.writeNbt(value.settings);},buffer->new Open(buffer.readBlockPos(),buffer.readUUID(),read(buffer)));
        public Open{pos=pos.immutable();settings=settings.copy();}
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public record Control(BlockPos pos,UUID token,CompoundTag settings) implements CustomPacketPayload {
        public static final Type<Control> TYPE=new Type<>(ResourceLocation.parse("hbm:structure_tandem_control"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Control> CODEC=StreamCodec.of((buffer,value)->{buffer.writeBlockPos(value.pos);buffer.writeUUID(value.token);buffer.writeNbt(value.settings);},buffer->new Control(buffer.readBlockPos(),buffer.readUUID(),read(buffer)));
        public Control{pos=pos.immutable();settings=settings.copy();}
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    private static CompoundTag read(RegistryFriendlyByteBuf buffer){var tag=buffer.readNbt();if(tag==null)throw new IllegalArgumentException("Missing actual tandem settings");return tag;}
    public static void open(ServerLevel level,Player player,TandemEntity tile){
        if(player instanceof ServerPlayer actor){var session=new Session(UUID.randomUUID(),level.dimension().location(),tile.getBlockPos().immutable());SESSIONS.put(actor,session);PacketDistributor.sendToPlayer(actor,new Open(session.pos,session.token,tile.settings()));}
    }
    public static void register(RegisterPayloadHandlersEvent event){
        var registrar=event.registrar("1");
        registrar.playToClient(Open.TYPE,Open.CODEC,(packet,context)->context.enqueueWork(()->CLIENT_OPEN.accept(packet)));
        registrar.playToServer(Control.TYPE,Control.CODEC,(packet,context)->context.enqueueWork(()->{
            if(!(context.player() instanceof ServerPlayer actor))return;
            var session=SESSIONS.get(actor);if(session==null||!session.token.equals(packet.token)||!session.dimension.equals(actor.serverLevel().dimension().location())||!session.pos.equals(packet.pos)||actor.distanceToSqr(packet.pos.getCenter())>64)return;
            if(actor.serverLevel().getChunkSource().getChunkNow(packet.pos.getX()>>4,packet.pos.getZ()>>4)==null)return;
            if(!(actor.serverLevel().getBlockEntity(packet.pos) instanceof TandemEntity tile))return;
            SESSIONS.remove(actor);tile.edit(packet.settings);
        }));
    }
    private TandemPackets(){}
}
