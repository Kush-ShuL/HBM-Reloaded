package com.hbm.client.casingbag;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.casingbag.CasingBagMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** Source GUICasingBag:176x186, exact unscaled PNG and foreground coordinates/colours. */
public final class CasingBagScreen extends LegacyContainerScreen<CasingBagMenu> {
    public CasingBagScreen(CasingBagMenu menu,Inventory inventory,Component title) {
        super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/gui_casing_bag.png"),176,186);
    }
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y) {
        var name=menu.bag.displayName();
        graphics.drawString(font,name,imageWidth/2-font.width(name)/2,6,0xffffff,false);
        graphics.drawString(font,playerInventoryTitle,8,imageHeight-98,4210752,false);
    }
}
