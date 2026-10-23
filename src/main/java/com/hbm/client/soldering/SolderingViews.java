package com.hbm.client.soldering;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;import com.hbm.item.fluid.FluidIconItem;import com.hbm.machine.soldering.*;import java.util.*;import net.minecraft.client.gui.GuiGraphics;import net.minecraft.network.chat.Component;import net.minecraft.world.item.ItemStack;
/** Source NEIUniversalHandler layout, including machine slot and exact right-aligned43/55 labels. */
final class SolderingViews {
 private static final LegacyRecipeDisplays.Category CATEGORY=new LegacyRecipeDisplays.Category("soldering","jei.hbm.soldering","universal",SolderingContent.id("textures/gui/nei/gui_nei.png"),166,65,List.of());
 static List<List<ItemStack>> inputs(SolderingRecipe recipe){var result=new ArrayList<List<ItemStack>>();for(var input:recipe.inputItems())result.add(input.examples());if(recipe.fluid()!=null)result.add(List.of(FluidIconItem.make(recipe.fluid())));return result;}
 static int[][] positions(SolderingRecipe recipe){return LegacyRecipeDisplays.inputCoordinates(CATEGORY,inputs(recipe).size());}
 static void draw(SolderingRecipe recipe,GuiGraphics graphics,int x,int y){var texture=CATEGORY.texture();LegacyDisplayDrawing.recipeBackground(graphics,texture,x,y);for(var point:positions(recipe))graphics.blit(texture,x+point[0]-1,y+point[1]-1,5,87,18,18,256,256);graphics.blit(texture,x+101,y+23,5,87,18,18,256,256);graphics.blit(texture,x+74,y+14,59,87,18,36,256,256);var font=com.hbm.client.gui.font.LegacyFonts.get();var duration=Component.translatable("gui.hbm.soldering.duration",String.format(Locale.US,"%,d",recipe.duration()));var consumption=Component.translatable("gui.hbm.soldering.power",String.format(Locale.US,"%,d",recipe.power()));graphics.drawString(font,duration,x+160-font.width(duration),y+43,0x404040,false);graphics.drawString(font,consumption,x+160-font.width(consumption),y+55,0x404040,false);}
 private SolderingViews(){}
}
