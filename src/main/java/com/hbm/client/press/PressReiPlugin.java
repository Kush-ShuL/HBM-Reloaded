package com.hbm.client.press;

import com.hbm.client.gui.PressScreen;
import com.hbm.item.StampItem;
import com.hbm.machine.press.PressContent;
import com.hbm.machine.press.PressMenu;
import com.hbm.machine.press.PressRecipe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

@REIPluginClient
public final class PressReiPlugin implements REIClientPlugin {
    public static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","press");
    private static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei_press.png");
    @Override public void registerCategories(CategoryRegistry registration) { registration.add(new Category()); registration.addWorkstations(TYPE,EntryStacks.of(PressContent.ITEM.get())); }
    // Native holders include the server-generated configured-material WIRE loop.
    @Override public void registerDisplays(DisplayRegistry registration) { registration.registerRecipeFiller(PressRecipe.class,PressContent.RECIPE_TYPE.get(),Display::new); }
    @Override public void registerScreens(ScreenRegistry registration) { registration.registerContainerClickArea(new Rectangle(98,23,24,18),PressScreen.class,TYPE); }
    @Override public void registerTransferHandlers(TransferHandlerRegistry registration) { registration.register(new SimpleTransferHandler() {
        @Override public Iterable<SlotAccessor> getInputSlots(Context context) { return List.of(SlotAccessor.fromSlot(context.getMenu().getSlot(2)),SlotAccessor.fromSlot(context.getMenu().getSlot(1))); }
        @Override public Iterable<SlotAccessor> getInventorySlots(Context context) { return IntStream.range(4,49).mapToObj(index->SlotAccessor.fromSlot(context.getMenu().getSlot(index))).toList(); }
        @Override public Result handle(Context context) { if(!(context.getMenu() instanceof PressMenu) || !context.getDisplay().getCategoryIdentifier().equals(TYPE)) return Result.createNotApplicable(); return SimpleTransferHandler.super.handle(context); }
    }); }
    public static final class Display extends BasicDisplay {
        Display(RecipeHolder<PressRecipe> holder) {
            super(List.of(EntryIngredient.of(Arrays.stream(holder.value().input().getItems()).map(EntryStacks::of).toList()),
                    EntryIngredient.of(StampItem.stamps(holder.value().stamp()).stream().map(EntryStacks::of).toList())),
                    List.of(EntryIngredient.of(EntryStacks.of(holder.value().result()))),Optional.of(holder.id()));
        }
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return TYPE; }
    }
    private static final class Category implements DisplayCategory<Display> {
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return TYPE; }
        @Override public Component getTitle() { return Component.translatable("container.press"); }
        @Override public Renderer getIcon() { return EntryStacks.of(PressContent.ITEM.get()); }
        @Override public int getDisplayHeight() { return 65; }
        @Override public int getDisplayWidth(Display display) { return 166; }
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds) {
            var widgets=new ArrayList<Widget>();
            widgets.add(Widgets.createDrawableWidget((graphics,mouseX,mouseY,partial)-> {
                graphics.blit(TEXTURE,bounds.x,bounds.y,5,11,166,65,256,256);
                int height=(int)((System.currentTimeMillis()/50%20)*18/20);
                if(height>0) graphics.blit(TEXTURE,bounds.x+47,bounds.y+24,0,86,18,height,256,256);
            }));
            widgets.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+42)).entries(display.getInputEntries().get(0)).disableBackground().markInput());
            widgets.add(Widgets.createSlot(new Point(bounds.x+48,bounds.y+6)).entries(display.getInputEntries().get(1)).disableBackground().markInput());
            widgets.add(Widgets.createSlot(new Point(bounds.x+111,bounds.y+24)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
            return widgets;
        }
    }
}
