package com.hbm.machine.foundry.client;
import com.hbm.machine.foundry.*;
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
public final class FoundryReiPlugin implements REIClientPlugin {
 static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","foundry_casting");
 @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(FoundryContent.block(FoundryContent.Kind.MOLD)),EntryStacks.of(FoundryContent.block(FoundryContent.Kind.BASIN)));}
 @Override public void registerDisplays(DisplayRegistry event){for(var recipe:FoundryCastingViews.recipes())event.add(new Display(recipe));}
 static final class Display extends BasicDisplay{Display(FoundryCastingViews.Casting recipe){super(List.of(EntryIngredient.of(EntryStacks.of(recipe.liquid())),EntryIngredient.of(EntryStacks.of(recipe.mold())),EntryIngredient.of(EntryStacks.of(recipe.basin()))),List.of(EntryIngredient.of(EntryStacks.of(recipe.output()))));}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 static final class Category implements DisplayCategory<Display>{
  @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("recipe.hbm.foundry.casting");}@Override public Renderer getIcon(){return EntryStacks.of(FoundryContent.block(FoundryContent.Kind.MOLD));}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}
  @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((graphics,x,y,partial)->FoundryCastingViews.draw(graphics,bounds.x,bounds.y)));int[][] positions={{48,24},{75,6},{75,42}};for(int i=0;i<3;i++)widgets.add(Widgets.createSlot(new Point(bounds.x+positions[i][0],bounds.y+positions[i][1])).entries(display.getInputEntries().get(i)).disableBackground().markInput());widgets.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());return widgets;}
 }
}
