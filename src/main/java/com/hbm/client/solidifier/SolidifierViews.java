package com.hbm.client.solidifier;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.solidifier.SolidifierRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
final class SolidifierViews {
 static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");
 static ItemStack input(SolidifierRecipe recipe){return FluidIconItem.make(recipe.input().type,recipe.input().fill,0);}
 static void draw(GuiGraphics g,int x,int y){com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,TEXTURE,x,y);g.blit(TEXTURE,x+47,y+23,5,87,18,18,256,256);g.blit(TEXTURE,x+101,y+23,5,87,18,18,256,256);g.blit(TEXTURE,x+74,y+14,59,87,18,36,256,256);}
}
