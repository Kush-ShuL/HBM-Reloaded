package com.hbm.client.custommachine;
import com.hbm.machine.custommachine.*;
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
public final class CustomMachineReiPlugin implements REIClientPlugin {
 static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","custom_machine");
 @Override public void registerCategories(CategoryRegistry registry){registry.add(new Category());for(var stack:CustomMachineContent.controllers())registry.addWorkstations(TYPE,EntryStacks.of(stack));}
 @Override public void registerDisplays(DisplayRegistry registry){for(var display:CustomMachineViews.all(registry.getRecipeManager()))registry.add(new Display(display));}
 private static final class Display extends BasicDisplay{final CustomMachineViews.Display value;Display(CustomMachineViews.Display display){super(CustomMachineViews.inputs(display).stream().map(stacks->EntryIngredient.of(stacks.stream().map(EntryStacks::of).toList())).toList(),CustomMachineViews.outputs(display).stream().map(stacks->EntryIngredient.of(stacks.stream().map(EntryStacks::of).toList())).toList());value=display;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 private static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("tile.custom_machine.name");}@Override public Renderer getIcon(){return EntryStacks.of(CustomMachineContent.controller(100));}@Override public int getDisplayWidth(Display d){return 166;}@Override public int getDisplayHeight(){return 87;}@Override public List<Widget> setupDisplay(Display d,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->CustomMachineViews.draw(g,d.value,bounds.x,bounds.y)));var ip=CustomMachineViews.coordinates(d.value,false);var op=CustomMachineViews.coordinates(d.value,true);for(int i=0;i<d.getInputEntries().size();i++)widgets.add(Widgets.createSlot(new Point(bounds.x+ip[i][0],bounds.y+ip[i][1])).entries(d.getInputEntries().get(i)).disableBackground().markInput());for(int i=0;i<d.getOutputEntries().size();i++){int lane=i;var entries=d.getOutputEntries().get(i).map(entry->{var copy=entry.copy();copy.tooltipProcessor((stack,tooltip)->{CustomMachineViews.tooltip(d.value,lane).forEach(tooltip::add);return tooltip;});return copy;});widgets.add(Widgets.createSlot(new Point(bounds.x+op[i][0],bounds.y+op[i][1])).entries(entries).disableBackground().markOutput());}widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+42)).entry(EntryStacks.of(d.value.machine())).disableBackground());return widgets;}}
}
