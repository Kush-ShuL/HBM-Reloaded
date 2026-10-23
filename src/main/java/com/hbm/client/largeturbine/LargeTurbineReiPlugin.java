package com.hbm.client.largeturbine;
import com.hbm.machine.largeturbine.LargeTurbineContent;
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
@REIPluginClient public final class LargeTurbineReiPlugin implements REIClientPlugin {
 private static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","large_turbine_legacy");
 @Override public void registerCategories(CategoryRegistry e){e.add(new Category());e.addWorkstations(TYPE,EntryStacks.of(LargeTurbineContent.ITEM.get()));}@Override public void registerDisplays(DisplayRegistry e){for(var recipe:LargeTurbineViews.recipes())e.add(new Display(recipe));}
 private static final class Display extends BasicDisplay{final String energy;Display(LargeTurbineViews.View r){super(List.of(EntryIngredient.of(EntryStacks.of(r.input()))),List.of(EntryIngredient.of(EntryStacks.of(r.output()))));energy=r.energy();}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 private static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_large_turbine");}@Override public Renderer getIcon(){return EntryStacks.of(LargeTurbineContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}@Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->LargeTurbineViews.draw(g,bounds.x,bounds.y,display.energy)));widgets.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entry(EntryStacks.of(LargeTurbineContent.ITEM.get())).disableBackground());widgets.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());return widgets;}}
}
