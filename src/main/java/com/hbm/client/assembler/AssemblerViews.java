package com.hbm.client.assembler;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.assembler.*;
import com.hbm.recipe.generic.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
public final class AssemblerViews {
    static final LegacyRecipeDisplays.Category CATEGORY=new LegacyRecipeDisplays.Category("assembly","Assembly Machine","generic",ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei.png"),166,65,List.of(new ItemStack(AssemblerContent.ITEM.get())));
    static List<List<ItemStack>> inputs(AssemblerRecipe recipe){var values=new ArrayList<List<ItemStack>>();recipe.inputItems().forEach(in->values.add(in.examples()));recipe.inputFluids().forEach(in->values.add(List.of(FluidIconItem.make(in))));return values;}
    static List<List<ItemStack>> outputs(AssemblerRecipe recipe){var values=new ArrayList<List<ItemStack>>();recipe.outputItems().forEach(out->values.add(out.pool().stream().map(c->c.stack().copy()).toList()));recipe.outputFluids().forEach(out->values.add(List.of(FluidIconItem.make(out))));return values;}
    static LegacyRecipeDisplays.Display drawing(AssemblerRecipe recipe){return new LegacyRecipeDisplays.Display(ResourceLocation.fromNamespaceAndPath("hbm","assembly"),"assembly",inputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),outputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),recipe.duration(),recipe.power(),List.of());}
    static int offset(AssemblerRecipe recipe){return recipe.inputItems().size()+recipe.inputFluids().size()>12?27:recipe.inputItems().size()+recipe.inputFluids().size()>9?18:0;}
    static List<Component> outputTooltip(AssemblerRecipe recipe,int lane,ItemStack stack){if(lane>=recipe.outputItems().size())return List.of();var output=recipe.outputItems().get(lane);var values=new ArrayList<Component>();for(var choice:output.pool())if(choice.stack().getItem()==stack.getItem()&&Blueprints.metadata(choice.stack())==Blueprints.metadata(stack)){if(choice.chance()<1)values.add(Component.literal(((int)(choice.chance()*1000)/10F)+"% per item").withStyle(net.minecraft.ChatFormatting.RED));if(output.weighted())values.add(Component.literal(((int)(output.selectionProbability(choice)*1000)/10F)+"% selection").withStyle(net.minecraft.ChatFormatting.RED));}return values;}
    static void draw(GuiGraphics graphics,AssemblerRecipe recipe,int x,int y){var display=drawing(recipe);LegacyDisplayDrawing.background(graphics,CATEGORY,display,x,y);LegacyDisplayDrawing.extras(graphics,CATEGORY,display,x,y);}
    static boolean visible(AssemblerRecipe recipe){return com.hbm.compat.recipeviewer.RecipeViewerVisibility.visible(recipe.pools(),inputs(recipe),outputs(recipe));}
}
