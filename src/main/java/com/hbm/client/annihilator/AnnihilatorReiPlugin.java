package com.hbm.client.annihilator;
import com.hbm.machine.annihilator.AnnihilatorContent;
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
public final class AnnihilatorReiPlugin implements REIClientPlugin {
 static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","annihilating");@Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(AnnihilatorContent.ITEM.get()));}@Override public void registerDisplays(DisplayRegistry event){for(var recipe:AnnihilatorViews.recipes())event.add(new Display(recipe));}
 private static final class Display extends BasicDisplay{final AnnihilatorViews.View recipe;Display(AnnihilatorViews.View recipe){super(List.of(EntryIngredient.of(recipe.inputs().stream().map(stack->EntryStacks.of(stack)).toList())),List.of(EntryIngredient.of(EntryStacks.of(recipe.output()))));this.recipe=recipe;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 private static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.annihilator");}@Override public Renderer getIcon(){return EntryStacks.of(AnnihilatorContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}@Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var rows=new ArrayList<Widget>();rows.add(Widgets.createRecipeBase(bounds));rows.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+24)).entries(display.getInputEntries().getFirst()).markInput());rows.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entries(display.getOutputEntries().getFirst()).markOutput());rows.add(Widgets.createLabel(new Point(bounds.x+83,bounds.y+48),Component.translatable("gui.hbm.annihilator.threshold",String.format(Locale.US,"%,d",display.recipe.amount()))).color(0x404040).noShadow());return rows;}}
}
