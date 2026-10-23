package com.hbm.block.pipe;

import com.hbm.HbmReloaded;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original TOOL_CTRL/TOOL_ALT state, synchronized independently of client classes. */
public final class PipeControls {
    private static final Map<UUID, Keys> FLAGS = new HashMap<>();
    private PipeControls() {}
    public static Keys flags(Player player) { return FLAGS.getOrDefault(player.getUUID(), new Keys(false, false)); }
    public static void clear() { FLAGS.clear(); }
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(Keys.TYPE, Keys.CODEC, (payload, context) -> context.enqueueWork(() -> FLAGS.put(context.player().getUUID(), payload)));
    }
    public record Keys(boolean control, boolean alt) implements CustomPacketPayload {
        public static final Type<Keys> TYPE = new Type<>(HbmReloaded.id("pipe_tool_keys"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Keys> CODEC = StreamCodec.of((buffer, value) -> { buffer.writeBoolean(value.control()); buffer.writeBoolean(value.alt()); }, buffer -> new Keys(buffer.readBoolean(), buffer.readBoolean()));
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }
}
