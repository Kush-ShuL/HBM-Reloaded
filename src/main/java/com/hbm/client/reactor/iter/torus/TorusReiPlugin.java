package com.hbm.client.reactor.iter.torus;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.machine.reactor.iter.*;
import com.hbm.machine.reactor.iter.torus.*;
import com.hbm.recipe.generic.Blueprints;
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
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;
@REIPluginClient
public final class TorusReiPlugin implements REIClientPlugin {
    static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","fusion");
    @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.add(new ToolingCategory());for(var tool:TorusTooling.tools())event.addWorkstations(TOOLING,EntryStacks.of(tool));event.addWorkstations(TYPE,EntryStacks.of(TorusContent.ITEM.get()));}
    @Override public void registerDisplays(DisplayRegistry event){for(var recipe:FusionRecipeConfig.recipes(event.getRecipeManager()))if(TorusViews.visible(recipe))event.add(new Display(recipe));event.add(new ToolingDisplay());}
    static final class Display extends BasicDisplay{final FusionRecipe recipe;Display(FusionRecipe recipe){super(TorusViews.inputs(recipe).stream().map(stacks->EntryIngredient.of(stacks.stream().map(EntryStacks::of).toList())).toList(),TorusViews.outputs(recipe).stream().map(stacks->EntryIngredient.of(stacks.stream().map(EntryStacks::of).toList())).toList());this.recipe=recipe;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
    static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.fusionTorus");}@Override public Renderer getIcon(){return EntryStacks.of(TorusContent.ITEM.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();var drawing=TorusViews.drawing(display.recipe);var in=LegacyRecipeDisplays.inputCoordinates(TorusViews.CATEGORY,display.getInputEntries().size());var out=LegacyRecipeDisplays.outputCoordinates(TorusViews.CATEGORY,drawing);widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->TorusViews.draw(g,display.recipe,bounds.x,bounds.y)));for(int i=0;i<display.getInputEntries().size();i++)widgets.add(Widgets.createSlot(new Point(bounds.x+in[i][0],bounds.y+in[i][1])).entries(display.getInputEntries().get(i)).disableBackground().markInput());for(int i=0;i<display.getOutputEntries().size();i++){int lane=i;var entries=display.getOutputEntries().get(i).map(entry->{var copy=entry.copy();copy.tooltipProcessor((stack,tooltip)->{var value=(net.minecraft.world.item.ItemStack)stack.getValue();TorusViews.outputTooltip(display.recipe,lane,value).forEach(tooltip::add);return tooltip;});return copy;});widgets.add(Widgets.createSlot(new Point(bounds.x+out[i][0],bounds.y+out[i][1])).entries(entries).disableBackground().markOutput());}int offset=TorusViews.offset(display.recipe);widgets.add(Widgets.createSlot(new Point(bounds.x+75+offset,bounds.y+(display.recipe.isPooled()?38:31))).entry(EntryStacks.of(TorusContent.ITEM.get())).disableBackground());if(display.recipe.isPooled())widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+10)).entries(TorusViews.blueprints(display.recipe).stream().map(EntryStacks::of).toList()).disableBackground());return widgets;}
    }

 @Override public void registerItemComparators(ItemComparatorRegistry event){event.register((context,stack)->stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),0),TorusContent.COMPONENT_ITEM.get(),TorusContent.DRIVE.get());}
 @Override public void registerEntries(EntryRegistry event){for(int i=1;i<4;i++)event.addEntry(EntryStacks.of(FusionComponent.stack(i,1)));for(var stack:TorusContent.DRIVE.get().creativeStacks())event.addEntry(EntryStacks.of(stack));}
 static final CategoryIdentifier<ToolingDisplay> TOOLING=CategoryIdentifier.of("hbm","fusion_tooling");
 static final class ToolingDisplay extends BasicDisplay{ToolingDisplay(){super(List.of(EntryIngredient.of(TorusTooling.bolts().stream().map(EntryStacks::of).toList()),EntryIngredient.of(EntryStacks.of(TorusTooling.input()))),List.of(EntryIngredient.of(EntryStacks.of(TorusTooling.output()))));}public CategoryIdentifier<ToolingDisplay> getCategoryIdentifier(){return TOOLING;}}
 static final class ToolingCategory implements DisplayCategory<ToolingDisplay>{public CategoryIdentifier<ToolingDisplay> getCategoryIdentifier(){return TOOLING;}public Component getTitle(){return Component.translatable("recipe.hbm.fusion_tooling");}public Renderer getIcon(){return EntryStacks.of(TorusTooling.output());}public int getDisplayWidth(ToolingDisplay display){return 166;}public int getDisplayHeight(){return 65;}public List<Widget> setupDisplay(ToolingDisplay display,Rectangle bounds){var list=new ArrayList<Widget>();list.add(Widgets.createDrawableWidget((graphics,x,y,partial)->{TorusToolingViews.draw(graphics,bounds.x,bounds.y);graphics.blit(net.minecraft.resources.ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png"),bounds.x+29,bounds.y+23,5,87,18,18,256,256);}));list.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());list.add(Widgets.createSlot(new Point(bounds.x+30,bounds.y+24)).entries(display.getInputEntries().get(1)).disableBackground().markInput());list.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entries(TorusTooling.tools().stream().map(EntryStacks::of).toList()).disableBackground());list.add(Widgets.createSlot(new Point(bounds.x+102,bounds.y+24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());return list;}}

}
