package com.hbm.network.rtty.relay;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public record RadioRelayControl(BlockPos position, CompoundTag data) implements CustomPacketPayload {
    public static final Type<RadioRelayControl> TYPE = new Type<>(ResourceLocation.parse("hbm:radio_relay_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RadioRelayControl> CODEC = StreamCodec.of(
            (buffer, packet) -> { buffer.writeBlockPos(packet.position); buffer.writeNbt(packet.data); },
            buffer -> { var position = buffer.readBlockPos(); var data = buffer.readNbt(); return new RadioRelayControl(position, data == null ? new CompoundTag() : data); });

    public RadioRelayControl { position = position.immutable(); data = data.copy(); }
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(TYPE, CODEC, (packet, context) -> context.enqueueWork(() -> {
            var player = context.player();
            var pos = packet.position;
            if (player.distanceToSqr(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5) >= 256 || !player.level().hasChunkAt(pos)) return;
            if (player.level().getBlockEntity(pos) instanceof RadioRelayEntity relay) relay.control(packet.data);
        }));
    }
}
