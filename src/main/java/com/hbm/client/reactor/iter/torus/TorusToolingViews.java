package com.hbm.client.reactor.iter.torus;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
final class TorusToolingViews {
    static void draw(GuiGraphics graphics,int x,int y){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");graphics.blit(texture,x+47,y+23,5,87,18,18,256,256);graphics.blit(texture,x+101,y+23,5,87,18,18,256,256);graphics.blit(texture,x+74,y+14,59,87,18,36,256,256);}
}
