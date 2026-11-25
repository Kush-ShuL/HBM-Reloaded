package com.hbm.client.trackingradar;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.trackingradar.TrackingRadarContent.RadarEntity;
import com.hbm.machine.trackingradar.TrackingRadarMenu;
import com.hbm.satellite.SatelliteRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

public final class TrackingRadarScreen extends LegacyContainerScreen<TrackingRadarMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("hbm:textures/gui/machine/gui_tracking_radar.png");
    private static final int ROWS = 7;
    private int scroll;

    public TrackingRadarScreen(TrackingRadarMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, TEXTURE, 256, 240);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 110 - font.width(title) / 2, 6, 0xffffff, false);
        graphics.drawString(font, playerInventoryTitle, 48, 146, 0x404040, false);
        var contacts = menu.contacts();
        for (int i = 0; i < ROWS && scroll + i < contacts.size(); i++) {
            var contact = contacts.get(scroll + i);
            int y = 22 + i * 12;
            graphics.drawString(font, contact.x() + "-" + contact.y() + "-" + contact.z(), 48, y, 0x00ff00, false);
            graphics.pose().pushPose();
            graphics.pose().translate(183, y - 2, 0);
            graphics.pose().scale(.625F, .625F, 1);
            graphics.renderItem(SatelliteRegistry.iconFor(contact.type()), 0, 0);
            graphics.pose().popPose();
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partial, int mouseX, int mouseY) {
        drawLegacyRegion(graphics, 0, 0, 0, 0, 256, 132);
        drawLegacyRegion(graphics, 40, 140, 40, 140, 176, 100);
        int mode = menu.mode();
        if (mode >= 0) drawLegacyRegion(graphics, 7, 17 + mode * 9, 232, 155 + mode * 9, 18, 9);
        switch (mode) {
            case 0 -> drawLegacyRegion(graphics, 19, 56, 9, 140, 9, 24);
            case 1 -> drawLegacyRegion(graphics, 10, 81, 0, 165, 14, 35);
            case 2 -> drawLegacyRegion(graphics, 26, 81, 16, 165, 11, 35);
        }
        int maxScroll = Math.max(1, menu.contacts().size() - ROWS);
        drawLegacyRegion(graphics, 200, 17 + scroll * 73 / maxScroll, 216, 0, 12, 15);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        super.render(graphics, mouseX, mouseY, partial);
        if (legacyInfoHovered(mouseX, mouseY, 232, 18, 16, 88)) {
            graphics.renderTooltip(font, Component.literal(menu.power() + "/" + RadarEntity.CAPACITY + " HE"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0 && minecraft != null && minecraft.gameMode != null) {
            for (int mode = 0; mode < 3; mode++) {
                if (legacyInfoHovered(x, y, 7, 17 + 9 * mode, 18, 9)) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, mode);
                    return true;
                }
            }
        }
        return super.mouseClicked(x, y, button);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double horizontal, double vertical) {
        int maxScroll = Math.max(0, menu.contacts().size() - ROWS);
        if (maxScroll > 0 && vertical != 0 && legacyInfoHovered(x, y, 44, 17, 168, 88)) {
            scroll = Math.clamp(scroll - (int) Math.signum(vertical), 0, maxScroll);
            return true;
        }
        return super.mouseScrolled(x, y, horizontal, vertical);
    }
}
