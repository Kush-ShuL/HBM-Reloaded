package com.hbm.client.oilcatalyticcracker;
import com.hbm.machine.oilcatalyticcracker.*;
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
@REIPluginClient public final class CatalyticCrackerReiPlugin implements REIClientPlugin {
 static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","catalytic_cracking");@Override public void registerCategories(CategoryRegistry e){e.add(new Category());e.addWorkstations(TYPE,EntryStacks.of(CatalyticCrackerContent.ITEM.get()));}@Override public void registerDisplays(DisplayRegistry e){for(var recipe:CatalyticCrackerRecipes.recipes(e.getRecipeManager()))e.add(new Display(recipe));}
 private static final class Display extends BasicDisplay{final CatalyticCrackerRecipe recipe;Display(CatalyticCrackerRecipe recipe){super(CatalyticCrackerViews.inputs(recipe).stream().map(stack->EntryIngredient.of(EntryStacks.of(stack))).toList(),CatalyticCrackerViews.outputs(recipe).stream().map(stack->EntryIngredient.of(EntryStacks.of(stack))).toList());this.recipe=recipe;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 private static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("gui.hbm.catalytic_cracker.recipe");}@Override public Renderer getIcon(){return EntryStacks.of(CatalyticCrackerContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}
  @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->CatalyticCrackerViews.draw(display.recipe,g,bounds.x,bounds.y)));for(int i=0;i<display.getInputEntries().size();i++)widgets.add(Widgets.createSlot(new Point(bounds.x+30+i*18,bounds.y+24)).entries(display.getInputEntries().get(i)).disableBackground().markInput());for(int i=0;i<display.getOutputEntries().size();i++)widgets.add(Widgets.createSlot(new Point(bounds.x+102+i*18,bounds.y+24)).entries(display.getOutputEntries().get(i)).disableBackground().markOutput());widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entry(EntryStacks.of(CatalyticCrackerContent.ITEM.get())).disableBackground());return widgets;}
 }
}
