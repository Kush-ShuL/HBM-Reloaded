package com.hbm.client.rtg;

import com.hbm.machine.rtg.RtgContent;
import java.util.ArrayList;
import java.util.List;
import me.shedaniel.math.*;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.*;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.*;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;

@REIPluginClient
public final class RtgReiPlugin implements REIClientPlugin {
    private static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","rtg");
    @Override public void registerEntries(EntryRegistry event){for(var stack:RtgContent.creativeStacks("control"))event.addEntry(EntryStacks.of(stack));}
    @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(RtgContent.ITEM.get()));}
    @Override public void registerDisplays(DisplayRegistry event){for(var recipe:RtgViews.recipes())event.add(new Display(recipe));}
    static final class Display extends BasicDisplay{Display(RtgViews.Decay recipe){super(List.of(EntryIngredients.of(recipe.pellet())),List.of(EntryIngredients.of(recipe.depleted())));}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
    static final class Category implements DisplayCategory<Display>{
        @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}
        @Override public Component getTitle(){return Component.translatable("container.rtg");}
        @Override public Renderer getIcon(){return EntryStacks.of(RtgContent.ITEM.get());}
        @Override public int getDisplayWidth(Display display){return 166;}
        @Override public int getDisplayHeight(){return 65;}
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->RtgViews.draw(g,bounds.x,bounds.y)));widgets.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+24)).entries(display.getInputEntries().get(0)).disableBackground().markInput());widgets.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entry(EntryStacks.of(RtgContent.ITEM.get())).disableBackground());return widgets;}
    }
}
