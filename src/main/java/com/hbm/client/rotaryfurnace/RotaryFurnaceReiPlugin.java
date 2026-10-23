package com.hbm.client.rotaryfurnace;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.machine.rotaryfurnace.*;
import java.util.*;
import me.shedaniel.math.*;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.*;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.*;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;
@REIPluginClient
public final class RotaryFurnaceReiPlugin implements REIClientPlugin {
 static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","rotary_furnace");
 @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(RotaryFurnaceContent.ITEM.get()));}@Override public void registerDisplays(DisplayRegistry event){for(var recipe:RotaryFurnaceRecipes.recipes(event.getRecipeManager()))event.add(new Display(recipe));}
 static final class Display extends BasicDisplay{final RotaryFurnaceRecipe recipe;Display(RotaryFurnaceRecipe recipe){super(RotaryFurnaceViews.inputs(recipe).stream().map(stacks->EntryIngredient.of(stacks.stream().map(stack->EntryStacks.of(stack).tooltipProcessor((entry,tooltip)->tooltip.addAllTexts(com.hbm.ore.OreRecipeInputs.tooltip(entry.getValue())))).toList())).toList(),RotaryFurnaceViews.outputs(recipe).stream().map(stacks->EntryIngredient.of(stacks.stream().map(EntryStacks::of).toList())).toList());this.recipe=recipe;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.machineRotaryFurnace");}@Override public Renderer getIcon(){return EntryStacks.of(RotaryFurnaceContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}@Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();var drawing=RotaryFurnaceViews.drawing(display.recipe);var in=LegacyRecipeDisplays.inputCoordinates(RotaryFurnaceViews.CATEGORY,display.getInputEntries().size());var out=LegacyRecipeDisplays.outputCoordinates(RotaryFurnaceViews.CATEGORY,drawing);widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->RotaryFurnaceViews.draw(g,display.recipe,bounds.x,bounds.y)));for(int i=0;i<display.getInputEntries().size();i++)widgets.add(Widgets.createSlot(new Point(bounds.x+in[i][0],bounds.y+in[i][1])).entries(display.getInputEntries().get(i)).disableBackground().markInput());for(int i=0;i<display.getOutputEntries().size();i++)widgets.add(Widgets.createSlot(new Point(bounds.x+out[i][0],bounds.y+out[i][1])).entries(display.getOutputEntries().get(i)).disableBackground().markOutput());widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entries(List.of(EntryStacks.of(RotaryFurnaceContent.ITEM.get()))).disableBackground());return widgets;}}
}
