package com.hbm.client.rockmill;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.machine.rockmill.*;
import com.hbm.recipe.generic.Blueprints;
import java.util.*;
import me.shedaniel.math.*;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.*;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.*;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;
@REIPluginClient
public final class RockMillReiPlugin implements REIClientPlugin {
    static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","rockmill");
    @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(RockMillContent.ITEM.get()));}
    @Override public void registerDisplays(DisplayRegistry event){for(var recipe:RockMillRecipeConfig.recipes(event.getRecipeManager()))if(RockMillViews.visible(recipe))event.add(new Display(recipe));}
    static final class Display extends BasicDisplay{final RockMillRecipe recipe;Display(RockMillRecipe recipe){super(RockMillViews.inputs(recipe).stream().map(stacks->EntryIngredient.of(stacks.stream().map(stack->EntryStacks.of(stack).tooltipProcessor((entry,tooltip)->tooltip.addAllTexts(com.hbm.ore.OreRecipeInputs.tooltip(entry.getValue())))).toList())).toList(),RockMillViews.outputs(recipe).stream().map(stacks->EntryIngredient.of(stacks.stream().map(EntryStacks::of).toList())).toList());this.recipe=recipe;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
    static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.machineRockMill");}@Override public Renderer getIcon(){return EntryStacks.of(RockMillContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();var drawing=RockMillViews.drawing(display.recipe);var in=LegacyRecipeDisplays.inputCoordinates(RockMillViews.CATEGORY,display.getInputEntries().size());var out=LegacyRecipeDisplays.outputCoordinates(RockMillViews.CATEGORY,drawing);widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->RockMillViews.draw(g,display.recipe,bounds.x,bounds.y)));for(int i=0;i<display.getInputEntries().size();i++)widgets.add(Widgets.createSlot(new Point(bounds.x+in[i][0],bounds.y+in[i][1])).entries(display.getInputEntries().get(i)).disableBackground().markInput());for(int i=0;i<display.getOutputEntries().size();i++){int lane=i;var entries=display.getOutputEntries().get(i).map(entry->{var copy=entry.copy();copy.tooltipProcessor((stack,tooltip)->{var value=(net.minecraft.world.item.ItemStack)stack.getValue();RockMillViews.outputTooltip(display.recipe,lane,value).forEach(tooltip::add);return tooltip;});return copy;});widgets.add(Widgets.createSlot(new Point(bounds.x+out[i][0],bounds.y+out[i][1])).entries(entries).disableBackground().markOutput());}int offset=RockMillViews.offset(display.recipe);if(display.recipe.isPooled())widgets.add(Widgets.createSlot(new Point(bounds.x+75+offset,bounds.y+10)).entries(RockMillViews.blueprints(display.recipe).stream().map(EntryStacks::of).toList()).disableBackground());widgets.add(Widgets.createSlot(new Point(bounds.x+75+offset,bounds.y+(display.recipe.isPooled()?38:31))).entries(List.of(EntryStacks.of(RockMillContent.ITEM.get()))).disableBackground());return widgets;}
    }
}
