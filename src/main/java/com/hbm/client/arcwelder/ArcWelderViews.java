package com.hbm.client.arcwelder;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;import com.hbm.item.fluid.FluidIconItem;import com.hbm.machine.arcwelder.*;import java.util.*;import net.minecraft.client.gui.GuiGraphics;import net.minecraft.network.chat.Component;import net.minecraft.world.item.ItemStack;
/** Source NEIUniversalHandler layout, including machine slot and exact right-aligned45/57 labels. */
final class ArcWelderViews {
 private static final LegacyRecipeDisplays.Category CATEGORY=new LegacyRecipeDisplays.Category("arcwelder","jei.hbm.arcwelder","universal",ArcWelderContent.id("textures/gui/nei/gui_nei.png"),166,65,List.of());
 static List<List<ItemStack>> inputs(ArcWelderRecipe recipe){var result=new ArrayList<List<ItemStack>>();for(var input:recipe.inputItems())result.add(input.examples());if(recipe.fluid()!=null)result.add(List.of(FluidIconItem.make(recipe.fluid())));return result;}
 static int[][] positions(ArcWelderRecipe recipe){return LegacyRecipeDisplays.inputCoordinates(CATEGORY,inputs(recipe).size());}
 static void draw(ArcWelderRecipe recipe,GuiGraphics graphics,int x,int y){var texture=CATEGORY.texture();LegacyDisplayDrawing.recipeBackground(graphics,texture,x,y);for(var point:positions(recipe))graphics.blit(texture,x+point[0]-1,y+point[1]-1,5,87,18,18,256,256);graphics.blit(texture,x+101,y+23,5,87,18,18,256,256);graphics.blit(texture,x+74,y+14,59,87,18,36,256,256);var font=com.hbm.client.gui.font.LegacyFonts.get();var duration=Component.translatable("gui.hbm.arcwelder.duration",shortNumber(recipe.duration()));var consumption=Component.translatable("gui.hbm.arcwelder.power",shortNumber(recipe.power()));graphics.drawString(font,duration,x+164-font.width(duration),y+45,0x404040,false);graphics.drawString(font,consumption,x+164-font.width(consumption),y+57,0x404040,false);}
private static String shortNumber(long l) {
		
		double res;
		String suffix = "";
		long abs = Math.abs(l);

		if(abs >= Math.pow(10, 18)) {
			res = l / Math.pow(10, 18);
			suffix = "E";
		}
		else if(abs >= Math.pow(10, 15)) {
			res = l / Math.pow(10, 15);
			suffix = "P";
		}
		else if(abs >= Math.pow(10, 12)) {
			res = l / Math.pow(10, 12);
			suffix = "T";
		}
		else if(abs >= Math.pow(10, 9)) {
			res = l / Math.pow(10, 9);
			suffix = "G";
		}
		else if(abs >= Math.pow(10, 6)) {
			res = l / Math.pow(10, 6);
			suffix = "M";
		}
		else if(abs >= Math.pow(10, 3)) {
			res = l / Math.pow(10, 3);
			suffix = "k";
		}
		else {
			return Long.toString(l);
		}

		// Edgecase: a negative triple digit number would result in a 8 character long result so we will loose one decimal place
		if (res <= -100.0) {
			res = Math.round(res * 10.0) / 10.0;
		} else {
			res = Math.round(res * 100.0) / 100.0;
		}

		return res + suffix;
	}
 private ArcWelderViews(){}
}
