package com.hbm.client.refinery;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.refinery.RefineryRecipe;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Source RefineryRecipeHandler:1000mB display scale and exact five-output coordinates. */
final class RefineryViews {
    static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei_refinery.png");
    static final int[][] OUTPUT={{111,6},{129,15},{111,24},{129,33},{111,42}};
    static ItemStack input(RefineryRecipe recipe){return FluidIconItem.make(recipe.data().inputFluids().getFirst());}
    static List<ItemStack> outputs(RefineryRecipe recipe){var outputs=new ArrayList<ItemStack>();for(var fluid:recipe.data().outputFluids())outputs.add(FluidIconItem.make(fluid));outputs.add(recipe.solid());return outputs;}
    static void draw(GuiGraphics graphics,int x,int y){graphics.blit(TEXTURE,x,y,5,11,166,65,256,256);int height=(int)((1F-(System.currentTimeMillis()/50%480)/480F)*52);if(height>0)graphics.blit(TEXTURE,x+3,y+6+52-height,0,86+52-height,16,height,256,256);int width=(int)(System.currentTimeMillis()/50%48)*24/48;if(width>0)graphics.blit(TEXTURE,x+78,y+24,16,86,width,17,256,256);}
}
