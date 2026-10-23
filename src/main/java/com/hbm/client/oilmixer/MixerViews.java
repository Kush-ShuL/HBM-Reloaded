package com.hbm.client.oilmixer;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.oilmixer.MixerRecipe;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** One source NEI recipe per actual output variant; all solid raw/source/deep alternatives are processing-only. */
final class MixerViews {
 static List<List<ItemStack>> inputs(MixerRecipe r){var inputs=new ArrayList<List<ItemStack>>();for(var fluid:r.inputFluids())inputs.add(List.of(FluidIconItem.make(fluid)));for(var item:r.inputItems())inputs.add(item.processingExamples());return inputs;}
 static ItemStack output(MixerRecipe r){return FluidIconItem.make(r.outputFluids().getFirst());}
 static void draw(MixerRecipe recipe,GuiGraphics g,int x,int y){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,texture,x,y);for(int i=0;i<inputs(recipe).size();i++)g.blit(texture,x+20+i*18,y+23,5,87,18,18,256,256);g.blit(texture,x+74,y+14,59,87,18,36,256,256);g.blit(texture,x+112,y+23,5,87,18,18,256,256);g.drawString(com.hbm.client.gui.font.LegacyFonts.get(),Component.literal(recipe.duration()/20D+" s"),x+8,y+53,0x404040,false);}
 private MixerViews(){}
}
