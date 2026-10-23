package com.hbm.client.reactor.fuelpool;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.reactor.fuelpool.*;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Original176x189 GUI/utility icon and fixed-anchor three-line source information popup. */
public final class FuelPoolScreen extends LegacyContainerScreen<FuelPoolMenu> {
    public FuelPoolScreen(FuelPoolMenu menu,Inventory inventory,Component title){super(menu,inventory,title,FuelPoolContent.id("textures/gui/gui_waste_drum.png"),176,189);}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,imageWidth/2-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,98,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);graphics.blit(FuelPoolContent.id("textures/gui/gui_utility.png"),leftPos-16,topPos+36,8,0,16,16,256,256);}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);if(legacyInfoHovered(x,y,-16,36,16,16))graphics.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.fuel_pool.info.1"),Component.translatable("gui.hbm.fuel_pool.info.2"),Component.translatable("gui.hbm.fuel_pool.info.3")),leftPos-8,topPos+52);}
}
