package com.hbm.machine.ashpit.client;
import com.hbm.machine.ashpit.AshpitContent;
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
public final class AshpitReiPlugin implements REIClientPlugin {
 static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","ashpit");@Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(AshpitContent.ITEM.get()));}@Override public void registerDisplays(DisplayRegistry event){for(var recipe:AshpitViews.recipes())event.add(new Display(recipe));}
 static final class Display extends BasicDisplay{Display(AshpitViews.View recipe){super(List.of(EntryIngredient.of(recipe.machines().stream().map(EntryStacks::of).toList()),EntryIngredient.of(recipe.input().stream().map(EntryStacks::of).toList())),List.of(EntryIngredient.of(EntryStacks.of(recipe.ash()))));}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_ashpit");}@Override public Renderer getIcon(){return EntryStacks.of(AshpitContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}@Override public List<Widget> setupDisplay(Display d,Rectangle bounds){var list=new ArrayList<Widget>();list.add(Widgets.createDrawableWidget((g,x,y,p)->AshpitViews.draw(g,bounds.x,bounds.y)));list.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+24)).entries(d.getInputEntries().get(0)).disableBackground().markInput());list.add(Widgets.createSlot(new Point(bounds.x+30,bounds.y+24)).entries(d.getInputEntries().get(1)).disableBackground().markInput());list.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entry(EntryStacks.of(AshpitContent.ITEM.get())).disableBackground());list.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entries(d.getOutputEntries().getFirst()).disableBackground().markOutput());return list;}}
}
