package com.hbm.client.storagedrum;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.storagedrum.StorageDrumEntity;
import com.hbm.machine.storagedrum.StorageDrumMenu;
import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** Original176x234 GUI and106px source atlas bars, not generic tinted fluid tiles or a fabricated progress bar. */
public final class StorageDrumScreen extends LegacyContainerScreen<StorageDrumMenu> {
    public StorageDrumScreen(StorageDrumMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, ResourceLocation.parse("hbm:textures/gui/processing/gui_drum.png"), 176, 234);
    }
    private StorageDrumEntity drum() {
        return minecraft != null && minecraft.level != null && minecraft.level.getBlockEntity(menu.position()) instanceof StorageDrumEntity drum ? drum : null;
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 88 - font.width(title) / 2, 6, 4210752, false);
        graphics.drawString(font, playerInventoryTitle, 8, 140, 4210752, false);
    }
    @Override protected void renderBg(GuiGraphics graphics, float partial, int mouseX, int mouseY) {
        super.renderBg(graphics, partial, mouseX, mouseY);
        var drum = drum();
        if (drum == null) return;
        int liquid = drum.tanks[0].getFill() * 106 / drum.tanks[0].getMaxFill();
        drawLegacyRegion(graphics, 17, 130 - liquid, 176, 106 - liquid, 7, liquid);
        int gas = drum.tanks[1].getFill() * 106 / drum.tanks[1].getMaxFill();
        drawLegacyRegion(graphics, 152, 130 - gas, 183, 106 - gas, 7, gas);
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        super.render(graphics, mouseX, mouseY, partial);
        var drum = drum();
        if (drum == null) return;
        if (hovered(mouseX, mouseY, 16)) info(graphics, drum.tanks[0], mouseX, mouseY);
        if (hovered(mouseX, mouseY, 151)) info(graphics, drum.tanks[1], mouseX, mouseY);
    }
    private boolean hovered(int mouseX, int mouseY, int x) {
        return leftPos + x <= mouseX && mouseX < leftPos + x + 9 && topPos + 23 < mouseY && mouseY <= topPos + 131;
    }
    private void info(GuiGraphics graphics, FluidTank tank, int x, int y) {
        var lines = new ArrayList<Component>();
        lines.add(tank.getTankType().displayName());
        lines.add(Component.literal(tank.getFill() + "/" + tank.getMaxFill() + "mB"));
        if (tank.getPressure() != 0) {
            lines.add(Component.translatable("gui.hbm.storage_drum.pressure", tank.getPressure()).withStyle(ChatFormatting.RED));
            lines.add(Component.translatable("gui.hbm.storage_drum.pressurized").withStyle(System.currentTimeMillis() % 1000 < 500 ? ChatFormatting.RED : ChatFormatting.DARK_RED));
        }
        FluidInfo.append(tank.getTankType(), lines, hasShiftDown());
        graphics.renderComponentTooltip(font, lines, x, y);
    }
}
