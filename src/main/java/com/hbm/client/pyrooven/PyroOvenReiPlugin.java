package com.hbm.client.pyrooven;
import com.hbm.machine.pyrooven.*;
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
public final class PyroOvenReiPlugin implements REIClientPlugin {
 static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","pyrooven");
 @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(PyroOvenContent.ITEM.get()));}@Override public void registerDisplays(DisplayRegistry event){for(var recipe:PyroOvenRecipes.recipes(event.getRecipeManager()))if(!PyroOvenViews.inputs(recipe).isEmpty()&&!PyroOvenViews.outputs(recipe).isEmpty())event.add(new Display(recipe));}@Override public void registerScreens(me.shedaniel.rei.api.client.registry.screen.ScreenRegistry event){event.registerContainerClickArea(new Rectangle(50,35,28,14),PyroOvenScreen.class,TYPE);}
 static final class Display extends BasicDisplay{final PyroOvenRecipe recipe;Display(PyroOvenRecipe recipe){super(PyroOvenViews.inputs(recipe).stream().map(options->EntryIngredient.of(options.stream().map(s->EntryStacks.of(s)).toList())).toList(),PyroOvenViews.outputs(recipe).stream().map(s->EntryIngredient.of(EntryStacks.of(s))).toList());this.recipe=recipe;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.machinePyroOven");}@Override public Renderer getIcon(){return EntryStacks.of(PyroOvenContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}
  @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->PyroOvenViews.draw(display.recipe,g,bounds.x,bounds.y)));for(int i=0;i<display.getInputEntries().size();i++)widgets.add(Widgets.createSlot(new Point(bounds.x+48-i*18,bounds.y+24)).entries(display.getInputEntries().get(i)).disableBackground().markInput());for(int i=0;i<display.getOutputEntries().size();i++)widgets.add(Widgets.createSlot(new Point(bounds.x+102+i*18,bounds.y+24)).entries(display.getOutputEntries().get(i)).disableBackground().markOutput());widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entry(EntryStacks.of(PyroOvenContent.ITEM.get())).disableBackground());return widgets;}
 }
}
