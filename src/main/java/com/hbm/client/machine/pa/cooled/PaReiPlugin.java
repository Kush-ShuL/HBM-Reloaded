package com.hbm.client.machine.pa.cooled;
import com.hbm.machine.pa.cooled.*;
import com.hbm.machine.pa.recipe.*;
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
public final class PaReiPlugin implements REIClientPlugin{
 static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","particle_accelerator");@Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(PaContent.ITEM.get(PaKind.DETECTOR).get()));}@Override public void registerDisplays(DisplayRegistry event){for(var recipe:PaRecipes.recipes(event.getRecipeManager()))event.add(new Display(recipe));}
 static final class Display extends BasicDisplay{final PaRecipe recipe;Display(PaRecipe recipe){super(List.of(EntryIngredient.of(recipe.input1().examples().stream().map(EntryStacks::of).toList()),EntryIngredient.of(recipe.input2().examples().stream().map(EntryStacks::of).toList())),outputs(recipe));this.recipe=recipe;}static List<EntryIngredient> outputs(PaRecipe recipe){var list=new ArrayList<EntryIngredient>();list.add(EntryIngredient.of(EntryStacks.of(recipe.output1())));recipe.output2().ifPresent(value->list.add(EntryIngredient.of(EntryStacks.of(value))));return list;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.pa_detector");}@Override public Renderer getIcon(){return EntryStacks.of(PaContent.ITEM.get(PaKind.DETECTOR).get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}@Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->PaRecipeViews.draw(display.recipe,g,bounds.x,bounds.y)));widgets.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());widgets.add(Widgets.createSlot(new Point(bounds.x+30,bounds.y+24)).entries(display.getInputEntries().get(1)).disableBackground().markInput());for(int i=0;i<display.getOutputEntries().size();i++)widgets.add(Widgets.createSlot(new Point(bounds.x+102+i*18,bounds.y+24)).entries(display.getOutputEntries().get(i)).disableBackground().markOutput());widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entry(EntryStacks.of(PaContent.ITEM.get(PaKind.DETECTOR).get())).disableBackground());return widgets;}}
}
