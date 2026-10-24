package com.hbm.player;

import com.hbm.HbmReloaded;
import com.hbm.config.GeneralModes;
import java.net.URI;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.VersionChecker;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/** Login messages use this loaded mod's version and the loader's existing update result. */
public final class LoginMessages {
    private static boolean registered;

    public static void register() {
        if (registered) return;
        registered = true;
        NeoForge.EVENT_BUS.addListener(LoginMessages::onPlayerLogin);
    }

    private static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !GeneralModes.loginMessagesEnabled()) return;
        var container = ModList.get().getModContainerById(HbmReloaded.MOD_ID).orElseThrow();
        var mod = container.getModInfo();
        player.sendSystemMessage(Component.translatable("message.hbm.login.welcome",
                mod.getDisplayName(), mod.getVersion().toString(), FMLLoader.versionInfo().mcVersion()));

        var update = VersionChecker.getResult(mod);
        if ((update.status() != VersionChecker.Status.OUTDATED
                && update.status() != VersionChecker.Status.BETA_OUTDATED) || update.target() == null) return;
        var message = Component.translatable("message.hbm.login.update_available", update.target().toString())
                .withStyle(ChatFormatting.YELLOW);
        String url = updateUrl(update.url());
        if (url != null) {
            message.append(" ").append(Component.translatable("message.hbm.login.update_link")
                    .withStyle(style -> style.withColor(ChatFormatting.RED).withUnderlined(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))));
        }
        player.sendSystemMessage(message);
    }

    private static String updateUrl(String value) {
        if (value == null) return null;
        try {
            var uri = URI.create(value);
            String scheme = uri.getScheme();
            if (("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme))
                    && uri.getHost() != null) return uri.toASCIIString();
        } catch (IllegalArgumentException ignored) {
            // A malformed loader-provided link must not prevent the actual version notice.
        }
        return null;
    }

    private LoginMessages() {}
}
