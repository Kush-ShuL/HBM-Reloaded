package com.hbm.client.soyuzlauncher;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;
import com.hbm.machine.soyuzlauncher.SoyuzLauncherContent;
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
@REIPluginClient public final class SoyuzConstructionReiPlugin implements REIClientPlugin {
 static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","soyuz_construction");
 @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(SoyuzLauncherContent.STRUCTURE_ITEM.get()));}
 @Override public void registerDisplays(DisplayRegistry event){event.add(new Display());}
 static final class Display extends BasicDisplay{final LegacyRecipeDisplays.Display recipe;Display(){this(SoyuzConstruction.display());}Display(LegacyRecipeDisplays.Display recipe){super(recipe.inputs().stream().map(s->EntryIngredient.of(s.alternatives().stream().map(EntryStacks::of).toList())).toList(),recipe.outputs().stream().map(s->EntryIngredient.of(s.alternatives().stream().map(EntryStacks::of).toList())).toList());this.recipe=recipe;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 static final class Category implements DisplayCategory<Display>{
  @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("recipe.hbm.category.soyuz_construction");}@Override public Renderer getIcon(){return EntryStacks.of(SoyuzLauncherContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}
  @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();var category=SoyuzConstruction.category();widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->LegacyDisplayDrawing.background(g,category,display.recipe,bounds.x,bounds.y)));var points=LegacyRecipeDisplays.inputCoordinates(category,display.recipe.inputs().size());for(int i=0;i<points.length;i++)widgets.add(Widgets.createSlot(new Point(bounds.x+points[i][0],bounds.y+points[i][1])).entries(display.getInputEntries().get(i)).disableBackground().markInput());widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entry(EntryStacks.of(SoyuzLauncherContent.STRUCTURE_ITEM.get())).disableBackground());widgets.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());return widgets;}
 }
}
