package com.hbm.item.rttyhandheld;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** The actual source chan-only item control, authenticated against the real still-held item/hand/kind. */
public record RttyHandheldControl(InteractionHand hand, RttyHandheldItem.Kind kind, String channel) implements CustomPacketPayload {
    public static final Type<RttyHandheldControl> TYPE = new Type<>(ResourceLocation.parse("hbm:rtty_handheld_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RttyHandheldControl> CODEC = StreamCodec.of(
        (buffer, packet) -> { buffer.writeEnum(packet.hand); buffer.writeEnum(packet.kind); buffer.writeUtf(packet.channel, 15); },
        buffer -> new RttyHandheldControl(buffer.readEnum(InteractionHand.class), buffer.readEnum(RttyHandheldItem.Kind.class), buffer.readUtf(15)));
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(TYPE, CODEC, (packet, context) -> context.enqueueWork(() -> {
            var stack = context.player().getItemInHand(packet.hand);
            if (!(stack.getItem() instanceof RttyHandheldItem item) || item.kind != packet.kind) return;
            CustomData.update(DataComponents.CUSTOM_DATA, stack, data -> data.putString(RttyHandheldItem.KEY_CHANNEL, packet.channel));
        }));
    }
}
