package com.hbm.client.cyclotron;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
final class CyclotronViews {
 static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei_cyclotron.png");
 static void draw(GuiGraphics g,int x,int y){com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,TEXTURE,x,y);int progress=(int)(System.currentTimeMillis()/50%48)*24/48;g.blit(TEXTURE,x+44,y+24,100,119,progress,16,256,256);}
}
