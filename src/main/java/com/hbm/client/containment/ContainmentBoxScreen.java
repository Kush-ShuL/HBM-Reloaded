package com.hbm.client.containment;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.containment.ContainmentBoxMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** Unmodified source GUI176x186, centered actual name, source grey text, selected Minecraft/ModernUI font. */
public final class ContainmentBoxScreen extends LegacyContainerScreen<ContainmentBoxMenu> {
    public ContainmentBoxScreen(ContainmentBoxMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/gui_containment.png"),176,186);}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){
        var name=menu.box.displayName();graphics.drawString(font,name,imageWidth/2-font.width(name)/2,6,4210752,false);
        graphics.drawString(font,playerInventoryTitle,8,imageHeight-96+2,4210752,false);
    }
}
