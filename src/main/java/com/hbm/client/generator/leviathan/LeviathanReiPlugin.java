package com.hbm.client.generator.leviathan;
import com.hbm.machine.generator.leviathan.LeviathanContent;
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
public final class LeviathanReiPlugin implements REIClientPlugin {
    private static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","leviathan");
    @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(LeviathanContent.ITEM.get()));}
    @Override public void registerDisplays(DisplayRegistry event){for(var recipe:LeviathanViews.recipes())event.add(new Display(recipe));}
    private static final class Display extends BasicDisplay {final String energy;Display(LeviathanViews.View recipe){super(List.of(EntryIngredient.of(EntryStacks.of(recipe.input()))),List.of(EntryIngredient.of(EntryStacks.of(recipe.output()))));energy=recipe.energy();}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
    private static final class Category implements DisplayCategory<Display> {
        @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_chungus");}@Override public Renderer getIcon(){return EntryStacks.of(LeviathanContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var result=new ArrayList<Widget>();result.add(Widgets.createDrawableWidget((graphics,x,y,partial)->LeviathanViews.draw(graphics,bounds.x,bounds.y,display.energy)));result.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());result.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entry(EntryStacks.of(LeviathanContent.ITEM.get())).disableBackground());result.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());return result;}
    }
}
