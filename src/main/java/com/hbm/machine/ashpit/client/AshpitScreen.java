package com.hbm.machine.ashpit.client;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.ashpit.AshpitMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
public final class AshpitScreen extends LegacyContainerScreen<AshpitMenu> {
 public AshpitScreen(AshpitMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/machine/gui_ashpit.png"),176,168);}@Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,(imageWidth-font.width(title))/2,6,0x404040,false);g.drawString(font,playerInventoryTitle,8,74,0x404040,false);}
}
