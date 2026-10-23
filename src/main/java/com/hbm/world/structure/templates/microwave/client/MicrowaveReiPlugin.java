package com.hbm.world.structure.templates.microwave.client;
import com.hbm.world.structure.templates.microwave.*;
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
@REIPluginClient
public final class MicrowaveReiPlugin implements REIClientPlugin {
    static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","microwave");
    @Override public void registerCategories(CategoryRegistry event){event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(MicrowaveContent.ITEM.get()));}
    @Override public void registerDisplays(DisplayRegistry event){MicrowaveRecipes.recipes().forEach(recipe->event.add(new Display(recipe)));}
    @Override public void registerScreens(ScreenRegistry event){event.registerContainerClickArea(new Rectangle(104,34,22,16),MicrowaveScreen.class,TYPE);}
    @Override public void registerTransferHandlers(TransferHandlerRegistry event){event.register(new SimpleTransferHandler(){
        @Override public Iterable<SlotAccessor> getInputSlots(Context context){return List.of(SlotAccessor.fromSlot(context.getMenu().getSlot(0)));}
        @Override public Iterable<SlotAccessor> getInventorySlots(Context context){return IntStream.range(3,39).mapToObj(index->SlotAccessor.fromSlot(context.getMenu().getSlot(index))).toList();}
        @Override public Result handle(Context context){if(!(context.getMenu() instanceof MicrowaveMenu)||!context.getDisplay().getCategoryIdentifier().equals(TYPE))return Result.createNotApplicable();return SimpleTransferHandler.super.handle(context);}
    });}
    static final class Display extends BasicDisplay {
        final MicrowaveRecipes.View recipe;Display(MicrowaveRecipes.View recipe){super(List.of(EntryIngredient.of(recipe.inputs().stream().map(EntryStacks::of).toList())),List.of(EntryIngredient.of(EntryStacks.of(recipe.output()))),Optional.of(recipe.recipe()));this.recipe=recipe;}
        @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}
    }
    private static final class Category implements DisplayCategory<Display>{
        @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}
        @Override public Component getTitle(){return Component.translatable("container.microwave");}
        @Override public Renderer getIcon(){return EntryStacks.of(MicrowaveContent.ITEM.get());}
        @Override public int getDisplayWidth(Display display){return 88;}
        @Override public int getDisplayHeight(){return 34;}
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((graphics,x,y,partial)->MicrowaveRecipes.draw(graphics,bounds.x,bounds.y)));widgets.add(Widgets.createSlot(new Point(bounds.x+8,bounds.y+9)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());widgets.add(Widgets.createSlot(new Point(bounds.x+68,bounds.y+9)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());return widgets;}
    }
}
