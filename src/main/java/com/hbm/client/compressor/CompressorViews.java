package com.hbm.client.compressor;
import com.hbm.fluid.*;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.compressor.*;
import com.hbm.recipe.generic.GenericRecipeData;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
/** Source fluid icons and NEI layout. Show every actual pressure route, including source NEI's hidden same-type routes. */
final class CompressorViews {
 static List<CompressorRecipe> recipes(RecipeManager manager){var result=new ArrayList<>(CompressorRecipeConfig.recipes(manager));for(var type:HbmFluids.getInNiceOrder()){if(type==HbmFluids.NONE)continue;for(int pressure=0;pressure<5;pressure++){if(CompressorRecipeConfig.find(manager,type,pressure)!=null)continue;var input=new FluidStack(type,1000,pressure);var output=new FluidStack(type,1000,pressure+1);result.add(new CompressorRecipe(new GenericRecipeData("compressor.generic."+type.getName()+"."+pressure,100,2500,List.of(),List.of(input),List.of(),List.of(output),List.of(),"",ItemStack.EMPTY,false,"",result.size())));}}return result;}
 static ItemStack input(CompressorRecipe recipe){return FluidIconItem.make(recipe.inputFluids().getFirst());}static ItemStack output(CompressorRecipe recipe){return FluidIconItem.make(recipe.outputFluids().getFirst());}
 static void draw(GuiGraphics graphics,int x,int y){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(graphics,texture,x,y);graphics.blit(texture,x+47,y+23,5,87,18,18,256,256);graphics.blit(texture,x+101,y+23,5,87,18,18,256,256);graphics.blit(texture,x+74,y+14,59,87,18,36,256,256);}
 private CompressorViews(){}
}
