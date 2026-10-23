package com.hbm.client.reactor.icf;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.reactor.icf.*;
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
import me.shedaniel.rei.api.common.entry.*;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;
@REIPluginClient
public final class IcfReiPlugin implements REIClientPlugin{
 static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","icf_tooling");@Override public void registerItemComparators(ItemComparatorRegistry event){for(var item:List.of(IcfReactorContent.COMPONENT_ITEM.get(),IcfReactorContent.LASER_COMPONENT_ITEM.get()))event.register((context,stack)->stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0),item);}@Override public void registerEntries(EntryRegistry event){for(var stack:IcfReactorContent.materialStacks())event.addEntry(EntryStacks.of(stack));}@Override public void registerCategories(CategoryRegistry event){event.add(new Category());for(var recipe:IcfTooling.recipes())for(var tool:recipe.tools())event.addWorkstations(TYPE,EntryStacks.of(tool));}@Override public void registerDisplays(DisplayRegistry event){for(var recipe:IcfTooling.recipes())event.add(new Display(recipe));}
 static final class Display extends BasicDisplay{final IcfTooling.View recipe;Display(IcfTooling.View recipe){super(recipe.inputs().stream().map(values->EntryIngredient.of(values.stream().map(EntryStacks::of).toList())).toList(),List.of(EntryIngredient.of(EntryStacks.of(recipe.output()))));this.recipe=recipe;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("recipe.hbm.icf.tooling");}@Override public Renderer getIcon(){return EntryStacks.of(IcfTooling.recipes().getFirst().output());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}@Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->IcfTooling.draw(display.recipe,g,bounds.x,bounds.y)));for(int i=0;i<display.getInputEntries().size();i++)widgets.add(Widgets.createSlot(new Point(bounds.x+48-i*18,bounds.y+24)).entries(display.getInputEntries().get(i)).disableBackground().markInput());widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entries(display.recipe.tools().stream().map(EntryStacks::of).toList()).disableBackground());widgets.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());return widgets;}}
}
