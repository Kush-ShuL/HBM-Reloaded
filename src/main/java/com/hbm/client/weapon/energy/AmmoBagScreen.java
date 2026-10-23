package com.hbm.client.weapon.energy;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.weapon.sedna.gun.energy.bag.AmmoBagMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
public final class AmmoBagScreen extends LegacyContainerScreen<AmmoBagMenu>{
 public AmmoBagScreen(AmmoBagMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/gui_ammo_bag.png"),176,168);}
 @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,imageWidth/2-font.width(title)/2,6,0xffffff,false);graphics.drawString(font,playerInventoryTitle,8,imageHeight-98,4210752,false);}
}
