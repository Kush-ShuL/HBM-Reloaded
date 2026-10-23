package com.hbm.client.railway.cart;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.railway.cart.CrateCartMenu;
import com.hbm.machine.storage.StorageCrateKind;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
public final class CrateCartScreen extends LegacyContainerScreen<CrateCartMenu> {
    public CrateCartScreen(CrateCartMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse(StorageCrateKind.STEEL.texture()),176,222);}
    @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,88-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,128,4210752,false);}
    @Override public boolean mouseClicked(double x,double y,int button){boolean touch=minecraft.options.touchscreen().get();minecraft.options.touchscreen().set(false);try{return super.mouseClicked(x,y,button);}finally{minecraft.options.touchscreen().set(touch);}}
}
