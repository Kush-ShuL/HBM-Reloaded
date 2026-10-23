package com.hbm.client.reactor.rbmk.column;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/** Original DODD look data, using the requested progress bars and the shared current Minecraft font. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
public final class RbmkDiagnostics {
    @SubscribeEvent public static void render(RenderGuiEvent.Post event) {
        var mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.level == null || !(mc.hitResult instanceof BlockHitResult hit)
                || !(mc.level.getBlockState(hit.getBlockPos()).getBlock() instanceof RbmkColumnBlock block)) return;
        var core = block.core(mc.level, hit.getBlockPos()); if (core == null) return;
        var g = event.getGuiGraphics(); var font = LegacyFonts.get(); int x = g.guiWidth() / 2 + 8, y = g.guiHeight() / 2 - 20;
        g.drawString(font, "Dump of Ordered Data Diagnostic (DODD)", x, y, 0x00FF00, true); y += 10;
        g.drawString(font, Component.translatable(core.getBlockState().getBlock().getDescriptionId()), x, y, 0xFFFF00, true); y += 12;
        for (var bar : core.progressBars()) {
            double ratio = Math.max(0, Math.min(1, bar.value() / bar.maximum()));
             g.fill(x + 1, y + 1, x + 1 + (int)(178 * ratio), y + 11, 0xA0000000 | bar.color());
            g.drawString(font, Component.translatable(bar.name()).append(": " + String.format(java.util.Locale.ROOT, "%.1f", bar.value())), x + 2, y + 1, 0xFFFFFF, true); y += 14;
        }
    }
    private RbmkDiagnostics() { }
}
