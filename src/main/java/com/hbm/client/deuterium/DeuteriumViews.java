package com.hbm.client.deuterium;

import com.hbm.item.fluid.FluidIconItem;
import com.hbm.fluid.HbmFluids;
import com.hbm.machine.deuterium.DeuteriumProcess;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** The actual DeuteriumHandler's sole source row, shared by both viewers and the conversion ratio descriptor. */
final class DeuteriumViews {
    enum Recipe { WATER_TO_HEAVYWATER }
    static ItemStack input(){return FluidIconItem.make(HbmFluids.WATER,DeuteriumProcess.VIEW_WATER,0);}
    static ItemStack output(){return FluidIconItem.make(HbmFluids.fromName("HEAVYWATER"),DeuteriumProcess.VIEW_HEAVY,0);}
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");
    static void draw(GuiGraphics g,int x,int y){com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,TEXTURE,x,y);g.blit(TEXTURE,x+47,y+23,5,87,18,18,256,256);g.blit(TEXTURE,x+101,y+23,5,87,18,18,256,256);g.blit(TEXTURE,x+74,y+14,59,87,18,36,256,256);}
}
