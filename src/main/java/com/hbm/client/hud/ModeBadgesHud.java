package com.hbm.client.hud;

import com.hbm.client.config.ClientEffectsConfig;
import com.hbm.config.GeneralModes;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/** The four original atlas badges, before all HUD layers and their later nuclear translation. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
public final class ModeBadgesHud {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("hbm", "textures/misc/overlay_misc.png");

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void render(RenderGuiEvent.Pre event) {
        var client = Minecraft.getInstance();
        if (client.options.hideGui || client.player == null || client.level == null || !ClientEffectsConfig.modeBadgesEnabled()) return;
        var state = GeneralModes.synchronizedSnapshot();
        if (state == null || !(state.badge528() || state.badgeExpensive() || state.badgeRampant())) return;

        var graphics = event.getGuiGraphics();
        graphics.flush();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        try {
            int x = 2;
            if (state.badge528()) { graphics.blit(TEXTURE, x, 2, 0, 218, 24, 8, 256, 256); x += 26; }
            if (state.badgeExpensive()) { graphics.blit(TEXTURE, x, 2, 0, 226, 24, 8, 256, 256); x += 26; }
            if (state.badgeRampant()) { graphics.blit(TEXTURE, x, 2, 0, 234, 24, 8, 256, 256); x += 26; }
            if (state.badge528() && state.badgeExpensive() && state.badgeRampant()) graphics.blit(TEXTURE, x, 2, 0, 242, 24, 8, 256, 256);
            graphics.flush();
        } finally {
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
        }
    }

    private ModeBadgesHud() {}
}
