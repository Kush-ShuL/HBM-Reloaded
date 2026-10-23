package com.hbm.machine.reactor.client;
import com.hbm.machine.reactor.BreedingRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
/** Reuses the original breeder GUI source rectangle for recipe viewers. */
final class BreedingRecipeView {
    static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/processing/gui_breeder.png");
    static void draw(GuiGraphics graphics,BreedingRecipe recipe,int x,int y){graphics.blit(TEXTURE,x,y,5,11,166,60);int fill=(int)((System.currentTimeMillis()/50%400)/400F*70);if(fill>0)graphics.blit(TEXTURE,x+48,y+21,176,0,fill,20);var font=com.hbm.client.gui.font.LegacyFonts.get();String flux=Integer.toString(recipe.flux());graphics.drawString(font,flux,x+83-font.width(flux)/2,y+10,0x08ff00,false);}
}
