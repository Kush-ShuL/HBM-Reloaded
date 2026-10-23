package com.hbm.client.bomb.conventional;

import com.hbm.bomb.conventional.MultiBombMenu;
import com.hbm.client.gui.LegacyContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** Source GUIBombMulti: original 176x166 bitmap, slot grid, labels and matching/mixed-payload icon. */
public final class MultiBombScreen extends LegacyContainerScreen<MultiBombMenu> {
    public MultiBombScreen(MultiBombMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/weapon/bombgeneric.png"),176,166);}
    @Override protected void renderLabels(GuiGraphics g,int mouseX,int mouseY){g.drawString(font,title,88-font.width(title)/2,6,4210752,false);g.drawString(font,Component.translatable("container.inventory"),8,72,4210752,false);}
    @Override protected void renderBg(GuiGraphics g,float partial,int mouseX,int mouseY){super.renderBg(g,partial,mouseX,mouseY);int a=menu.type(2),b=menu.type(5);if(a!=b)drawLegacyRegion(g,124,34,176,7*18,18,18);else if(a>0)drawLegacyRegion(g,124,34,176,(a-1)*18,18,18);}
}
