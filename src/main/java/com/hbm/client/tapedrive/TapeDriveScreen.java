package com.hbm.client.tapedrive;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.tapedrive.TapeDriveMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Original176x186 GUI without invented buttons, progress bars, power gauges or file browser. */
public final class TapeDriveScreen extends LegacyContainerScreen<TapeDriveMenu> {
    public TapeDriveScreen(TapeDriveMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/machine/gui_tape_drive.png"),176,186);}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,88-font.width(title)/2,6,0xffffff,false);graphics.drawString(font,playerInventoryTitle,8,92,4210752,false);}
}
