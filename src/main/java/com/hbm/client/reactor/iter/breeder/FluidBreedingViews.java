package com.hbm.client.reactor.iter.breeder;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.reactor.iter.breeder.FluidBreedingRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Source FusionBreederHandler actual three fluid recipes; solids remain in the unique shared irradiation category. */
final class FluidBreedingViews {
 static ItemStack input(FluidBreedingRecipe recipe){return FluidIconItem.make(recipe.input());}static ItemStack output(FluidBreedingRecipe recipe){return FluidIconItem.make(recipe.output());}
 static void draw(GuiGraphics g,int x,int y){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,texture,x,y);g.blit(texture,x+47,y+23,5,87,18,18,256,256);g.blit(texture,x+101,y+23,5,87,18,18,256,256);g.blit(texture,x+74,y+14,59,87,18,36,256,256);g.drawString(com.hbm.client.gui.font.LegacyFonts.get(),Component.translatable("gui.hbm.fusion_breeder.requirement"),x+4,y+54,0x404040,false);}
}
