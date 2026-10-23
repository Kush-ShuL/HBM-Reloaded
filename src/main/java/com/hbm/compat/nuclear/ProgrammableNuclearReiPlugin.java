package com.hbm.compat.nuclear;
import com.hbm.bomb.nuclear.programmable.*;
import com.hbm.client.bomb.nuclear.programmable.*;
import java.util.*;
import me.shedaniel.math.*;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.*;
import me.shedaniel.rei.api.client.registry.display.*;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.*;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;

/** Same real source data and formula drawing as JEI, with all 21 exact circuit metadata variants. */
@REIPluginClient
public final class ProgrammableNuclearReiPlugin implements REIClientPlugin {
 private static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","nuclear_payload");
 @Override public void registerItemComparators(ItemComparatorRegistry registry){registry.register((context,stack)->ProgrammableNuclearContent.CIRCUIT.get().ordinal(stack),ProgrammableNuclearContent.CIRCUIT.get());}
 @Override public void registerEntries(EntryRegistry registry){ProgrammableNuclearContent.CIRCUIT.get().creativeStacks().forEach(stack->registry.addEntry(EntryStacks.of(stack)));}
 @Override public void registerCategories(CategoryRegistry registry){registry.add(new DisplayCategory<Display>(){
  public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}public Component getTitle(){return Component.translatable("gui.hbm.nuclear_payload");}public Renderer getIcon(){return EntryStacks.of(ProgrammableNuclearContent.CUSTOM_ITEM.get());}public int getDisplayHeight(){return 84;}public int getDisplayWidth(Display display){return 176;}
  public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();for(int i=0;i<display.getInputEntries().size();i++)widgets.add(Widgets.createSlot(new Point(bounds.x,bounds.y+i*20)).entries(display.getInputEntries().get(i)).disableBackground().markInput());widgets.add(Widgets.createSlot(new Point(bounds.x+158,bounds.y)).entry(EntryStacks.of(display.data.bomb())).disableBackground());widgets.add(Widgets.createDrawableWidget((graphics,x,y,partial)->NuclearPayloadDrawing.draw(graphics,display.data,bounds.x,bounds.y)));return widgets;}
 });registry.addWorkstations(TYPE,EntryStacks.of(ProgrammableNuclearContent.TIMED_ITEM.get()),EntryStacks.of(ProgrammableNuclearContent.CUSTOM_ITEM.get()));}
 @Override public void registerDisplays(DisplayRegistry registry){NuclearPayloadDisplays.all().forEach(display->registry.add(new Display(display)));}
 @Override public void registerScreens(ScreenRegistry registry){registry.registerContainerClickArea(new Rectangle(16,89,144,18),CustomNuclearScreen.class,TYPE);registry.registerContainerClickArea(new Rectangle(19,90,30,16),TimedBalefireScreen.class,TYPE);}
 private static final class Display extends BasicDisplay {
  final NuclearPayloadDisplays.Display data;
  Display(NuclearPayloadDisplays.Display data){super(data.inputs().stream().map(stacks->EntryIngredient.of(stacks.stream().map(EntryStacks::of).toList())).toList(),List.of());this.data=data;}
  @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}
 }
}
