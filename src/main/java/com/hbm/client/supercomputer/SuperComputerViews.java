package com.hbm.client.supercomputer;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.supercomputer.*;
import com.hbm.recipe.generic.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
public final class SuperComputerViews {
    static final LegacyRecipeDisplays.Category CATEGORY=new LegacyRecipeDisplays.Category("supercomputer","Supercomputer","generic",ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei.png"),166,65,List.of(new ItemStack(SuperComputerContent.ITEM.get())));
    static List<List<ItemStack>> inputs(SuperComputerRecipe recipe){var values=new ArrayList<List<ItemStack>>();recipe.inputItems().forEach(in->values.add(in.processingExamples()));recipe.inputFluids().forEach(in->values.add(List.of(FluidIconItem.make(in))));return values;}
    static List<List<ItemStack>> outputs(SuperComputerRecipe recipe){var values=new ArrayList<List<ItemStack>>();recipe.outputItems().forEach(out->values.add(out.pool().stream().map(c->c.stack().copy()).toList()));recipe.outputFluids().forEach(out->values.add(List.of(FluidIconItem.make(out))));return values;}
    static LegacyRecipeDisplays.Display drawing(SuperComputerRecipe recipe){return new LegacyRecipeDisplays.Display(ResourceLocation.fromNamespaceAndPath("hbm","supercomputer"),"supercomputer",inputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),outputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),recipe.duration(),recipe.power(),List.of());}
    static int offset(SuperComputerRecipe recipe){return 0;}
    static List<ItemStack> blueprints(SuperComputerRecipe recipe){return recipe.pools().stream().map(Blueprints::make).toList();}
    static List<Component> outputTooltip(SuperComputerRecipe recipe,int lane,ItemStack stack){var values=new ArrayList<Component>();if(lane==0)values.addAll(recipe.tooltip());if(lane<recipe.outputItems().size()){var output=recipe.outputItems().get(lane);for(var choice:output.pool())if(choice.stack().getItem()==stack.getItem()&&Blueprints.metadata(choice.stack())==Blueprints.metadata(stack)){if(choice.chance()<1)values.add(Component.translatable("gui.hbm.chemical.per_item",(int)(choice.chance()*1000)/10F));if(output.weighted())values.add(Component.translatable("gui.hbm.chemical.selection",(int)(output.selectionProbability(choice)*1000)/10F));}}return values;}
    static void draw(GuiGraphics graphics,SuperComputerRecipe recipe,int x,int y){var display=drawing(recipe);LegacyDisplayDrawing.background(graphics,CATEGORY,display,x,y);if(recipe.isPooled())graphics.blit(CATEGORY.texture(),x+74,y+7,77,87,18,50,256,256);LegacyDisplayDrawing.extras(graphics,CATEGORY,display,x,y);}
    static boolean visible(SuperComputerRecipe recipe){return recipe.pools().stream().noneMatch(pool->pool.startsWith(Blueprints.SECRET));}
}
