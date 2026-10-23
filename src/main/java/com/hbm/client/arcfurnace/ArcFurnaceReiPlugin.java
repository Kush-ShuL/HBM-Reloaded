package com.hbm.client.arcfurnace;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.arcfurnace.*;
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
public final class ArcFurnaceReiPlugin implements REIClientPlugin {
 static final CategoryIdentifier<Display> SOLID=CategoryIdentifier.of("hbm","arc_furnace_solid"),LIQUID=CategoryIdentifier.of("hbm","arc_furnace_liquid");
 @Override public void registerItemComparators(me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry event){for(var item:List.of(ArcFurnaceContent.ELECTRODE.get(),ArcFurnaceContent.BURNT.get(),ArcFurnaceContent.SAND_ITEM.get()))event.register((context,stack)->stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0),item);}
 @Override public void registerEntries(me.shedaniel.rei.api.client.registry.entry.EntryRegistry event){for(var tab:List.of("machine","control"))for(var stack:ArcFurnaceContent.creativeStacks(tab))event.addEntry(EntryStacks.of(stack));}
 @Override public void registerCategories(CategoryRegistry event){event.add(new Category(false),new Category(true));event.addWorkstations(SOLID,EntryStacks.of(ArcFurnaceContent.ITEM.get()));event.addWorkstations(LIQUID,EntryStacks.of(ArcFurnaceContent.ITEM.get()));}
 @Override public void registerDisplays(DisplayRegistry event){for(boolean liquid:new boolean[]{false,true})for(var view:ArcFurnaceViews.recipes(event.getRecipeManager(),liquid))event.add(new Display(view));}
 static final class Display extends BasicDisplay {final ArcFurnaceViews.View view;Display(ArcFurnaceViews.View view){super(List.of(EntryIngredient.of(view.inputs().stream().map(EntryStacks::of).toList())),view.outputs().stream().map(stack->EntryIngredient.of(EntryStacks.of(stack))).toList());this.view=view;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return view.liquid()?LIQUID:SOLID;}}
 static final class Category implements DisplayCategory<Display>{final boolean liquid;Category(boolean liquid){this.liquid=liquid;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return liquid?LIQUID:SOLID;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_arc_furnace");}@Override public Renderer getIcon(){return EntryStacks.of(ArcFurnaceContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}
  @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((graphics,x,y,partial)->ArcFurnaceViews.draw(graphics,display.view,bounds.x,bounds.y)));widgets.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());var out=ArcFurnaceViews.outputCoords(display.view);for(int i=0;i<out.length;i++)widgets.add(Widgets.createSlot(new Point(bounds.x+out[i][0],bounds.y+out[i][1])).entries(display.getOutputEntries().get(i)).disableBackground().markOutput());widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entry(EntryStacks.of(ArcFurnaceContent.ITEM.get())).disableBackground());return widgets;}
 }
}
