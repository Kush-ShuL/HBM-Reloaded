package com.hbm.machine.reactor.zirnox.client;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.reactor.zirnox.*;
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
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;
@REIPluginClient
public final class ZirnoxReiPlugin implements REIClientPlugin {
    static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","zirnox");
    @Override public void registerItemComparators(me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry event){event.register((context,stack)->stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0),ZirnoxContent.ROD.get());}
    @Override public void registerEntries(me.shedaniel.rei.api.client.registry.entry.EntryRegistry event){for(var stack:ZirnoxContent.ROD.get().creativeStacks())event.addEntry(EntryStacks.of(stack));}
    @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(ZirnoxContent.ITEM.get()));}
    @Override public void registerDisplays(DisplayRegistry event){for(var recipe:ZirnoxFuelRecipes.all())event.add(new Display(recipe));}
    private static final class Display extends BasicDisplay {
        Display(ZirnoxFuelRecipes recipe){super(List.of(EntryIngredient.of(EntryStacks.of(recipe.input()))),List.of(EntryIngredient.of(EntryStacks.of(recipe.output()))));}
        @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}
    }
    private static final class Category implements DisplayCategory<Display>{
        @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("tile.machine_zirnox.name");}@Override public Renderer getIcon(){return EntryStacks.of(ZirnoxContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 60;}
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((graphics,x,y,partial)->ZirnoxFuelRecipeView.draw(graphics,bounds.x,bounds.y)));widgets.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());widgets.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entries(EntryIngredient.of(EntryStacks.of(ZirnoxContent.ITEM.get()))).disableBackground());return widgets;}
    }
}
