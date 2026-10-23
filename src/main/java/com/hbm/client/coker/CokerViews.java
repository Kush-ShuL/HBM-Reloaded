package com.hbm.client.coker;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.coker.CokerRecipe;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Source CokingHandler/NEIUniversalHandler: one fluid input and original solid/byproduct output order. */
final class CokerViews {
    static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei.png");
    static final int[][] OUTPUT={{102,24},{120,24}};
    static ItemStack input(CokerRecipe recipe){return FluidIconItem.make(recipe.data().inputFluids().getFirst());}
    static List<ItemStack> outputs(CokerRecipe recipe){var outputs=new ArrayList<ItemStack>();if(!recipe.solid().isEmpty())outputs.add(recipe.solid().copy());if(recipe.byproduct()!=null)outputs.add(FluidIconItem.make(recipe.byproduct()));return outputs;}
    static void draw(GuiGraphics graphics,int x,int y){com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(graphics,TEXTURE,x,y);graphics.blit(TEXTURE,x+47,y+23,5,87,18,18,256,256);for(int[] point:OUTPUT)graphics.blit(TEXTURE,x+point[0]-1,y+point[1]-1,5,87,18,18,256,256);graphics.blit(TEXTURE,x+74,y+14,59,87,18,36,256,256);}
}
