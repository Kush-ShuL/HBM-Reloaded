package com.hbm.client.industrialboiler;
import com.hbm.machine.industrialboiler.IndustrialBoilerContent;
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
public final class BoilingReiPlugin implements REIClientPlugin {
 static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","boiling");@Override public void registerCategories(CategoryRegistry event){event.add(new Category());for(var machine:BoilingViews.machines())event.addWorkstations(TYPE,EntryStacks.of(machine));}@Override public void registerDisplays(DisplayRegistry event){for(var recipe:BoilingViews.recipes())event.add(new Display(recipe));}
 static final class Display extends BasicDisplay{Display(BoilingViews.View recipe){super(List.of(EntryIngredient.of(EntryStacks.of(recipe.input()))),List.of(EntryIngredient.of(EntryStacks.of(recipe.output()))));}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("tile.machine_boiler.name");}@Override public Renderer getIcon(){return EntryStacks.of(IndustrialBoilerContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}@Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var result=new ArrayList<Widget>();result.add(Widgets.createDrawableWidget((graphics,x,y,partial)->BoilingViews.draw(graphics,bounds.x,bounds.y)));result.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());result.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entries(BoilingViews.machines().stream().map(EntryStacks::of).toList()).disableBackground());result.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());return result;}}
}
