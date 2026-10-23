package com.hbm.client.electrolyser;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.electrolyser.*;
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
public final class ElectrolyserReiPlugin implements REIClientPlugin {
 static final CategoryIdentifier<Display> FLUID=CategoryIdentifier.of("hbm","electrolyser_fluid"),METAL=CategoryIdentifier.of("hbm","electrolyser_metal");
 @Override public void registerCategories(CategoryRegistry event){event.add(new Category(false),new Category(true));event.addWorkstations(FLUID,EntryStacks.of(ElectrolyserContent.ITEM.get()));event.addWorkstations(METAL,EntryStacks.of(ElectrolyserContent.ITEM.get()));}
 @Override public void registerDisplays(DisplayRegistry event){for(boolean metal:new boolean[]{false,true})for(var view:ElectrolyserViews.recipes(event.getRecipeManager(),metal))event.add(new Display(view));}
 static final class Display extends BasicDisplay {final ElectrolyserViews.View view;Display(ElectrolyserViews.View view){super(view.inputs().stream().map(items->EntryIngredient.of(items.stream().map(EntryStacks::of).toList())).toList(),view.outputs().stream().map(stack->EntryIngredient.of(EntryStacks.of(stack))).toList());this.view=view;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return view.recipe().metal()?METAL:FLUID;}}
 static final class Category implements DisplayCategory<Display>{final boolean metal;Category(boolean metal){this.metal=metal;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return metal?METAL:FLUID;}@Override public Component getTitle(){return Component.translatable(metal?"gui.hbm.electrolyser.metal":"gui.hbm.electrolyser.fluid");}@Override public Renderer getIcon(){return EntryStacks.of(ElectrolyserContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}
  @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((graphics,x,y,partial)->ElectrolyserViews.draw(graphics,display.view,bounds.x,bounds.y)));var in=ElectrolyserViews.inputCoords(display.view);for(int i=0;i<in.length;i++)widgets.add(Widgets.createSlot(new Point(bounds.x+in[i][0],bounds.y+in[i][1])).entries(display.getInputEntries().get(i)).disableBackground().markInput());var out=ElectrolyserViews.outputCoords(display.view);for(int i=0;i<out.length;i++)widgets.add(Widgets.createSlot(new Point(bounds.x+out[i][0],bounds.y+out[i][1])).entries(display.getOutputEntries().get(i)).disableBackground().markOutput());widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entry(EntryStacks.of(ElectrolyserContent.ITEM.get())).disableBackground());return widgets;}
 }
}
