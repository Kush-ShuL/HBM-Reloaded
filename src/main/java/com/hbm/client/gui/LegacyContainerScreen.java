package com.hbm.client.gui;

import java.util.Objects;

import com.hbm.client.gui.font.LegacyFonts;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Pixel-coordinate adapter for X5846 container screens. The supplied original
 * background and original logical xSize/ySize are used without responsive layout,
 * nine-slicing, resizing, recoloring, added labels or replacement button artwork.
 * Concrete ports retain their old label, progress, tank and control coordinates.
 */
public class LegacyContainerScreen<M extends AbstractContainerMenu> extends AbstractContainerScreen<M> {
    protected final ResourceLocation legacyBackground;
    protected final int legacyTextureWidth;
    protected final int legacyTextureHeight;
    private final int backgroundU;
    private final int backgroundV;

    public LegacyContainerScreen(M menu, Inventory inventory, Component title,
                                 ResourceLocation originalBackground, int xSize, int ySize) {
        this(menu, inventory, title, originalBackground, xSize, ySize, 256, 256, 0, 0);
    }

    public LegacyContainerScreen(M menu, Inventory inventory, Component title,
                                 ResourceLocation originalBackground, int xSize, int ySize,
                                 int textureWidth, int textureHeight, int backgroundU, int backgroundV) {
        super(menu, inventory, title);
        if (xSize <= 0 || ySize <= 0 || textureWidth <= 0 || textureHeight <= 0) throw new IllegalArgumentException("Original GUI dimensions must be positive");
        this.legacyBackground = Objects.requireNonNull(originalBackground);
        this.imageWidth = xSize;
        this.imageHeight = ySize;
        this.legacyTextureWidth = textureWidth;
        this.legacyTextureHeight = textureHeight;
        this.backgroundU = backgroundU;
        this.backgroundV = backgroundV;
    }

    @Override
    protected void init() {
        this.font = LegacyFonts.get();
        super.init();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.setColor(1, 1, 1, 1);
        drawTexturedModalRect(graphics, leftPos, topPos, backgroundU, backgroundV, imageWidth, imageHeight);
    }

    /** Draw exactly the old source rectangle; x/y are absolute scaled screen coordinates. */
    protected final void drawTexturedModalRect(GuiGraphics graphics, int x, int y, int u, int v, int width, int height) {
        graphics.blit(legacyBackground, x, y, (float) u, (float) v, width, height, legacyTextureWidth, legacyTextureHeight);
    }

    protected final void drawTexturedModalRect(GuiGraphics graphics, ResourceLocation texture,
                                               int x, int y, int u, int v, int width, int height,
                                               int textureWidth, int textureHeight) {
        graphics.blit(texture, x, y, (float) u, (float) v, width, height, textureWidth, textureHeight);
    }

    /** Source coordinates are relative to guiLeft/guiTop, as in old progress/tank calls. */
    protected final void drawLegacyRegion(GuiGraphics graphics, int x, int y, int u, int v, int width, int height) {
        drawTexturedModalRect(graphics, leftPos + x, topPos + y, u, v, width, height);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Concrete X5846 screens choose their own exact label text and coordinates.
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    /** The same boundary convention used by GuiInfoContainer.drawCustomInfoStat. */
    protected final boolean legacyInfoHovered(double mouseX, double mouseY, int x, int y, int width, int height) {
        int absoluteX = leftPos + x, absoluteY = topPos + y;
        return absoluteX <= mouseX && absoluteX + width > mouseX && absoluteY < mouseY && absoluteY + height >= mouseY;
    }

    /** Add a control using original sprite coordinates, without modern Button styling. */
    protected final LegacyTextureButton addLegacyButton(ResourceLocation texture,
                                                        int x, int y, int width, int height,
                                                        int u, int v, int hoverU, int hoverV,
                                                        int disabledU, int disabledV,
                                                        int textureWidth, int textureHeight,
                                                        Component narration, Runnable action) {
        return addRenderableWidget(new LegacyTextureButton(leftPos + x, topPos + y, width, height,
                texture, u, v, hoverU, hoverV, disabledU, disabledV,
                textureWidth, textureHeight, narration, action));
    }

    public static final class LegacyTextureButton extends AbstractWidget {
        private final ResourceLocation texture;
        private final int u, v, hoverU, hoverV, disabledU, disabledV, textureWidth, textureHeight;
        private final Runnable action;

        private LegacyTextureButton(int x, int y, int width, int height, ResourceLocation texture,
                                    int u, int v, int hoverU, int hoverV, int disabledU, int disabledV,
                                    int textureWidth, int textureHeight, Component narration, Runnable action) {
            super(x, y, width, height, narration);
            this.texture = texture;
            this.u = u;
            this.v = v;
            this.hoverU = hoverU;
            this.hoverV = hoverV;
            this.disabledU = disabledU;
            this.disabledV = disabledV;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            this.action = Objects.requireNonNull(action);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int sourceU = !active ? disabledU : isHovered ? hoverU : u;
            int sourceV = !active ? disabledV : isHovered ? hoverV : v;
            graphics.setColor(1, 1, 1, 1);
            graphics.blit(texture, getX(), getY(), (float) sourceU, (float) sourceV, width, height, textureWidth, textureHeight);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            action.run();
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }
    }
}
