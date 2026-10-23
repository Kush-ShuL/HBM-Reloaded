package com.hbm.machine.reactorcontroller;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class ReactorControllerPackets {
    public record Control(int containerId,CompoundTag data) implements CustomPacketPayload {
        public static final Type<Control> TYPE=new Type<>(ResourceLocation.parse("hbm:reactor_remote_control"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Control> CODEC=StreamCodec.of((b,p)->{b.writeVarInt(p.containerId);b.writeNbt(p.data);},b->new Control(b.readVarInt(),tag(b)));
        public Control{data=data.copy();}@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public record Snapshot(BlockPos position,CompoundTag data) implements CustomPacketPayload {
        public static final Type<Snapshot> TYPE=new Type<>(ResourceLocation.parse("hbm:reactor_remote_snapshot"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Snapshot> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.position);b.writeNbt(p.data);},b->new Snapshot(b.readBlockPos(),tag(b)));
        public Snapshot{position=position.immutable();data=data.copy();}@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    private static CompoundTag tag(RegistryFriendlyByteBuf buffer){var value=buffer.readNbt();return value==null?new CompoundTag():value;}
    public static void register(RegisterPayloadHandlersEvent event){var registrar=event.registrar("1");registrar.playToServer(Control.TYPE,Control.CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof ReactorControllerMenu menu&&menu.containerId==packet.containerId&&menu.stillValid(player)&&player.level().getBlockEntity(menu.position()) instanceof ReactorControllerEntity machine&&machine.hasPermission(player))machine.receiveControl(packet.data);}));registrar.playToClient(Snapshot.TYPE,Snapshot.CODEC,(packet,context)->context.enqueueWork(()->{var world=context.player().level();if(world.getBlockEntity(packet.position) instanceof ReactorControllerEntity machine)machine.readSnapshot(packet.data);}));}
    private ReactorControllerPackets(){}
}
