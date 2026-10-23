package com.hbm.client.reactor.rbmk.irradiation;
import com.hbm.machine.reactor.rbmk.irradiation.*;
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
public final class RbmkIrradiationReiPlugin implements REIClientPlugin {
    static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","irradiation");
    @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(RbmkOutgasserContent.ITEM.get()));var fusion=RbmkIrradiationViews.fusion();if(!fusion.isEmpty())event.addWorkstations(TYPE,EntryStacks.of(fusion));}@Override public void registerDisplays(DisplayRegistry event){for(var recipe:RbmkIrradiationRecipes.recipes(event.getRecipeManager()))event.add(new Display(recipe));}@Override public void registerScreens(me.shedaniel.rei.api.client.registry.screen.ScreenRegistry event){event.registerContainerClickArea(new Rectangle(75,26,16,32),RbmkOutgasserScreen.class,TYPE);}
    static final class Display extends BasicDisplay{final RbmkIrradiationRecipe recipe;Display(RbmkIrradiationRecipe recipe){super(List.of(EntryIngredient.of(recipe.input().examples().stream().map(EntryStacks::of).toList())),RbmkIrradiationViews.outputs(recipe).stream().map(stack->EntryIngredient.of(EntryStacks.of(stack))).toList());this.recipe=recipe;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
    static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.rbmk_outgasser");}@Override public Renderer getIcon(){return EntryStacks.of(RbmkOutgasserContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}@Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->RbmkIrradiationViews.draw(display.recipe,g,bounds.x,bounds.y)));widgets.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());for(int i=0;i<display.getOutputEntries().size();i++)widgets.add(Widgets.createSlot(new Point(bounds.x+102+i*18,bounds.y+24)).entries(display.getOutputEntries().get(i)).disableBackground().markOutput());var machines=RbmkIrradiationViews.machines(display.recipe);if(!machines.isEmpty())widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entries(EntryIngredient.of(machines.stream().map(EntryStacks::of).toList())).disableBackground());return widgets;}}
}
