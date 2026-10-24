package com.hbm.client.chemicalplant;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.chemicalplant.*;
import com.hbm.recipe.generic.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
public final class ChemicalPlantViews {
    static final LegacyRecipeDisplays.Category CATEGORY=new LegacyRecipeDisplays.Category("chemical","Chemical Plant","generic",ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei.png"),166,65,List.of(new ItemStack(ChemicalPlantContent.ITEM.get()),new ItemStack(com.hbm.machine.chemicalfactory.ChemicalFactoryContent.ITEM.get())));
    static List<List<ItemStack>> inputs(ChemicalPlantRecipe recipe){var values=new ArrayList<List<ItemStack>>();recipe.inputItems().forEach(in->values.add(in.processingExamples()));recipe.inputFluids().forEach(in->values.add(List.of(FluidIconItem.make(in))));return values;}
    static List<List<ItemStack>> outputs(ChemicalPlantRecipe recipe){var values=new ArrayList<List<ItemStack>>();recipe.outputItems().forEach(out->values.add(out.pool().stream().map(c->c.stack().copy()).toList()));recipe.outputFluids().forEach(out->values.add(List.of(FluidIconItem.make(out))));return values;}
    static LegacyRecipeDisplays.Display drawing(ChemicalPlantRecipe recipe){return new LegacyRecipeDisplays.Display(ResourceLocation.fromNamespaceAndPath("hbm","chemical"),"chemical",inputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),outputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),recipe.duration(),recipe.power(),List.of());}
    static int offset(ChemicalPlantRecipe recipe){return 0;}
    static List<ItemStack> blueprints(ChemicalPlantRecipe recipe){return recipe.pools().stream().map(Blueprints::make).toList();}
    static List<Component> outputTooltip(ChemicalPlantRecipe recipe,int lane,ItemStack stack){if(lane>=recipe.outputItems().size())return List.of();var output=recipe.outputItems().get(lane);var values=new ArrayList<Component>();for(var choice:output.pool())if(choice.stack().getItem()==stack.getItem()&&Blueprints.metadata(choice.stack())==Blueprints.metadata(stack)){if(choice.chance()<1)values.add(Component.translatable("gui.hbm.chemical.per_item",(int)(choice.chance()*1000)/10F).withStyle(net.minecraft.ChatFormatting.RED));if(output.weighted())values.add(Component.translatable("gui.hbm.chemical.selection",(int)(output.selectionProbability(choice)*1000)/10F).withStyle(net.minecraft.ChatFormatting.RED));}return values;}
    static void draw(GuiGraphics graphics,ChemicalPlantRecipe recipe,int x,int y){var display=drawing(recipe);LegacyDisplayDrawing.background(graphics,CATEGORY,display,x,y);if(recipe.isPooled())graphics.blit(CATEGORY.texture(),x+74+offset(recipe),y+7,77,87,18,50,256,256);LegacyDisplayDrawing.extras(graphics,CATEGORY,display,x,y);}
    static boolean visible(ChemicalPlantRecipe recipe){return com.hbm.compat.recipeviewer.RecipeViewerVisibility.visible(recipe.pools(),inputs(recipe),outputs(recipe));}
}
