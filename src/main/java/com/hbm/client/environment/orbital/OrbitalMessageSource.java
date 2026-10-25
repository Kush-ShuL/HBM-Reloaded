package com.hbm.client.environment.orbital;

import com.hbm.HbmReloaded;
import com.hbm.client.config.ClientEffectsConfig;
import com.hbm.client.loadingtips.LoadingTipSource;
import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.Util;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/** Original HTTPHandler.loadSoyuz feed; the fixed orbital scene keeps its actual source selection. */
@EventBusSubscriber(modid = "hbm", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class OrbitalMessageSource {
    private static final URI FEED = URI.create("https://gist.githubusercontent.com/HbmMods/a1cad71d00b6915945a43961d0037a43/raw/soyuz_holo");
    private static final AtomicBoolean REQUESTED = new AtomicBoolean();
    private static volatile List<String> remote = List.of();

    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        if (ClientEffectsConfig.remoteOrbitalMessagesEnabled() && REQUESTED.compareAndSet(false, true)) {
            Util.ioPool().execute(() -> {
                try { remote = LoadingTipSource.readFeed(FEED); }
                catch (java.io.IOException | RuntimeException failure) {
                    HbmReloaded.LOGGER.debug("Could not read orbital-module messages; using local messages", failure);
                }
            });
        }
    }

    public static List<String> messages() {
        var local = ClientEffectsConfig.orbitalModuleMessages();
        if (local.isEmpty()) return local;
        var downloaded = remote;
        return ClientEffectsConfig.remoteOrbitalMessagesEnabled() && !downloaded.isEmpty() ? downloaded : local;
    }

    private OrbitalMessageSource() {}
}
