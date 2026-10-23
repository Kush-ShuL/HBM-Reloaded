package com.hbm.client.woodprocessing;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.woodprocessing.WoodProcessingContent;
import java.util.*;
import me.shedaniel.math.*;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.*;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.*;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;
@REIPluginClient public final class WoodReiPlugin implements REIClientPlugin {
    private static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","sawmill");
    @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(WoodProcessingContent.SAWMILL_ITEM.get()));}@Override public void registerDisplays(DisplayRegistry event){for(var view:SawmillViews.views())event.add(new Display(view));}@Override public void registerEntries(EntryRegistry event){for(var stack:WoodProcessingContent.allStacks())event.addEntry(EntryStacks.of(stack));}@Override public void registerItemComparators(me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry event){event.register((context,stack)->stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0),WoodProcessingContent.SAWMILL_ITEM.get());}
    private static final class Display extends BasicDisplay {final SawmillViews.View view;Display(SawmillViews.View view){super(List.of(EntryIngredient.of(view.inputs().stream().map(EntryStacks::of).toList())),outputs(view));this.view=view;}private static List<EntryIngredient> outputs(SawmillViews.View view){var result=new ArrayList<EntryIngredient>();result.add(EntryIngredient.of(EntryStacks.of(view.result())));if(view.dustChance()>0)result.add(EntryIngredient.of(EntryStacks.of(SawmillViews.dust()).tooltipProcessor((entry,tooltip)->tooltip.addAllTexts(List.of(SawmillViews.chance(view))))));return result;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
    private static final class Category implements DisplayCategory<Display> {@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_sawmill");}@Override public Renderer getIcon(){return EntryStacks.of(WoodProcessingContent.SAWMILL_ITEM.get());}@Override public int getDisplayWidth(Display view){return 166;}@Override public int getDisplayHeight(){return 65;}
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->SawmillViews.draw(g,display.view,bounds.x,bounds.y)));widgets.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+24)).entries(display.getInputEntries().getFirst()).markInput().disableBackground());widgets.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entries(display.getOutputEntries().getFirst()).markOutput().disableBackground());if(display.view.dustChance()>0)widgets.add(Widgets.createSlot(new Point(bounds.x+120,bounds.y+24)).entries(display.getOutputEntries().get(1)).markOutput().disableBackground());widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entry(EntryStacks.of(WoodProcessingContent.SAWMILL_ITEM.get())).disableBackground());return widgets;}
    }
}
