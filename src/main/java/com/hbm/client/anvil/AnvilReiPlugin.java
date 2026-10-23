package com.hbm.client.anvil;
import com.hbm.client.gui.AnvilScreen;
import com.hbm.machine.anvil.*;
import java.util.*;
import java.util.stream.IntStream;
import me.shedaniel.math.*;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.*;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;
@REIPluginClient
public final class AnvilReiPlugin implements REIClientPlugin{
    static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","ntm_anvil");
    @Override public void registerItemComparators(me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry event){event.register((context,stack)->stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue()),AnvilContent.MOLD.get());for(var holder:AnvilContent.HOT)event.register((context,stack)->stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue()),holder.get());}
    @Override public void registerEntries(me.shedaniel.rei.api.client.registry.entry.EntryRegistry event){AnvilContent.MOLD.get().creativeStacks().forEach(stack->event.addEntry(EntryStacks.of(stack)));for(var holder:AnvilContent.HOT){int count=holder.get().symbol().equals("ingot_steel_dusted")?10:1;for(int i=0;i<count;i++){var stack=new net.minecraft.world.item.ItemStack(holder.get());stack.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),i);event.addEntry(EntryStacks.of(stack));}}}
    @Override public void registerCategories(CategoryRegistry event){event.add(new Category());for(var block:AnvilContent.ANVILS.values())event.addWorkstations(TYPE,EntryStacks.of(block.get()));}
    @Override public void registerDisplays(DisplayRegistry event){for(var view:AnvilViews.recipes(event.getRecipeManager()))event.add(new Display(view));}
    @Override public void registerTransferHandlers(TransferHandlerRegistry event){event.register(new SimpleTransferHandler(){@Override public Iterable<SlotAccessor> getInputSlots(Context context){return List.of(SlotAccessor.fromSlot(context.getMenu().getSlot(0)),SlotAccessor.fromSlot(context.getMenu().getSlot(1)));}@Override public Iterable<SlotAccessor> getInventorySlots(Context context){return IntStream.range(3,39).mapToObj(i->SlotAccessor.fromSlot(context.getMenu().getSlot(i))).toList();}@Override public Result handle(Context context){if(!(context.getMenu() instanceof AnvilMenu menu)||!(context.getDisplay() instanceof Display display))return Result.createNotApplicable();if(!display.view.tierValid(menu.tier()))return Result.createFailed(Component.translatable("gui.hbm.anvil.wrong_tier"));if(display.view.smithing())return SimpleTransferHandler.super.handle(context);if(context.isActuallyCrafting()){menu.focusConstruction(display.view.id());if(context.getContainerScreen() instanceof AnvilScreen screen)screen.focusRecipe(display.view.id());}return Result.createSuccessful();}});}
    static final class Display extends BasicDisplay{final AnvilViews.View view;Display(AnvilViews.View view){super(view.inputs().stream().map(stacks->EntryIngredient.of(stacks.stream().map(EntryStacks::of).toList())).toList(),view.outputs().stream().map(value->EntryIngredient.of(EntryStacks.of(value.stack()))).toList(),Optional.of(view.id()));this.view=view;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
    static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("gui.hbm.anvil.title");}@Override public Renderer getIcon(){return EntryStacks.of(AnvilContent.ANVILS.get("iron").get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();var p=AnvilViews.layout(display.view);widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->AnvilViews.draw(g,display.view,bounds.x,bounds.y)));for(int i=0;i<display.getInputEntries().size();i++){var entries=display.getInputEntries().get(i).map(entry->{var copy=entry.copy();copy.tooltipProcessor((stack,tooltip)->{AnvilViews.conditions(display.view).forEach(tooltip::add);return tooltip;});return copy;});widgets.add(Widgets.createSlot(new Point(bounds.x+p.ix()+18*(i%p.inColumns()),bounds.y+p.iy()+18*(i/p.inColumns()))).entries(entries).disableBackground().markInput());}for(int i=0;i<display.view.outputs().size();i++){var output=display.view.outputs().get(i);var entry=EntryStacks.of(output.stack());if(output.chance()!=1)entry.tooltipProcessor((stack,tooltip)->tooltip.add(Component.literal(AnvilViews.chance(output.chance())).withStyle(net.minecraft.ChatFormatting.RED)));widgets.add(Widgets.createSlot(new Point(bounds.x+p.ox()+18*(i%p.outColumns()),bounds.y+p.oy()+18*(i/p.outColumns()))).entry(entry).disableBackground().markOutput());}widgets.add(Widgets.createSlot(new Point(bounds.x+p.anvilX(),bounds.y+31)).entries(AnvilContent.exactTier(display.view.lower()).stream().map(EntryStacks::of).toList()).disableBackground());return widgets;}
    }
}
