package com.hbm.client.alloy;
import com.hbm.client.gui.AlloyFurnaceScreen;
import com.hbm.machine.alloy.*;
import java.util.*;
import java.util.stream.IntStream;
import me.shedaniel.math.*;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.*;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
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
public final class AlloyReiPlugin implements REIClientPlugin {
    public static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","alloy");
    @Override public void registerCategories(CategoryRegistry event) { event.add(new Category()); event.addWorkstations(TYPE,EntryStacks.of(AlloyContent.ITEM.get())); }
    @Override public void registerDisplays(DisplayRegistry event) { event.registerRecipesFiller(AlloyRecipe.class,AlloyContent.TYPE.get(),holder->{ var hidden=event.getRecipeManager().getAllRecipesFor(AlloyContent.TYPE.get()).stream().map(value->value.value()).filter(AlloyRecipe::hidden); return hidden.anyMatch(value->AlloyBlockEntity.sameLegacy(value.result(),holder.value().result()))?List.of():List.of(new Display(holder)); }); }
    @Override public void registerEntries(EntryRegistry event) { event.removeEntry(EntryStacks.of(AlloyContent.ON_ITEM.get())); }
    @Override public void registerScreens(ScreenRegistry event) { event.registerContainerClickArea(new Rectangle(101,36,24,18),AlloyFurnaceScreen.class,TYPE); }
    @Override public void registerTransferHandlers(TransferHandlerRegistry event) { event.register(new SimpleTransferHandler() {
        @Override public Iterable<SlotAccessor> getInputSlots(Context context) { return List.of(SlotAccessor.fromSlot(context.getMenu().getSlot(0)),SlotAccessor.fromSlot(context.getMenu().getSlot(1))); }
        @Override public Iterable<SlotAccessor> getInventorySlots(Context context) { return IntStream.range(4,40).mapToObj(index->SlotAccessor.fromSlot(context.getMenu().getSlot(index))).toList(); }
        @Override public Result handle(Context context) { if(!(context.getMenu() instanceof AlloyMenu)||!context.getDisplay().getCategoryIdentifier().equals(TYPE)) return Result.createNotApplicable(); return SimpleTransferHandler.super.handle(context); }
    }); }
    public static final class Display extends BasicDisplay {
        final AlloyRecipe recipe;
        Display(RecipeHolder<AlloyRecipe> holder) { super(List.of(EntryIngredient.of(com.hbm.ore.OreRecipeInputs.examples(holder.value().first()).stream().map(stack->EntryStacks.of(stack).tooltipProcessor((entry,tooltip)->tooltip.addAllTexts(com.hbm.ore.OreRecipeInputs.tooltip(entry.getValue())))).toList()),EntryIngredient.of(com.hbm.ore.OreRecipeInputs.examples(holder.value().second()).stream().map(stack->EntryStacks.of(stack).tooltipProcessor((entry,tooltip)->tooltip.addAllTexts(com.hbm.ore.OreRecipeInputs.tooltip(entry.getValue())))).toList())),List.of(EntryIngredient.of(EntryStacks.of(holder.value().result()))),Optional.of(holder.id())); recipe=holder.value(); }
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return TYPE; }
    }
    private static final class Category implements DisplayCategory<Display> {
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return TYPE; }
        @Override public Component getTitle() { return Component.translatable("recipe.hbm.category.alloy"); }
        @Override public Renderer getIcon() { return EntryStacks.of(AlloyContent.ITEM.get()); }
        @Override public int getDisplayHeight() { return 65; }
        @Override public int getDisplayWidth(Display display) { return 166; }
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds) {
            var widgets=new ArrayList<Widget>(); widgets.add(Widgets.createDrawableWidget((graphics,x,y,partial)->AlloyViews.draw(graphics,bounds.x,bounds.y)));
            widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+7)).entries(display.getInputEntries().get(0)).disableBackground().markInput()); widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+43)).entries(display.getInputEntries().get(1)).disableBackground().markInput());
            widgets.add(Widgets.createSlot(new Point(bounds.x+129,bounds.y+25)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput()); widgets.add(Widgets.createSlot(new Point(bounds.x+3,bounds.y+25)).entries(AlloyViews.fuels().stream().map(EntryStacks::of).toList()).disableBackground()); return widgets;
        }
    }
}
