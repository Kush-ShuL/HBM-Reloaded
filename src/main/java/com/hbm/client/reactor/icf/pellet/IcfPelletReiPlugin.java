package com.hbm.client.reactor.icf.pellet;
import com.hbm.machine.reactor.icf.pellet.*;
import com.hbm.registry.HbmContent;
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
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;
@REIPluginClient
public final class IcfPelletReiPlugin implements REIClientPlugin {
 private static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","icf_pellet_press");
 @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(IcfPelletPressContent.ITEM.get()));}
 @Override public void registerDisplays(DisplayRegistry event){for(var row:IcfPelletViews.rows())event.add(new Display(row));}
 @Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->IcfPelletViews.subtype(stack).hashCode(),IcfPelletPressContent.PELLET.get());}
 @Override public void registerEntries(EntryRegistry event){IcfPelletItem.creativeStacks().forEach(stack->event.addEntry(EntryStacks.of(stack)));}
 private static final class Display extends BasicDisplay {final IcfPelletViews.Row row;Display(IcfPelletViews.Row row){super(List.of(EntryIngredient.of(row.first().stream().map(EntryStacks::of).toList()),EntryIngredient.of(row.second().stream().map(EntryStacks::of).toList()),EntryIngredient.of(EntryStacks.of(HbmContent.item("icf_pellet_empty")))),List.of(EntryIngredient.of(EntryStacks.of(row.output()))));this.row=row;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 private static final class Category implements DisplayCategory<Display>{
  @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.machineICFPress");}@Override public Renderer getIcon(){return EntryStacks.of(IcfPelletPressContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}
  @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->IcfPelletViews.draw(g,display.row,bounds.x,bounds.y)));int[][]positions={{21,24},{48,24},{48,6}};for(int i=0;i<3;i++)widgets.add(Widgets.createSlot(new Point(bounds.x+positions[i][0],bounds.y+positions[i][1])).entries(display.getInputEntries().get(i)).disableBackground().markInput());widgets.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());if(display.row.muon())widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+24)).entry(EntryStacks.of(HbmContent.item("particle_muon"))).disableBackground());return widgets;}
 }
}
