package com.hbm.client.oilliquefactor;
import com.hbm.machine.oilliquefactor.*;
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
public final class LiquefactorReiPlugin implements REIClientPlugin {
    static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","oil_liquefaction");
    @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(LiquefactorContent.ITEM.get()));}
    @Override public void registerDisplays(DisplayRegistry event){for(var recipe:LiquefactionRecipes.recipes(event.getRecipeManager()))event.add(new Display(recipe));}
    static final class Display extends BasicDisplay{final LiquefactorRecipe recipe;Display(LiquefactorRecipe recipe){super(List.of(EntryIngredient.of(java.util.Arrays.stream(recipe.input().ingredient().getItems()).map(EntryStacks::of).toList())),List.of(EntryIngredient.of(EntryStacks.of(LiquefactorViews.output(recipe)))));this.recipe=recipe;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
    static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("category.hbm.liquefaction");}@Override public Renderer getIcon(){return EntryStacks.of(LiquefactorContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->LiquefactorViews.draw(g,bounds.x,bounds.y)));widgets.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());widgets.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entry(EntryStacks.of(LiquefactorContent.ITEM.get())).disableBackground());return widgets;}
    }
}
