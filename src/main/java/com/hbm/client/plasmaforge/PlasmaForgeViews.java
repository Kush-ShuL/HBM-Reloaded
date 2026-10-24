package com.hbm.client.plasmaforge;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.plasmaforge.*;
import com.hbm.recipe.generic.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
public final class PlasmaForgeViews {
    static final LegacyRecipeDisplays.Category CATEGORY=new LegacyRecipeDisplays.Category("assembly","Fusion Plasma Forge","generic",ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei.png"),166,65,List.of(new ItemStack(PlasmaForgeContent.ITEM.get())));
    static List<List<ItemStack>> inputs(PlasmaForgeRecipe recipe){var values=new ArrayList<List<ItemStack>>();recipe.inputItems().forEach(in->values.add(in.processingExamples()));recipe.inputFluids().forEach(in->values.add(List.of(FluidIconItem.make(in))));return values;}
    static List<List<ItemStack>> outputs(PlasmaForgeRecipe recipe){var values=new ArrayList<List<ItemStack>>();recipe.outputItems().forEach(out->values.add(out.pool().stream().map(c->c.stack().copy()).toList()));recipe.outputFluids().forEach(out->values.add(List.of(FluidIconItem.make(out))));return values;}
    static LegacyRecipeDisplays.Display drawing(PlasmaForgeRecipe recipe){return new LegacyRecipeDisplays.Display(ResourceLocation.fromNamespaceAndPath("hbm","plasma_forge"),"plasma_forge",inputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),outputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),recipe.duration(),recipe.power(),List.of());}
    static int offset(PlasmaForgeRecipe recipe){return recipe.inputItems().size()+recipe.inputFluids().size()>12?27:recipe.inputItems().size()+recipe.inputFluids().size()>9?18:0;}
    static List<Component> outputTooltip(PlasmaForgeRecipe recipe,int lane,ItemStack stack){if(lane>=recipe.outputItems().size())return List.of();var output=recipe.outputItems().get(lane);var values=new ArrayList<Component>();if(lane==0)values.add(Component.translatable("gui.recipe.plasmaIn").append(": "+com.hbm.item.battery.BatteryItem.shortNumber(recipe.ignitionTemp())+"TU/t").withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));for(var choice:output.pool())if(choice.stack().getItem()==stack.getItem()&&Blueprints.metadata(choice.stack())==Blueprints.metadata(stack)){if(choice.chance()<1)values.add(Component.translatable("gui.hbm.chemical.per_item",(int)(choice.chance()*1000)/10F).withStyle(net.minecraft.ChatFormatting.RED));if(output.weighted())values.add(Component.translatable("gui.hbm.chemical.selection",(int)(output.selectionProbability(choice)*1000)/10F).withStyle(net.minecraft.ChatFormatting.RED));}return values;}
    static void draw(GuiGraphics graphics,PlasmaForgeRecipe recipe,int x,int y){var display=drawing(recipe);LegacyDisplayDrawing.background(graphics,CATEGORY,display,x,y);var font=LegacyFonts.get();String duration=Component.translatable("gui.hbm.plasmaforge.ticks",com.hbm.item.battery.BatteryItem.shortNumber(recipe.duration())).getString();graphics.drawString(font,duration,x+164-font.width(duration),y+45,0x404040,false);boolean temperature=System.currentTimeMillis()%2000>=1000;String energy=com.hbm.item.battery.BatteryItem.shortNumber(temperature?recipe.ignitionTemp():recipe.power())+(temperature?"TU/t":"HE/t");graphics.drawString(font,energy,x+164-font.width(energy),y+57,temperature?0xa000a0:0x404040,false);}
    static boolean visible(PlasmaForgeRecipe recipe){return com.hbm.compat.recipeviewer.RecipeViewerVisibility.visible(recipe.pools(),inputs(recipe),outputs(recipe));}
}
