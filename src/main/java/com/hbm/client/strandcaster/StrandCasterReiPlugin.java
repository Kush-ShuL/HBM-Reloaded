package com.hbm.client.strandcaster;
import com.hbm.machine.strandcaster.StrandCasterContent;
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
public final class StrandCasterReiPlugin implements REIClientPlugin {
 static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","strand_casting");@Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(StrandCasterContent.ITEM.get()));}@Override public void registerDisplays(DisplayRegistry event){for(var recipe:StrandCasterViews.recipes())event.add(new Display(recipe));}
 private static final class Display extends BasicDisplay {final StrandCasterViews.Casting recipe;Display(StrandCasterViews.Casting recipe){super(List.of(EntryIngredient.of(EntryStacks.of(recipe.molten())),EntryIngredient.of(EntryStacks.of(recipe.mold()))),List.of(EntryIngredient.of(EntryStacks.of(recipe.output()))));this.recipe=recipe;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 private static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.machineStrandCaster");}@Override public Renderer getIcon(){return EntryStacks.of(StrandCasterContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}@Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var result=new ArrayList<Widget>();result.add(Widgets.createRecipeBase(bounds));result.add(Widgets.createDrawableWidget((g,x,y,partial)->StrandCasterViews.draw(display.recipe,g,bounds.x,bounds.y)));result.add(Widgets.createSlot(new Point(bounds.x+28,bounds.y+20)).entries(display.getInputEntries().getFirst()).markInput());result.add(Widgets.createSlot(new Point(bounds.x+58,bounds.y+20)).entries(display.getInputEntries().get(1)));result.add(Widgets.createSlot(new Point(bounds.x+108,bounds.y+20)).entries(display.getOutputEntries().getFirst()).markOutput());return result;}}
}
