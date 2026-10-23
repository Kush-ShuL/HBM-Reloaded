package com.hbm.client.oilliquefactor;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.oilliquefactor.LiquefactorRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
final class LiquefactorViews {
 static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");
 static ItemStack output(LiquefactorRecipe r){return FluidIconItem.make(r.output().type,r.output().fill,0);}
 static void draw(GuiGraphics g,int x,int y){com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,TEXTURE,x,y);g.blit(TEXTURE,x+47,y+23,5,87,18,18,256,256);g.blit(TEXTURE,x+101,y+23,5,87,18,18,256,256);g.blit(TEXTURE,x+74,y+14,59,87,18,36,256,256);}
}
