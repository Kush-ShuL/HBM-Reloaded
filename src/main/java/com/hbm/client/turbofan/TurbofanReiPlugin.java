package com.hbm.client.turbofan;
import com.hbm.machine.turbofan.TurbofanContent;
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
@REIPluginClient public final class TurbofanReiPlugin implements REIClientPlugin {
 private static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","turbofan_fuels");@Override public void registerCategories(CategoryRegistry e){e.add(new Category());e.addWorkstations(TYPE,EntryStacks.of(TurbofanContent.ITEM.get()));}@Override public void registerDisplays(DisplayRegistry e){for(var r:TurbofanFuelViews.all())e.add(new Display(r));}
 private static final class Display extends BasicDisplay{final String energy;Display(TurbofanFuelViews.View r){super(List.of(EntryIngredient.of(EntryStacks.of(r.fuel()))),List.of());energy=r.energy();}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 private static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_turbofan");}@Override public Renderer getIcon(){return EntryStacks.of(TurbofanContent.ITEM.get());}@Override public int getDisplayWidth(Display d){return 166;}@Override public int getDisplayHeight(){return 65;}@Override public List<Widget> setupDisplay(Display d,Rectangle bounds){var out=new ArrayList<Widget>();out.add(Widgets.createDrawableWidget((g,x,y,p)->TurbofanFuelViews.draw(g,bounds.x,bounds.y,d.energy)));out.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+24)).entries(d.getInputEntries().getFirst()).disableBackground().markInput());out.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entry(EntryStacks.of(TurbofanContent.ITEM.get())).disableBackground());return out;}}
}
