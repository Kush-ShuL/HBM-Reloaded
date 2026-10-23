package com.hbm.client.reactor.pwr;

import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.machine.reactor.pwr.*;
import java.util.*;
import me.shedaniel.math.*;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.*;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;

@REIPluginClient
public final class PwrReiPlugin implements REIClientPlugin {
    static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","pwr_fission");
    @Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->PwrFuelType.from(PwrFuelItem.metadata(stack)).ordinal(),PwrContent.FUEL.get());}
    @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(PwrContent.CONTROLLER_ITEM.get()));}
    @Override public void registerDisplays(DisplayRegistry event){for(var recipe:PwrFuelCycles.recipes())event.add(new Display(recipe));}
    static final class Display extends BasicDisplay{final PwrFuelCycles recipe;Display(PwrFuelCycles recipe){super(List.of(EntryIngredient.of(EntryStacks.of(recipe.input()))),List.of(EntryIngredient.of(EntryStacks.of(recipe.output()))));this.recipe=recipe;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
    static final class Category implements DisplayCategory<Display>{
        @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.pwrController");}@Override public Renderer getIcon(){return EntryStacks.of(PwrContent.CONTROLLER_ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var input=LegacyRecipeDisplays.inputCoordinates(PwrFuelCycles.CATEGORY,1)[0];var output=LegacyRecipeDisplays.outputCoordinates(PwrFuelCycles.CATEGORY,display.recipe.display())[0];return List.of(Widgets.createDrawableWidget((graphics,x,y,partial)->display.recipe.draw(graphics,bounds.x,bounds.y)),Widgets.createSlot(new Point(bounds.x+input[0],bounds.y+input[1])).entries(display.getInputEntries().getFirst()).disableBackground().markInput(),Widgets.createSlot(new Point(bounds.x+output[0],bounds.y+output[1])).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput(),Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entry(EntryStacks.of(PwrContent.CONTROLLER_ITEM.get())).disableBackground());}
    }
}
