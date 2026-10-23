package com.hbm.client.radiolysis;
import com.hbm.machine.radiolysis.*;
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
public final class RadiolysisReiPlugin implements REIClientPlugin {
    static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","radiolysis");
    @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(RadiolysisContent.ITEM.get()));}
    @Override public void registerScreens(me.shedaniel.rei.api.client.registry.screen.ScreenRegistry event){event.registerContainerClickArea(new Rectangle(66,25,25,14),RadiolysisScreen.class,TYPE);}
    @Override public void registerDisplays(DisplayRegistry event){for(var recipe:RadiolysisRecipeConfig.recipes(event.getRecipeManager()))event.add(new Display(recipe));}
    static final class Display extends BasicDisplay{final RadiolysisRecipe recipe;Display(RadiolysisRecipe recipe){super(List.of(EntryIngredient.of(EntryStacks.of(RadiolysisViews.input(recipe)))),RadiolysisViews.outputs(recipe).stream().map(s->s.isEmpty()?EntryIngredient.empty():EntryIngredient.of(EntryStacks.of(s))).toList());this.recipe=recipe;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
    static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("jei.hbm.radiolysis");}@Override public Renderer getIcon(){return EntryStacks.of(RadiolysisContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->RadiolysisViews.draw(g,bounds.x,bounds.y)));widgets.add(Widgets.createSlot(new Point(bounds.x+34,bounds.y+25)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());for(int i=0;i<display.getOutputEntries().size();i++)widgets.add(Widgets.createSlot(new Point(bounds.x+RadiolysisViews.OUTPUT[i][0],bounds.y+RadiolysisViews.OUTPUT[i][1])).entries(display.getOutputEntries().get(i)).disableBackground().markOutput());return widgets;}
    }
}
