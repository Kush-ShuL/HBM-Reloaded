package com.hbm.client.bomb.conventional.grenade;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
/** Original NEIUniversalHandler background and the exact three/four-input, output and workbench coordinates. */
final class GrenadeRecipeDrawing {
 private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");
 private GrenadeRecipeDrawing(){}
 static int[][] inputs(int count){return count==3?new int[][]{{48,24},{30,24},{12,24}}:new int[][]{{48,15},{30,15},{48,33},{30,33}};}
 static void draw(GuiGraphics g,int count,int x,int y){com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,TEXTURE,x,y);for(var point:inputs(count))g.blit(TEXTURE,x+point[0]-1,y+point[1]-1,5,87,18,18,256,256);g.blit(TEXTURE,x+101,y+23,5,87,18,18,256,256);g.blit(TEXTURE,x+74,y+14,59,87,18,36,256,256);}
}
