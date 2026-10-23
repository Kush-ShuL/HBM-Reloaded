package com.hbm.client.machine.pa.cooled;
import com.hbm.machine.pa.recipe.PaRecipe;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
final class PaRecipeViews{
 static void draw(PaRecipe recipe,GuiGraphics g,int x,int y){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,texture,x,y);for(int point:new int[]{29,47,101})g.blit(texture,x+point,y+23,5,87,18,18,256,256);if(recipe.output2().isPresent())g.blit(texture,x+119,y+23,5,87,18,18,256,256);g.blit(texture,x+74,y+14,59,87,18,36,256,256);g.drawString(com.hbm.client.gui.font.LegacyFonts.get(),Component.translatable("gui.hbm.pa.momentum",String.format(Locale.US,"%,d",recipe.momentum())),x+8,y+52,0x404040,false);}private PaRecipeViews(){}
}
