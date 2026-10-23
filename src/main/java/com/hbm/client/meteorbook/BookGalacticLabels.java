package com.hbm.client.meteorbook;

import com.hbm.client.gui.font.LegacyFontData;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.*;

/** The original standardGalacticFontRenderer page and its 1.7.10 width/7.99px drawing rules. */
final class BookGalacticLabels implements ResourceManagerReloadListener {
    static final BookGalacticLabels INSTANCE = new BookGalacticLabels();
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("hbm", "textures/font/meteorbook/ascii_sga.png");
    private static int[] widths;
    @Override public void onResourceManagerReload(ResourceManager resources) {
        int[] next = new int[256];
        try (var stream = resources.open(TEXTURE); NativeImage image = NativeImage.read(stream)) {
            int cellWidth = image.getWidth() / 16, cellHeight = image.getHeight() / 16;
            float factor = 8F / cellWidth;
            for (int character = 0; character < 256; character++) {
                int right;
                for (right = cellWidth - 1; right >= 0; right--) {
                    boolean transparent = true;
                    for (int y = 0; y < cellHeight && transparent; y++) if (image.getPixelRGBA(character % 16 * cellWidth + right, character / 16 * cellHeight + y) >>> 24 != 0) transparent = false;
                    if (!transparent) break;
                }
                next[character] = (int) (.5 + ++right * factor) + 1;
            }
        } catch (IOException exception) { throw new IllegalStateException("Cannot load original galactic book font", exception); }
        widths = next;
    }
    static void draw(GuiGraphics graphics, String text, float x, float y, int color) {
        if (widths == null) INSTANCE.onResourceManagerReload(Minecraft.getInstance().getResourceManager());
        var matrix = graphics.pose().last().pose();
        var out = graphics.bufferSource().getBuffer(RenderType.text(TEXTURE));
        float red = (color >> 16 & 255) / 255F, green = (color >> 8 & 255) / 255F, blue = (color & 255) / 255F;
        for (char character : text.toCharArray()) {
            if (character == ' ') { x += 4; continue; }
            int index = LegacyFontData.ASCII.indexOf(character);
            if (index < 0) continue;
            float width = widths[index] - .01F - 1F, u = index % 16 * 8F / 128F, v = index / 16 * 8F / 128F;
            out.addVertex(matrix, x, y, 0).setColor(red, green, blue, 1).setUv(u, v).setLight(0xF000F0);
            out.addVertex(matrix, x, y + 7.99F, 0).setColor(red, green, blue, 1).setUv(u, v + 7.99F / 128F).setLight(0xF000F0);
            out.addVertex(matrix, x + width, y + 7.99F, 0).setColor(red, green, blue, 1).setUv(u + width / 128F, v + 7.99F / 128F).setLight(0xF000F0);
            out.addVertex(matrix, x + width, y, 0).setColor(red, green, blue, 1).setUv(u + width / 128F, v).setLight(0xF000F0);
            x += widths[index];
        }
    }
}
