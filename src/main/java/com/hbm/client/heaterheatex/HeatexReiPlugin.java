package com.hbm.client.heaterheatex;
import com.hbm.machine.heaterheatex.HeatexContent;
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
public final class HeatexReiPlugin implements REIClientPlugin {
 static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","heater_heatex");@Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(HeatexContent.ITEM.get()));}@Override public void registerDisplays(DisplayRegistry event){for(var recipe:HeatexViews.recipes())event.add(new Display(recipe));}
 static final class Display extends BasicDisplay{final HeatexViews.View recipe;Display(HeatexViews.View recipe){super(List.of(EntryIngredient.of(EntryStacks.of(recipe.hot()))),List.of(EntryIngredient.of(EntryStacks.of(recipe.cold()))));this.recipe=recipe;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.heater_heatex");}@Override public Renderer getIcon(){return EntryStacks.of(HeatexContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}@Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var list=new ArrayList<Widget>();list.add(Widgets.createDrawableWidget((graphics,x,y,partial)->HeatexViews.draw(graphics,bounds.x,bounds.y,display.recipe)));list.add(Widgets.createSlot(new Point(bounds.x+30,bounds.y+24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());list.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+42)).entry(EntryStacks.of(HeatexContent.ITEM.get())).disableBackground());list.add(Widgets.createSlot(new Point(bounds.x+120,bounds.y+24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());return list;}}
}
