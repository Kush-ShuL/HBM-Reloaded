package com.hbm.machine.reactor.zirnox.client;
import net.minecraft.client.gui.GuiGraphics;
/** Original NEIUniversalHandler one-input/one-output coordinates and machine rectangle. */
final class ZirnoxFuelRecipeView {
    static void draw(GuiGraphics graphics,int x,int y){var texture=ZirnoxRenderer.id("textures/gui/nei/gui_nei.png");graphics.blit(texture,x+47,y+23,5,87,18,18);graphics.blit(texture,x+101,y+23,5,87,18,18);graphics.blit(texture,x+74,y+14,59,87,18,36);}
}
