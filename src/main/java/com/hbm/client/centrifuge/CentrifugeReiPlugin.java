package com.hbm.client.centrifuge;

import com.hbm.client.gui.CentrifugeScreen;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;
import com.hbm.machine.centrifuge.*;
import java.util.*;
import java.util.stream.IntStream;
import me.shedaniel.math.*;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.*;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;

@REIPluginClient
public final class CentrifugeReiPlugin implements REIClientPlugin {
    public static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","centrifuge");
    @Override public void registerCategories(CategoryRegistry registration) { registration.add(new Category()); registration.addWorkstations(TYPE,EntryStacks.of(CentrifugeContent.ITEM.get())); }
    @Override public void registerDisplays(DisplayRegistry registration) { registration.registerRecipeFiller(CentrifugeRecipe.class,CentrifugeContent.TYPE.get(),Display::new); }
    @Override public void registerScreens(ScreenRegistry registration) { registration.registerContainerClickArea(new Rectangle(63,7,80,38),CentrifugeScreen.class,TYPE); }
    @Override public void registerTransferHandlers(TransferHandlerRegistry registration) { registration.register(new SimpleTransferHandler() {
        @Override public Iterable<SlotAccessor> getInputSlots(Context context) { return List.of(SlotAccessor.fromSlot(context.getMenu().getSlot(0))); }
        @Override public Iterable<SlotAccessor> getInventorySlots(Context context) { return IntStream.range(8,44).mapToObj(index->SlotAccessor.fromSlot(context.getMenu().getSlot(index))).toList(); }
        @Override public Result handle(Context context) { if(!(context.getMenu() instanceof CentrifugeMenu)||!context.getDisplay().getCategoryIdentifier().equals(TYPE)) return Result.createNotApplicable(); return SimpleTransferHandler.super.handle(context); }
    }); }
    public static final class Display extends BasicDisplay {
        final CentrifugeRecipe recipe;
        Display(RecipeHolder<CentrifugeRecipe> holder) { super(List.of(EntryIngredient.of(com.hbm.ore.OreRecipeInputs.examples(holder.value().input()).stream().map(stack->EntryStacks.of(stack).tooltipProcessor((entry,tooltip)->tooltip.addAllTexts(com.hbm.ore.OreRecipeInputs.tooltip(entry.getValue())))).toList())),holder.value().outputs().stream().map(stack->EntryIngredient.of(stack.isEmpty()?List.of():List.of(EntryStacks.of(stack)))).toList(),Optional.of(holder.id())); recipe=holder.value(); }
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return TYPE; }
    }
    private static final class Category implements DisplayCategory<Display> {
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return TYPE; }
        @Override public Component getTitle() { return Component.translatable("container.centrifuge"); }
        @Override public Renderer getIcon() { return EntryStacks.of(CentrifugeContent.ITEM.get()); }
        @Override public int getDisplayHeight() { return 65; }
        @Override public int getDisplayWidth(Display display) { return 166; }
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds) {
            var widgets=new ArrayList<Widget>(); var drawing=CentrifugeViews.drawing(display.recipe);
            widgets.add(Widgets.createDrawableWidget((graphics,x,y,partial)->LegacyDisplayDrawing.background(graphics,CentrifugeViews.CATEGORY,drawing,bounds.x,bounds.y)));
            widgets.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());
            int[][] points=LegacyRecipeDisplays.outputCoordinates(CentrifugeViews.CATEGORY,drawing);
            for(int i=0;i<display.getOutputEntries().size();i++) widgets.add(Widgets.createSlot(new Point(bounds.x+points[i][0],bounds.y+points[i][1])).entries(display.getOutputEntries().get(i)).disableBackground().markOutput());
            widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+31)).entry(EntryStacks.of(CentrifugeContent.ITEM.get())).disableBackground()); return widgets;
        }
    }
}
