package com.hbm.client.oreslopper;
import com.hbm.machine.oreslopper.*;
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
public final class OreSlopperReiPlugin implements REIClientPlugin {
    static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","oreslopper");
    @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(OreSlopperContent.ITEM.get()));}
    @Override public void registerScreens(me.shedaniel.rei.api.client.registry.screen.ScreenRegistry event){event.registerContainerClickArea(new Rectangle(62,18,34,34),OreSlopperScreen.class,TYPE);}
    @Override public void registerDisplays(DisplayRegistry event){event.add(new Display());}
    static final class Display extends BasicDisplay{Display(){super(OreSlopperViews.inputs().stream().map(s->EntryIngredient.of(EntryStacks.of(s))).toList(),OreSlopperViews.outputs().stream().map(s->EntryIngredient.of(EntryStacks.of(s))).toList());}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
    static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("jei.hbm.oreslopper");}@Override public Renderer getIcon(){return EntryStacks.of(OreSlopperContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->OreSlopperViews.draw(g,bounds.x,bounds.y)));for(int i=0;i<display.getInputEntries().size();i++)widgets.add(Widgets.createSlot(new Point(bounds.x+OreSlopperViews.INPUT[i][0],bounds.y+OreSlopperViews.INPUT[i][1])).entries(display.getInputEntries().get(i)).disableBackground().markInput());widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entry(EntryStacks.of(OreSlopperContent.ITEM.get())).disableBackground());for(int i=0;i<display.getOutputEntries().size();i++)widgets.add(Widgets.createSlot(new Point(bounds.x+OreSlopperViews.OUTPUT[i][0],bounds.y+OreSlopperViews.OUTPUT[i][1])).entries(display.getOutputEntries().get(i)).disableBackground().markOutput());return widgets;}
    }
}
