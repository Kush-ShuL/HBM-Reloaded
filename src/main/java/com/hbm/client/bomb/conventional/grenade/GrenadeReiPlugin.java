package com.hbm.client.bomb.conventional.grenade;
import com.hbm.bomb.conventional.grenade.*;
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
import net.minecraft.world.item.*;
/** Exact source assembly/usages and all475 NBT combinations in REI. */
@REIPluginClient
public final class GrenadeReiPlugin implements REIClientPlugin{
 static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","grenade_assembly");
 @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(Items.CRAFTING_TABLE));}
 @Override public void registerDisplays(DisplayRegistry event){for(var recipe:GrenadeRecipes.all())event.add(new Display(recipe));}
 @Override public void registerEntries(EntryRegistry event){GrenadeContent.creativeStacks().stream().skip(1).forEach(stack->event.addEntry(EntryStacks.of(stack)));}
 @Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->GrenadeRecipes.key(stack),GrenadeContent.UNIVERSAL.get());}
 static final class Display extends BasicDisplay{
  final GrenadeRecipes.Assembly recipe;Display(GrenadeRecipes.Assembly recipe){super(recipe.inputs().stream().map(stack->EntryIngredient.of(EntryStacks.of(stack))).toList(),List.of(EntryIngredient.of(EntryStacks.of(recipe.output()))));this.recipe=recipe;}
  @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}
 }
 static final class Category implements DisplayCategory<Display>{
  @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("hbm.grenades.assembly");}@Override public Renderer getIcon(){return EntryStacks.of(Items.CRAFTING_TABLE);}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}
  @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->GrenadeRecipeDrawing.draw(g,display.recipe.inputs().size(),bounds.x,bounds.y)));var points=GrenadeRecipeDrawing.inputs(display.recipe.inputs().size());for(int i=0;i<points.length;i++)widgets.add(Widgets.createSlot(new Point(bounds.x+points[i][0],bounds.y+points[i][1])).entries(display.getInputEntries().get(i)).disableBackground().markInput());widgets.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entry(EntryStacks.of(Items.CRAFTING_TABLE)).disableBackground());return widgets;}
 }
}
