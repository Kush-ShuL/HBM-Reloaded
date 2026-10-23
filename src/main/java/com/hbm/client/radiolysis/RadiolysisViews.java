package com.hbm.client.radiolysis;
import com.hbm.fluid.HbmFluids;import com.hbm.item.fluid.FluidIconItem;import com.hbm.machine.radiolysis.*;import java.util.*;import net.minecraft.client.gui.GuiGraphics;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.item.ItemStack;
/** Exact original NEI positioned stacks and60tick horizontal64x28 progress overlay. */
final class RadiolysisViews {
 static final ResourceLocation TEXTURE=RadiolysisContent.id("textures/gui/nei/gui_nei_radiolysis.png");static final int[][] OUTPUT={{118,16},{118,34}};
 static ItemStack input(RadiolysisRecipe recipe){return FluidIconItem.make(recipe.data().inputFluids().getFirst());}
 static List<ItemStack> outputs(RadiolysisRecipe recipe){return recipe.data().outputFluids().stream().map(fluid->fluid.type==HbmFluids.NONE?new ItemStack(com.hbm.registry.HbmContent.item("nothing")):FluidIconItem.make(fluid)).toList();}
 static void draw(GuiGraphics graphics,int x,int y){graphics.blit(TEXTURE,x,y,5,11,166,65,256,256);int width=(int)(System.currentTimeMillis()/50%60)*64/60;if(width>0)graphics.blit(TEXTURE,x+52,y+19,5,87,width,28,256,256);}
 private RadiolysisViews(){}
}
