package com.hbm.client.shredder;

import com.hbm.machine.shredder.ShredderContent;
import com.hbm.machine.shredder.ShredderMenu;
import com.hbm.machine.shredder.ShredderRecipe;
import java.util.ArrayList;
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
public final class ShredderReiPlugin implements REIClientPlugin {
    public static final CategoryIdentifier<Display> TYPE = CategoryIdentifier.of("hbm", "shredder");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("hbm", "textures/gui/nei/gui_nei_shredder.png");
    @Override public void registerCategories(CategoryRegistry event) { event.add(new Category()); event.addWorkstations(TYPE, EntryStacks.of(ShredderContent.ITEM.get())); }
    @Override public void registerDisplays(DisplayRegistry event) { event.registerRecipeFiller(ShredderRecipe.class, ShredderContent.RECIPE_TYPE.get(), Display::new); }
    @Override public void registerScreens(ScreenRegistry event) { event.registerContainerClickArea(new Rectangle(60, 78, 34, 18), ShredderScreen.class, TYPE); }
    @Override public void registerTransferHandlers(TransferHandlerRegistry event) { event.register(new SimpleTransferHandler() {
        @Override public Iterable<SlotAccessor> getInputSlots(Context context) { return List.of(SlotAccessor.fromSlot(context.getMenu().getSlot(0))); }
        @Override public Iterable<SlotAccessor> getInventorySlots(Context context) { return IntStream.range(30, 66).mapToObj(index -> SlotAccessor.fromSlot(context.getMenu().getSlot(index))).toList(); }
        @Override public Result handle(Context context) { if (!(context.getMenu() instanceof ShredderMenu) || !context.getDisplay().getCategoryIdentifier().equals(TYPE)) return Result.createNotApplicable(); return SimpleTransferHandler.super.handle(context); }
    }); }
    public static final class Display extends BasicDisplay {
        Display(RecipeHolder<ShredderRecipe> holder) { super(List.of(EntryIngredient.of(com.hbm.ore.OreRecipeInputs.examples(holder.value().input(),holder.value().metadata()).stream().map(stack->EntryStacks.of(stack).tooltipProcessor((entry,tooltip)->tooltip.addAllTexts(com.hbm.ore.OreRecipeInputs.tooltip(entry.getValue())))).toList())), List.of(EntryIngredient.of(EntryStacks.of(holder.value().result()))), Optional.of(holder.id())); }
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return TYPE; }
    }
    private static final class Category implements DisplayCategory<Display> {
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return TYPE; }
        @Override public Component getTitle() { return Component.translatable("recipe.hbm.category.shredder"); }
        @Override public Renderer getIcon() { return EntryStacks.of(ShredderContent.ITEM.get()); }
        @Override public int getDisplayHeight() { return 65; }
        @Override public int getDisplayWidth(Display display) { return 166; }
        @Override public List<Widget> setupDisplay(Display display, Rectangle bounds) {
            var widgets = new ArrayList<Widget>();
            widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, partial) -> {
                graphics.blit(TEXTURE, bounds.x, bounds.y, 5, 11, 166, 65, 256, 256);
                long tick = System.currentTimeMillis() / 50;
                int height = (int) (tick % 480 * 52 / 480), width = (int) (tick % 48 * 24 / 48);
                if (height > 0) graphics.blit(TEXTURE, bounds.x + 3, bounds.y + 6 + 52 - height, 36, 86 + 52 - height, 16, height, 256, 256);
                if (width > 0) graphics.blit(TEXTURE, bounds.x + 80, bounds.y + 23, 100, 118, width, 16, 256, 256);
            }));
            widgets.add(Widgets.createSlot(new Point(bounds.x + 39, bounds.y + 24)).entries(display.getInputEntries().getFirst()).disableBackground().markInput());
            widgets.add(Widgets.createSlot(new Point(bounds.x + 129, bounds.y + 24)).entries(display.getOutputEntries().getFirst()).disableBackground().markOutput());
            var blades = EntryIngredient.of(ShredderContent.blades().stream().map(EntryStacks::of).toList());
            widgets.add(Widgets.createSlot(new Point(bounds.x + 84, bounds.y + 6)).entries(blades).disableBackground());
            widgets.add(Widgets.createSlot(new Point(bounds.x + 84, bounds.y + 42)).entries(blades).disableBackground());
            return widgets;
        }
    }
}
