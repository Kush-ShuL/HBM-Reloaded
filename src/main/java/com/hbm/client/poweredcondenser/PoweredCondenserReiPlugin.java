package com.hbm.client.poweredcondenser;
import com.hbm.machine.poweredcondenser.PoweredCondenserContent;
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
@REIPluginClient public final class PoweredCondenserReiPlugin implements REIClientPlugin {
 private static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","powered_condenser");
 @Override public void registerCategories(CategoryRegistry e){e.add(new Category());e.addWorkstations(TYPE,EntryStacks.of(PoweredCondenserContent.ITEM.get()));}@Override public void registerDisplays(DisplayRegistry e){e.add(new Display(PoweredCondenserViews.view()));}
 private static final class Display extends BasicDisplay{Display(PoweredCondenserViews.View r){super(List.of(EntryIngredient.of(EntryStacks.of(r.input()))),List.of(EntryIngredient.of(EntryStacks.of(r.output()))));}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 private static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_condenser_powered");}@Override public Renderer getIcon(){return EntryStacks.of(PoweredCondenserContent.ITEM.get());}@Override public int getDisplayWidth(Display d){return 166;}@Override public int getDisplayHeight(){return 65;}@Override public List<Widget> setupDisplay(Display d,Rectangle bounds){var result=new ArrayList<Widget>();result.add(Widgets.createDrawableWidget((g,x,y,partial)->PoweredCondenserViews.draw(g,bounds.x,bounds.y)));result.add(Widgets.createSlot(new Point(bounds.x+30,bounds.y+24)).entries(d.getInputEntries().getFirst()).disableBackground().markInput());result.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entries(d.getOutputEntries().getFirst()).disableBackground().markOutput());return result;}}
}
