package com.hbm.client.generator.diesel;
import com.hbm.machine.generator.diesel.DieselContent;
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
public final class DieselReiPlugin implements REIClientPlugin {
    private static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","diesel_fuels");
    @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(DieselContent.ITEM.get()));}@Override public void registerDisplays(DisplayRegistry event){for(var recipe:DieselFuelViews.all())event.add(new Display(recipe));}
    private static final class Display extends BasicDisplay {final String energy;Display(DieselFuelViews.View recipe){super(List.of(EntryIngredient.of(EntryStacks.of(recipe.fuel()))),List.of());energy=recipe.energy();}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
    private static final class Category implements DisplayCategory<Display> {@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_diesel");}@Override public Renderer getIcon(){return EntryStacks.of(DieselContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}@Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var result=new ArrayList<Widget>();result.add(Widgets.createDrawableWidget((graphics,x,y,partial)->DieselFuelViews.draw(graphics,bounds.x,bounds.y,display.energy)));result.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());result.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entry(EntryStacks.of(DieselContent.ITEM.get())).disableBackground());return result;}}
}
