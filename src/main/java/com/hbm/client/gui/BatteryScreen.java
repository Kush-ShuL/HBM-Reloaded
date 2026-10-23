package com.hbm.client.gui;

import com.hbm.HbmReloaded;
import com.hbm.item.battery.BatteryItem;
import com.hbm.machine.battery.BatteryMachineContent;
import com.hbm.machine.battery.BatteryMenu;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Exact original 176x166 panel, sprite UVs, slot positions and click rectangles. */
@EventBusSubscriber(modid = HbmReloaded.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class BatteryScreen extends LegacyContainerScreen<BatteryMenu> {
    private static final ResourceLocation BACKGROUND = HbmReloaded.id("textures/gui/storage/gui_battery.png");

    public BatteryScreen(BatteryMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, BACKGROUND, 176, 166); }
    @SubscribeEvent public static void registerScreens(RegisterMenuScreensEvent event) { event.register(BatteryMachineContent.MENU.get(), BatteryScreen::new); }

    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        Component name = title.copy().append(" (" + menu.power() + " HE)");
        graphics.drawString(font, name, imageWidth / 2 - font.width(name) / 2, 6, 4_210_752, false);
        graphics.drawString(font, playerInventoryTitle, 8, imageHeight - 96 + 2, 4_210_752, false);
    }

    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTick, mouseX, mouseY);
        if (menu.power() > 0) {
            int charge = menu.scaledPower(52);
            drawLegacyRegion(graphics, 62, 69 - charge, 176, 52 - charge, 52, charge);
        }
        drawLegacyRegion(graphics, 133, 16, 176, 52 + menu.redLow() * 18, 18, 18);
        drawLegacyRegion(graphics, 133, 52, 176, 52 + menu.redHigh() * 18, 18, 18);
        drawLegacyRegion(graphics, 152, 35, 194, 52 + menu.priority() * 16 - 16, 16, 16);
    }

    @Override public boolean mouseClicked(double x, double y, int button) {
        boolean handled = super.mouseClicked(x, y, button);
        int action = legacyInfoHovered(x, y, 133, 16, 18, 18) ? 0
                : legacyInfoHovered(x, y, 133, 52, 18, 18) ? 1
                : legacyInfoHovered(x, y, 152, 35, 16, 16) ? 2 : -1;
        if (action < 0 || minecraft == null || minecraft.gameMode == null) return handled;
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action);
        return true;
    }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        if (legacyInfoHovered(mouseX, mouseY, 62, 17, 52, 52)) {
            long delta = menu.delta();
            String change = (delta < 0 ? "-" : "+") + BatteryItem.shortNumber(Math.abs(delta)) + "HE/s";
            graphics.renderComponentTooltip(font, List.of(
                    Component.literal(BatteryItem.shortNumber(menu.power()) + "/" + BatteryItem.shortNumber(menu.capacity()) + "HE"),
                    Component.literal(change).withStyle(delta > 0 ? ChatFormatting.GREEN : delta < 0 ? ChatFormatting.RED : ChatFormatting.YELLOW)), mouseX, mouseY);
        }
        if (legacyInfoHovered(mouseX, mouseY, 152, 35, 16, 16)) {
            String priority = switch (menu.priority()) { case 1 -> "low"; case 3 -> "high"; default -> "normal"; };
            var lines = new ArrayList<Component>();
            lines.add(Component.translatable("battery.priority." + priority));
            lines.add(Component.translatable("battery.priority.recommended"));
            for (String line : I18n.get("battery.priority." + priority + ".desc").split("\\$")) lines.add(Component.literal(line));
            graphics.renderComponentTooltip(font, lines, mouseX, mouseY);
        }
    }
}
