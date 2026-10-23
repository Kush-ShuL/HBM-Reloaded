package com.hbm.client.reactor.watz;

import com.hbm.machine.reactor.watz.fuel.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** All12 actual source WatzRecipeHandler fresh→depleted pairs; no surrogate recipes. */
final class WatzViews {
    record View(ItemStack fresh,ItemStack depleted){}
    static List<View> recipes(){var result=new ArrayList<View>();for(int i=0;i<WatzFuelType.values().length;i++)result.add(new View(WatzFuelContent.stack(false,i),WatzFuelContent.stack(true,i)));return result;}
    static void draw(GuiGraphics graphics,int x,int y){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");graphics.blit(texture,x+47,y+23,5,87,18,18,256,256);graphics.blit(texture,x+101,y+23,5,87,18,18,256,256);graphics.blit(texture,x+74,y+14,59,87,18,36,256,256);}
    private WatzViews(){}
}
