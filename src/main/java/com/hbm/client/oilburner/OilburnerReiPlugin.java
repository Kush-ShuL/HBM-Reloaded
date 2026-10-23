package com.hbm.client.oilburner;
import com.hbm.machine.oilburner.OilburnerContent;
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
public final class OilburnerReiPlugin implements REIClientPlugin {
 static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","heater_oilburner");@Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(OilburnerContent.ITEM.get()));}@Override public void registerDisplays(DisplayRegistry event){for(var recipe:OilburnerViews.recipes())event.add(new Display(recipe));}
 static final class Display extends BasicDisplay{final OilburnerViews.View recipe;Display(OilburnerViews.View recipe){super(List.of(EntryIngredient.of(EntryStacks.of(recipe.fuel()))),List.of());this.recipe=recipe;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.heater_oilburner");}@Override public Renderer getIcon(){return EntryStacks.of(OilburnerContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}@Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var list=new ArrayList<Widget>();list.add(Widgets.createDrawableWidget((graphics,x,y,partial)->OilburnerViews.draw(graphics,bounds.x,bounds.y,display.recipe)));list.add(Widgets.createSlot(new Point(bounds.x+30,bounds.y+24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());list.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+42)).entry(EntryStacks.of(OilburnerContent.ITEM.get())).disableBackground());return list;}}
}
