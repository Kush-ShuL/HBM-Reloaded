package com.hbm.client.reactor.rbmk.column;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/** Source DODD data for every real RBMK column, with the existing progress bars and current Minecraft font. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
public final class RbmkDiagnostics {
    @SubscribeEvent public static void render(RenderGuiEvent.Post event) {if(!com.hbm.item.settings.client.LegacyInfoSystem.lookOverlayEnabled())return;
        var mc = Minecraft.getInstance();
        if (mc.level == null || !(mc.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK
                || !(mc.level.getBlockState(hit.getBlockPos()).getBlock() instanceof RbmkColumnBlock block)) return;
        var core = block.core(mc.level, hit.getBlockPos());
        if (core == null) return;
        var graphics = event.getGuiGraphics();
        var font = LegacyFonts.get();
        int x = graphics.guiWidth() / 2 + 8, y = graphics.guiHeight() / 2;
        var title = Component.translatable("gui.hbm.rbmk.diagnostics.title");
        graphics.drawString(font, title, x + 1, y - 19, 0x006000, false);
        graphics.drawString(font, title, x, y - 20, 0x00FF00, false);
        var name = Component.translatable(core.getBlockState().getBlock().getDescriptionId());
        graphics.drawString(font, name, x + 1, y - 9, 0x606000, false);
        graphics.drawString(font, name, x, y - 10, 0xFFFF00, false);
        var bars = core.progressBars();
        for (var bar : bars) {
            double ratio = Math.max(0, Math.min(1, bar.value() / bar.maximum()));
            graphics.fill(x + 1, y + 1, x + 1 + (int)(178 * ratio), y + 11, 0xA0000000 | bar.color());
            graphics.drawString(font, Component.translatable(bar.name()).append(": " + String.format(java.util.Locale.ROOT, "%.1f", bar.value())), x + 2, y + 1, 0xFFFFFF, true);
            y += 14;
        }
        for (var line : RbmkDiagnosticFields.lines(core, bars)) {
            graphics.drawString(font, line, x, y, 0xFFFFFF, false);
            y += 10;
        }
    }
    private RbmkDiagnostics() {}
}
