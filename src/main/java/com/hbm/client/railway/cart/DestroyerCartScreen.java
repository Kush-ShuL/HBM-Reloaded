package com.hbm.client.railway.cart;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.railway.cart.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
public final class DestroyerCartScreen extends LegacyContainerScreen<DestroyerCartMenu> {
    public DestroyerCartScreen(DestroyerCartMenu menu,Inventory inventory,Component title){super(menu,inventory,title,SourceCartContent.id("textures/gui/cart/gui_destroyer.png"),176,166);}
    @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,88-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,74,4210752,false);}
    @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);int index=(int)(System.currentTimeMillis()%1000)/128;int v=switch(index){case 1,7->166;case 2,6->182;case 3,5->198;case 4->214;default->-1;};if(v>=0)drawLegacyRegion(g,66,35,0,v,44,16);}
}
