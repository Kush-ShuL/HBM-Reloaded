package com.hbm.client.oilcatalyticcracker;
import com.hbm.fluid.HbmFluids;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.oilcatalyticcracker.CatalyticCrackerRecipe;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Source CrackingHandler's exact two inputs and output tuple, hiding only the original NONE second output. */
final class CatalyticCrackerViews {
 static List<ItemStack> inputs(CatalyticCrackerRecipe recipe){return recipe.inputFluids().stream().map(FluidIconItem::make).toList();}
 static List<ItemStack> outputs(CatalyticCrackerRecipe recipe){var outputs=recipe.outputFluids();return outputs.get(1).type==HbmFluids.NONE?List.of(FluidIconItem.make(outputs.get(0)),FluidIconItem.make(outputs.get(2))):outputs.stream().map(FluidIconItem::make).toList();}
 static void draw(CatalyticCrackerRecipe recipe,GuiGraphics graphics,int x,int y){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(graphics,texture,x,y);for(int px:new int[]{29,47})graphics.blit(texture,x+px,y+23,5,87,18,18,256,256);for(int i=0;i<outputs(recipe).size();i++)graphics.blit(texture,x+101+i*18,y+23,5,87,18,18,256,256);graphics.blit(texture,x+74,y+14,59,87,18,36,256,256);graphics.drawString(com.hbm.client.gui.font.LegacyFonts.get(),Component.translatable("gui.hbm.catalytic_cracker.batch"),x+3,y+53,0x404040,false);}
 private CatalyticCrackerViews(){}
}
