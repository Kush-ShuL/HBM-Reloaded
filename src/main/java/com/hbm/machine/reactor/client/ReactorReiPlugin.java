package com.hbm.machine.reactor.client;
import com.hbm.machine.reactor.*;
import java.util.*;
import me.shedaniel.math.*;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.*;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
@REIPluginClient
public final class ReactorReiPlugin implements REIClientPlugin {
    static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","research_breeding");
    @Override public void registerItemComparators(me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry event){for(var item:ReactorContent.wasteItems())event.register((context,stack)->stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),0),item);}
    @Override public void registerEntries(me.shedaniel.rei.api.client.registry.entry.EntryRegistry event){for(var item:ReactorContent.fuelItems())event.addEntry(EntryStacks.of(item));for(var item:ReactorContent.wasteItems())for(int metadata=0;metadata<2;metadata++){var stack=new net.minecraft.world.item.ItemStack(item);stack.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),metadata);event.addEntry(EntryStacks.of(stack));}}
    @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(ReactorContent.BREEDING_ITEM.get()));}
    @Override public void registerDisplays(DisplayRegistry event){var level=Minecraft.getInstance().level;if(level!=null)for(var recipe:BreedingRecipeConfig.recipes(level.getRecipeManager()))event.add(new Display(recipe));}
    @Override public void registerScreens(ScreenRegistry event){event.registerContainerClickArea(new Rectangle(53,32,70,20),BreedingReactorScreen.class,TYPE);}
    private static final class Display extends BasicDisplay {
        private final BreedingRecipe recipe;
        Display(BreedingRecipe recipe){super(List.of(EntryIngredient.of(Arrays.stream(recipe.input().getItems()).map(EntryStacks::of).toList())),List.of(EntryIngredient.of(EntryStacks.of(recipe.result()))));this.recipe=recipe;}
        @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}
    }
    private static final class Category implements DisplayCategory<Display>{
        @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.reactorBreeding");}@Override public Renderer getIcon(){return EntryStacks.of(ReactorContent.BREEDING_ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 60;}
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((graphics,x,y,partial)->BreedingRecipeView.draw(graphics,display.recipe,bounds.x,bounds.y)));widgets.add(Widgets.createSlot(new Point(bounds.x+30,bounds.y+24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());widgets.add(Widgets.createSlot(new Point(bounds.x+120,bounds.y+24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());return widgets;}
    }
}
