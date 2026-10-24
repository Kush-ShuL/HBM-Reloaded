package com.hbm.client.blastfurnace;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.blastfurnace.*;
import com.hbm.recipe.generic.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
public final class BlastFurnaceViews {
    static final LegacyRecipeDisplays.Category CATEGORY=new LegacyRecipeDisplays.Category("blast_furnace","Blast Furnace","generic",ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei.png"),166,65,List.of(new ItemStack(BlastFurnaceContent.ITEM.get())));
    static List<List<ItemStack>> inputs(BlastFurnaceRecipe recipe){var values=new ArrayList<List<ItemStack>>();recipe.inputItems().forEach(in->values.add(in.processingExamples()));recipe.inputFluids().forEach(in->values.add(List.of(FluidIconItem.make(in))));return values;}
    static List<List<ItemStack>> outputs(BlastFurnaceRecipe recipe){var values=new ArrayList<List<ItemStack>>();recipe.outputItems().forEach(out->values.add(out.pool().stream().map(c->c.stack().copy()).toList()));recipe.outputFluids().forEach(out->values.add(List.of(FluidIconItem.make(out))));return values;}
    static LegacyRecipeDisplays.Display drawing(BlastFurnaceRecipe recipe){return new LegacyRecipeDisplays.Display(ResourceLocation.fromNamespaceAndPath("hbm","blast_furnace"),"blast_furnace",inputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),outputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),recipe.duration(),null,List.of());}
    static int offset(BlastFurnaceRecipe recipe){return 0;}
    static List<ItemStack> blueprints(BlastFurnaceRecipe recipe){return recipe.pools().stream().map(Blueprints::make).toList();}
    static List<Component> outputTooltip(BlastFurnaceRecipe recipe,int lane,ItemStack stack){if(lane>=recipe.outputItems().size())return List.of();var output=recipe.outputItems().get(lane);var values=new ArrayList<Component>();for(var choice:output.pool())if(choice.stack().getItem()==stack.getItem()&&Blueprints.metadata(choice.stack())==Blueprints.metadata(stack)){if(choice.chance()<1)values.add(Component.translatable("gui.hbm.blast.per_item",(int)(choice.chance()*1000)/10F).withStyle(net.minecraft.ChatFormatting.RED));if(output.weighted())values.add(Component.translatable("gui.hbm.blast.selection",(int)(output.selectionProbability(choice)*1000)/10F).withStyle(net.minecraft.ChatFormatting.RED));}return values;}
    static void draw(GuiGraphics graphics,BlastFurnaceRecipe recipe,int x,int y){var display=drawing(recipe);LegacyDisplayDrawing.background(graphics,CATEGORY,display,x,y);if(recipe.isPooled())graphics.blit(CATEGORY.texture(),x+74+offset(recipe),y+7,77,87,18,50,256,256);LegacyDisplayDrawing.extras(graphics,CATEGORY,display,x,y);}
    static boolean visible(BlastFurnaceRecipe recipe){return com.hbm.compat.recipeviewer.RecipeViewerVisibility.visible(recipe.pools(),inputs(recipe),outputs(recipe));}
}
