package com.hbm.client.oilhydrotreater;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.oilhydrotreater.HydrotreaterRecipe;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Actual HydrotreatingHandler/UniversalHandler two-fluid input/output layout, per1000mB viewer scale. */
final class HydrotreaterViews {
 static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");static final int[][] INPUT={{48,24},{30,24}},OUTPUT={{102,24},{120,24}};
 static List<ItemStack> inputs(HydrotreaterRecipe recipe){return recipe.inputFluids().stream().map(f->FluidIconItem.make(f.type,Math.multiplyExact(f.fill,10),f.pressure)).toList();}
 static List<ItemStack> outputs(HydrotreaterRecipe recipe){return recipe.outputFluids().stream().map(f->FluidIconItem.make(f.type,Math.multiplyExact(f.fill,10),0)).toList();}
 static void draw(GuiGraphics g,int x,int y){com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,TEXTURE,x,y);for(int[] point:INPUT)g.blit(TEXTURE,x+point[0]-1,y+point[1]-1,5,87,18,18,256,256);for(int[] point:OUTPUT)g.blit(TEXTURE,x+point[0]-1,y+point[1]-1,5,87,18,18,256,256);g.blit(TEXTURE,x+74,y+14,59,87,18,36,256,256);}
 private HydrotreaterViews(){}
}
