package com.hbm.client.pyrooven;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.pyrooven.PyroOvenRecipe;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Original NEI fluid-before-solid input and solid-before-fluid output ordering. */
final class PyroOvenViews {
 static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");
 static List<List<ItemStack>> inputs(PyroOvenRecipe recipe){var result=new ArrayList<List<ItemStack>>();if(recipe.inputFluid()!=null)result.add(List.of(FluidIconItem.make(recipe.inputFluid())));if(recipe.inputItem()!=null)result.add(recipe.inputItem().processingExamples());return result;}
 static List<ItemStack> outputs(PyroOvenRecipe recipe){var result=new ArrayList<ItemStack>();if(!recipe.outputItem().isEmpty())result.add(recipe.outputItem().copy());if(recipe.outputFluid()!=null)result.add(FluidIconItem.make(recipe.outputFluid()));return result;}
 static void draw(PyroOvenRecipe recipe,GuiGraphics graphics,int x,int y){com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(graphics,TEXTURE,x,y);for(int i=0;i<inputs(recipe).size();i++)graphics.blit(TEXTURE,x+47-i*18,y+23,5,87,18,18,256,256);for(int i=0;i<outputs(recipe).size();i++)graphics.blit(TEXTURE,x+101+i*18,y+23,5,87,18,18,256,256);graphics.blit(TEXTURE,x+74,y+14,59,87,18,36,256,256);}
 private PyroOvenViews(){}
}
