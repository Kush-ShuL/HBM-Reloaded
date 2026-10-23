package com.hbm.client.ammopress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
/** Viewer slots retain the actual three-by-three input order rather than flattening null source cells. */
final class AmmoPressViews {
 static void draw(GuiGraphics g,int x,int y){for(int row=0;row<3;row++)for(int col=0;col<3;col++)slot(g,x+7+col*18,y+3+row*18);slot(g,x+115,y+21);g.drawString(Minecraft.getInstance().font,"→",x+79,y+27,0x404040,false);}
 private static void slot(GuiGraphics g,int x,int y){g.fill(x,y,x+18,y+18,0xff373737);g.fill(x+1,y+1,x+18,y+18,0xffffffff);g.fill(x+1,y+1,x+17,y+17,0xff8b8b8b);}
 private AmmoPressViews(){}
}
