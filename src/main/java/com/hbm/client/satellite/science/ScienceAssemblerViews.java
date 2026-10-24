package com.hbm.client.satellite.science;

import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;
import com.hbm.recipe.generic.Blueprints;
import com.hbm.satellite.*;
import com.hbm.satellite.science.SpaceAssemblerRecipe;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Original NEIGenericRecipeHandler166x65 and GenericRecipeNoPower time label; no invented block GUI or HE line. */
public final class ScienceAssemblerViews {
    static ItemStack machine(){return com.hbm.satellite.item.SatelliteItem.stack(SatelliteKind.SCIENCE_ASSEMBLER);}
    static LegacyRecipeDisplays.Category category(){return new LegacyRecipeDisplays.Category("space_assembly","Space Assembler","generic",ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png"),166,65,List.of(machine()));}
    static List<List<ItemStack>> inputs(SpaceAssemblerRecipe recipe){return recipe.inputItems().stream().map(input->input.examples()).toList();}
    static List<List<ItemStack>> outputs(SpaceAssemblerRecipe recipe){return recipe.outputItems().stream().map(output->output.pool().stream().map(choice->choice.stack().copy()).toList()).toList();}
    static LegacyRecipeDisplays.Display drawing(SpaceAssemblerRecipe recipe){return new LegacyRecipeDisplays.Display(ResourceLocation.parse("hbm:space_assembler"),"space_assembly",inputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),outputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),recipe.duration(),null,List.of());}
    static void draw(GuiGraphics graphics,SpaceAssemblerRecipe recipe,int x,int y){var category=category();var display=drawing(recipe);LegacyDisplayDrawing.background(graphics,category,display,x,y);if(recipe.isPooled())graphics.blit(category.texture(),x+74,y+7,77,87,18,50,256,256);LegacyDisplayDrawing.extras(graphics,category,display,x,y);}
    static List<ItemStack> blueprints(SpaceAssemblerRecipe recipe){return recipe.pools().stream().map(Blueprints::make).toList();}
    static boolean visible(SpaceAssemblerRecipe recipe){return com.hbm.compat.recipeviewer.RecipeViewerVisibility.visible(recipe.pools(),inputs(recipe),outputs(recipe));}
    static List<Component> outputTooltip(SpaceAssemblerRecipe recipe,int lane,ItemStack stack){
        var output=recipe.outputItems().get(lane);var lines=new ArrayList<Component>();
        for(var choice:output.pool())if(choice.stack().getItem()==stack.getItem()&&Blueprints.metadata(choice.stack())==Blueprints.metadata(stack)){
            if(choice.chance()<1)lines.add(Component.translatable("gui.hbm.space_assembler.per_item",(int)(choice.chance()*1000)/10F).withStyle(ChatFormatting.RED));
            if(output.weighted())lines.add(Component.translatable("gui.hbm.space_assembler.selection",(int)(output.selectionProbability(choice)*1000)/10F).withStyle(ChatFormatting.RED));
        }
        return lines;
    }
    private ScienceAssemblerViews(){}
}
