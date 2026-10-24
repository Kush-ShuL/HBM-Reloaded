package com.hbm.client.crystallizer;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.crystallizer.CrystallizerRecipe;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
final class CrystallizerViews {
 static ItemStack fluid(CrystallizerRecipe r){return FluidIconItem.make(r.inputFluids().getFirst());}static List<ItemStack> items(CrystallizerRecipe r){return r.inputItems().getFirst().processingExamples();}static ItemStack output(CrystallizerRecipe r){return r.outputItems().getFirst().single();}
 static boolean visible(CrystallizerRecipe recipe){var inputs=items(recipe);boolean sourceVisible=recipe.keyKind().equals("tag")||inputs.stream().noneMatch(stack->stack.getItem() instanceof com.hbm.material.crystallizer.PlasticScrapItem);return com.hbm.compat.recipeviewer.RecipeViewerVisibility.visible(recipe.data().pools(),List.of(inputs),List.of(List.of(output(recipe))),sourceVisible);}
 static void draw(CrystallizerRecipe recipe,GuiGraphics g,int x,int y){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,texture,x,y);for(int point:new int[]{29,47,101})g.blit(texture,x+point,y+23,5,87,18,18,256,256);g.blit(texture,x+74,y+14,59,87,18,36,256,256);if(recipe.productivity()>0)g.drawString(com.hbm.client.gui.font.LegacyFonts.get(),Component.translatable("gui.hbm.crystallizer.effectiveness",Math.min((int)(recipe.productivity()*100),99)),x+8,y+52,0x404040,false);}
 private CrystallizerViews(){}
}
