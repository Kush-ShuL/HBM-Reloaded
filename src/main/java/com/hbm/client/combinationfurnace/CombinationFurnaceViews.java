package com.hbm.client.combinationfurnace;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.combinationfurnace.CombinationFurnaceRecipe;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Actual CombinationHandler/NEIUniversalHandler one input, optional solid then fluid icon, and machine catalyst. */
final class CombinationFurnaceViews {
    static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");static final int[][] OUTPUT={{102,24},{120,24}};
    static List<ItemStack> inputs(CombinationFurnaceRecipe recipe){return recipe.input().processingExamples();}
    static List<ItemStack> outputs(CombinationFurnaceRecipe recipe){var values=new ArrayList<ItemStack>();if(!recipe.solid().isEmpty())values.add(recipe.solid());if(recipe.fluid()!=null)values.add(FluidIconItem.make(recipe.fluid()));return values;}
    static void draw(CombinationFurnaceRecipe recipe,GuiGraphics g,int x,int y){com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,TEXTURE,x,y);g.blit(TEXTURE,x+47,y+23,5,87,18,18,256,256);for(int i=0;i<outputs(recipe).size();i++){int[] point=OUTPUT[i];g.blit(TEXTURE,x+point[0]-1,y+point[1]-1,5,87,18,18,256,256);}g.blit(TEXTURE,x+74,y+14,59,87,18,36,256,256);}
    private CombinationFurnaceViews(){}
}
