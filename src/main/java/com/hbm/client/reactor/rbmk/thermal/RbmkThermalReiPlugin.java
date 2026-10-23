package com.hbm.client.reactor.rbmk.thermal;
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
public final class RbmkThermalReiPlugin implements REIClientPlugin{
    static final CategoryIdentifier<Display> COOLER=CategoryIdentifier.of("hbm","rbmk_cooler"),HEATER=CategoryIdentifier.of("hbm","rbmk_heater");
    @Override public void registerCategories(CategoryRegistry event){for(boolean cooler:new boolean[]{true,false}){event.add(new Category(cooler));event.addWorkstations(cooler?COOLER:HEATER,EntryStacks.of(RbmkThermalViews.machine(cooler)));}}@Override public void registerDisplays(DisplayRegistry event){for(boolean cooler:new boolean[]{true,false})for(var recipe:RbmkThermalViews.recipes(cooler))event.add(new Display(recipe));}
    static final class Display extends BasicDisplay{final RbmkThermalViews.View recipe;Display(RbmkThermalViews.View recipe){super(List.of(EntryIngredient.of(EntryStacks.of(recipe.input()))),List.of(EntryIngredient.of(EntryStacks.of(recipe.output()))));this.recipe=recipe;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return recipe.cooler()?COOLER:HEATER;}}
    static final class Category implements DisplayCategory<Display>{final boolean cooler;Category(boolean cooler){this.cooler=cooler;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return cooler?COOLER:HEATER;}@Override public Component getTitle(){return RbmkThermalViews.title(cooler);}@Override public Renderer getIcon(){return EntryStacks.of(RbmkThermalViews.machine(cooler));}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}@Override public List<Widget> setupDisplay(Display display,Rectangle bounds){return List.of(Widgets.createDrawableWidget((g,x,y,partial)->RbmkThermalViews.draw(g,bounds.x,bounds.y,display.recipe)),Widgets.createSlot(new Point(bounds.x+30,bounds.y+24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput(),Widgets.createSlot(new Point(bounds.x+120,bounds.y+24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput(),Widgets.createSlot(new Point(bounds.x+75,bounds.y+42)).entry(EntryStacks.of(RbmkThermalViews.machine(cooler))).disableBackground());}}
}
