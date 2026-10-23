package com.hbm.client.fractiontower;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.fractiontower.FractionRecipe;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Source VacuumRecipeHandler/NEIUniversalHandler, native pressure2 input and four1000mB-scale outputs. */
final class FractionViews {
    static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei.png");
    static final int[][] OUTPUT={{102,24},{120,24}};
    static ItemStack input(FractionRecipe recipe){return FluidIconItem.make(recipe.data().inputFluids().getFirst());}
    static List<ItemStack> outputs(FractionRecipe recipe){return recipe.data().outputFluids().stream().map(fluid->FluidIconItem.make(fluid.type,fluid.fill,0)).toList();}
    static void draw(GuiGraphics graphics,int x,int y){com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(graphics,TEXTURE,x,y);graphics.blit(TEXTURE,x+47,y+23,5,87,18,18,256,256);for(int[] point:OUTPUT)graphics.blit(TEXTURE,x+point[0]-1,y+point[1]-1,5,87,18,18,256,256);graphics.blit(TEXTURE,x+74,y+14,59,87,18,36,256,256);}
}
