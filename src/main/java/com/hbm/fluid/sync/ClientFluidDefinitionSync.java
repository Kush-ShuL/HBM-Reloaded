package com.hbm.fluid.sync;

import com.google.gson.JsonParser;
import com.hbm.HbmReloaded;
import com.hbm.fluid.FluidContainerRegistry;
import com.hbm.fluid.HbmFluids;
import java.util.List;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Client-only memory overlay. Integrated connections share the server registry and only acknowledge. */
@EventBusSubscriber(modid = HbmReloaded.MOD_ID, value = Dist.CLIENT)
public final class ClientFluidDefinitionSync {
    private static Connection remoteConnection;
    private static HbmFluids.LocalState localDefinitions;
    private static List<FluidContainerRegistry.ContainerSnapshot> localContainers;
    private static Boolean localContainerCompat;

    public static void definitions(FluidDefinitionSync.Definitions packet, IPayloadContext context) {
        apply(context, packet.sequence(), () -> HbmFluids.applyDefinitions(JsonParser.parseString(packet.json()).getAsJsonObject()));
    }

    public static void snapshot(FluidDefinitionSync.Snapshot packet, IPayloadContext context) {
        apply(context, packet.sequence(), () -> {
            HbmFluids.applyDefinitions(JsonParser.parseString(packet.json()).getAsJsonObject());
            FluidContainerRegistry.applySnapshot(packet.containers(), packet.containerCompat());
        });
    }

    private static void apply(IPayloadContext context, long sequence, Runnable changes) {
        // PayloadRegistrar's MAIN wrapper has already switched threads; never wait/join on Netty.
        if (!context.connection().isConnected()) return;
        if (context.connection().isMemoryConnection()) {
            context.reply(new FluidDefinitionSync.Ack(sequence));
            return;
        }
        try {
            if (remoteConnection != context.connection()) {
                restore();
                localDefinitions = HbmFluids.captureLocalState();
                localContainers = FluidContainerRegistry.snapshot();
                localContainerCompat = FluidContainerRegistry.containerCompatOverride();
                remoteConnection = context.connection();
            }
            changes.run();
            context.reply(new FluidDefinitionSync.Ack(sequence));
        } catch (RuntimeException error) {
            HbmReloaded.LOGGER.error("Cannot apply server HBM fluid definitions/containers", error);
            restore();
            context.disconnect(Component.translatable("disconnect.hbm.fluid_sync.apply_failed"));
        }
    }

    @SubscribeEvent
    public static void disconnected(ClientPlayerNetworkEvent.LoggingOut event) {
        // Minecraft.disconnect fires this even after CONFIGURATION failure, with a null player/connection.
        // No local baseline exists for the integrated-server connection, so shared server data is untouched.
        restore();
    }

    private static void restore() {
        if (localDefinitions == null) return;
        HbmFluids.restoreLocalState(localDefinitions);
        FluidContainerRegistry.applySnapshot(localContainers, localContainerCompat);
        localDefinitions = null;
        localContainers = null;
        localContainerCompat = null;
        remoteConnection = null;
    }

    private ClientFluidDefinitionSync() {}
}
