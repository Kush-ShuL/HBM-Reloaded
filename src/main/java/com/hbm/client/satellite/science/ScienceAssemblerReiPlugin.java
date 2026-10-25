package com.hbm.client.satellite.science;

import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.satellite.science.*;
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
public final class ScienceAssemblerReiPlugin implements REIClientPlugin {
    static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","space_assembler");
    @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(ScienceAssemblerViews.machine()));}
    @Override public void registerDisplays(DisplayRegistry event){for(var recipe:SpaceAssemblerRecipes.recipes(event.getRecipeManager()))if(ScienceAssemblerViews.visible(recipe))event.add(new Display(recipe));}
    static final class Display extends BasicDisplay {
        final SpaceAssemblerRecipe recipe;
        Display(SpaceAssemblerRecipe recipe){super(ScienceAssemblerViews.inputs(recipe).stream().map(list->EntryIngredient.of(list.stream().map(EntryStacks::of).toList())).toList(),ScienceAssemblerViews.outputs(recipe).stream().map(list->EntryIngredient.of(list.stream().map(EntryStacks::of).toList())).toList());this.recipe=recipe;}
        @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}
    }
    static final class Category implements DisplayCategory<Display> {
        @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}
        @Override public Component getTitle(){return Component.translatable("item.satellite.science_assembler.name");}
        @Override public Renderer getIcon(){return EntryStacks.of(ScienceAssemblerViews.machine());}
        @Override public int getDisplayWidth(Display display){return 166;}
        @Override public int getDisplayHeight(){return 65;}
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){
            var widgets=new ArrayList<Widget>();var recipe=display.recipe;var category=ScienceAssemblerViews.category();var drawing=ScienceAssemblerViews.drawing(recipe);
            widgets.add(Widgets.createDrawableWidget((graphics,x,y,partial)->ScienceAssemblerViews.draw(graphics,recipe,bounds.x,bounds.y)));
            var ip=LegacyRecipeDisplays.inputCoordinates(category,display.getInputEntries().size());var op=LegacyRecipeDisplays.outputCoordinates(category,drawing);
            for(int i=0;i<display.getInputEntries().size();i++)widgets.add(Widgets.createSlot(new Point(bounds.x+ip[i][0],bounds.y+ip[i][1])).entries(display.getInputEntries().get(i)).disableBackground().markInput());
            for(int i=0;i<display.getOutputEntries().size();i++){int lane=i;var entries=display.getOutputEntries().get(i).map(entry->{var copy=entry.copy();copy.tooltipProcessor((stack,tooltip)->{ScienceAssemblerViews.outputTooltip(recipe,lane,(net.minecraft.world.item.ItemStack)stack.getValue()).forEach(tooltip::add);return tooltip;});return copy;});widgets.add(Widgets.createSlot(new Point(bounds.x+op[i][0],bounds.y+op[i][1])).entries(entries).disableBackground().markOutput());}
            
            widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+(31))).entry(EntryStacks.of(ScienceAssemblerViews.machine())).disableBackground());
            return widgets;
        }
    }
}
