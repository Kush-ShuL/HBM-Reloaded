package com.hbm.missile.custom.launcher;

import com.hbm.machine.missileassembly.MissileAssemblyContent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original independent50block state /250block multipart updates. Only a server-to-client packet is registered. */
public record CustomLauncherSync(BlockPos pos, CompoundTag data) implements CustomPacketPayload {
    public static final Type<CustomLauncherSync> TYPE = new Type<>(MissileAssemblyContent.id("custom_launcher_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CustomLauncherSync> CODEC = StreamCodec.of((buffer, packet) -> { buffer.writeBlockPos(packet.pos); buffer.writeNbt(packet.data); }, buffer -> new CustomLauncherSync(buffer.readBlockPos(), buffer.readNbt()));
    public CustomLauncherSync { pos = pos.immutable(); data = data.copy(); }
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(TYPE, CODEC, (packet, context) -> context.enqueueWork(() -> {
            var level = context.player().level(); if (level.hasChunkAt(packet.pos) && level.getBlockEntity(packet.pos) instanceof CustomLauncherEntity machine) machine.receiveClient(packet.data);
        }));
    }
    static void send(ServerLevel server, CustomLauncherEntity machine, CompoundTag data, int radius) {
        var packet = new CustomLauncherSync(machine.getBlockPos(), data);
        try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(server.getServer(),packet)){for (var player : server.players()) if (player.distanceToSqr(machine.getBlockPos().getX(), machine.getBlockPos().getY(), machine.getBlockPos().getZ()) < radius * radius) PacketDistributor.sendToPlayer(player, packet);dispatchScope.commit();}
    }
}
