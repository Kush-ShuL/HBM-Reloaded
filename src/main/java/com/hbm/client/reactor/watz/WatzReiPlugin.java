package com.hbm.client.reactor.watz;
import com.hbm.machine.reactor.watz.WatzContent;
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
public final class WatzReiPlugin implements REIClientPlugin {
 @Override public void registerItemComparators(me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry event){event.register((context,stack)->stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),0),WatzContent.CASING.get().asItem());}
 @Override public void registerEntries(me.shedaniel.rei.api.client.registry.entry.EntryRegistry event){event.addEntry(EntryStacks.of(WatzContent.creativeStacks("machine").getLast()));}
 static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","watz");@Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.add(new ToolingCategory());for(var tool:WatzTooling.tools())event.addWorkstations(TOOLING,EntryStacks.of(tool));event.addWorkstations(TYPE,EntryStacks.of(WatzContent.REACTOR.get()));}@Override public void registerDisplays(DisplayRegistry event){for(var recipe:WatzViews.recipes())event.add(new Display(recipe));event.add(new ToolingDisplay());}
 static final class Display extends BasicDisplay{final WatzViews.View recipe;Display(WatzViews.View recipe){super(List.of(EntryIngredient.of(EntryStacks.of(recipe.fresh()))),List.of(EntryIngredient.of(EntryStacks.of(recipe.depleted()))));this.recipe=recipe;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.watz");}@Override public Renderer getIcon(){return EntryStacks.of(WatzContent.REACTOR.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}@Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var list=new ArrayList<Widget>();list.add(Widgets.createDrawableWidget((graphics,x,y,partial)->WatzViews.draw(graphics,bounds.x,bounds.y)));list.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());list.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entry(EntryStacks.of(WatzContent.REACTOR.get())).disableBackground());list.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());return list;}}
 static final CategoryIdentifier<ToolingDisplay> TOOLING=CategoryIdentifier.of("hbm","watz_tooling");
 static final class ToolingDisplay extends BasicDisplay{ToolingDisplay(){super(List.of(EntryIngredient.of(WatzTooling.bolts().stream().map(EntryStacks::of).toList()),EntryIngredient.of(EntryStacks.of(WatzTooling.input()))),List.of(EntryIngredient.of(EntryStacks.of(WatzTooling.output()))));}public CategoryIdentifier<ToolingDisplay> getCategoryIdentifier(){return TOOLING;}}
 static final class ToolingCategory implements DisplayCategory<ToolingDisplay>{public CategoryIdentifier<ToolingDisplay> getCategoryIdentifier(){return TOOLING;}public Component getTitle(){return Component.translatable("recipe.hbm.watz_tooling");}public Renderer getIcon(){return EntryStacks.of(WatzTooling.output());}public int getDisplayWidth(ToolingDisplay display){return 166;}public int getDisplayHeight(){return 65;}public List<Widget> setupDisplay(ToolingDisplay display,Rectangle bounds){var list=new ArrayList<Widget>();list.add(Widgets.createDrawableWidget((graphics,x,y,partial)->{WatzViews.draw(graphics,bounds.x,bounds.y);graphics.blit(net.minecraft.resources.ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png"),bounds.x+29,bounds.y+23,5,87,18,18,256,256);}));list.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());list.add(Widgets.createSlot(new Point(bounds.x+30,bounds.y+24)).entries(display.getInputEntries().get(1)).disableBackground().markInput());list.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entries(WatzTooling.tools().stream().map(EntryStacks::of).toList()).disableBackground());list.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());return list;}}

}
