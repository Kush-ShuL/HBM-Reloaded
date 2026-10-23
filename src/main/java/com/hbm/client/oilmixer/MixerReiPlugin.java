package com.hbm.client.oilmixer;
import com.hbm.machine.oilmixer.*;
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
@REIPluginClient public final class MixerReiPlugin implements REIClientPlugin {
 static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","oil_mixer");@Override public void registerCategories(CategoryRegistry e){e.add(new Category());e.addWorkstations(TYPE,EntryStacks.of(MixerContent.ITEM.get()));}@Override public void registerDisplays(DisplayRegistry e){for(var recipe:MixerRecipeConfig.recipes(e.getRecipeManager()))e.add(new Display(recipe));}@Override public void registerScreens(me.shedaniel.rei.api.client.registry.screen.ScreenRegistry e){e.registerContainerClickArea(new Rectangle(66,20,52,44),MixerScreen.class,TYPE);}
 static final class Display extends BasicDisplay {final MixerRecipe recipe;Display(MixerRecipe r){super(MixerViews.inputs(r).stream().map(stacks->EntryIngredient.of(stacks.stream().map(stack->EntryStacks.of(stack).tooltipProcessor((entry,tooltip)->tooltip.addAllTexts(com.hbm.ore.OreRecipeInputs.tooltip(entry.getValue())))).toList())).toList(),List.of(EntryIngredient.of(EntryStacks.of(MixerViews.output(r)))));recipe=r;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.machineMixer");}@Override public Renderer getIcon(){return EntryStacks.of(MixerContent.ITEM.get());}@Override public int getDisplayWidth(Display d){return 166;}@Override public int getDisplayHeight(){return 65;}@Override public List<Widget> setupDisplay(Display d,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->MixerViews.draw(d.recipe,g,bounds.x,bounds.y)));for(int i=0;i<d.getInputEntries().size();i++)widgets.add(Widgets.createSlot(new Point(bounds.x+21+i*18,bounds.y+24)).entries(d.getInputEntries().get(i)).disableBackground().markInput());widgets.add(Widgets.createSlot(new Point(bounds.x+113,bounds.y+24)).entries(d.getOutputEntries().getFirst()).disableBackground().markOutput());widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entry(EntryStacks.of(MixerContent.ITEM.get())).disableBackground());return widgets;}}
}
