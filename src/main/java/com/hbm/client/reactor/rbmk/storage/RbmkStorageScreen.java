package com.hbm.client.reactor.rbmk.storage;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.reactor.rbmk.storage.RbmkStorageMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
public final class RbmkStorageScreen extends LegacyContainerScreen<RbmkStorageMenu> {
    public RbmkStorageScreen(RbmkStorageMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/reactors/gui_rbmk_storage.png"),176,186);}
    @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,imageWidth/2-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,imageHeight-96+2,4210752,false);}
}
