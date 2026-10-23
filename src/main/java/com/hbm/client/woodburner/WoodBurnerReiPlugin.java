package com.hbm.client.woodburner;
import com.hbm.client.gui.WoodBurnerScreen;
import com.hbm.machine.woodburner.WoodBurnerContent;
import java.util.*;
import me.shedaniel.math.*;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.*;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.*;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
@REIPluginClient
public final class WoodBurnerReiPlugin implements REIClientPlugin {
    static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","wood_burner_fuel");
    @Override public void registerCategories(CategoryRegistry event) { event.add(new Category()); event.addWorkstations(TYPE,EntryStacks.of(WoodBurnerContent.ITEM.get())); }
    @Override public void registerDisplays(DisplayRegistry event) { var items=new ArrayList<ItemStack>(); EntryRegistry.getInstance().getEntryStacks().filter(entry->entry.getType().equals(VanillaEntryTypes.ITEM)).forEach(entry->items.add((ItemStack)entry.getValue())); for(var recipe:WoodBurnerFuelViews.recipes(items)) event.add(new Display(recipe)); }
    @Override public void registerScreens(ScreenRegistry event) { event.registerContainerClickArea(new Rectangle(16,17,8,54),WoodBurnerScreen.class,TYPE); }
    static final class Display extends BasicDisplay {
        final WoodBurnerFuelViews.Fuel fuel;
        Display(WoodBurnerFuelViews.Fuel fuel) { super(List.of(EntryIngredient.of(EntryStacks.of(fuel.input()))),fuel.ash().isEmpty()?List.of():List.of(EntryIngredient.of(EntryStacks.of(fuel.ash())))); this.fuel=fuel; }
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return TYPE; }
    }
    static final class Category implements DisplayCategory<Display> {
        @Override public CategoryIdentifier<Display> getCategoryIdentifier() { return TYPE; }
        @Override public Component getTitle() { return Component.translatable("container.machineWoodBurner"); }
        @Override public Renderer getIcon() { return EntryStacks.of(WoodBurnerContent.ITEM.get()); }
        @Override public int getDisplayWidth(Display display) { return 166; }
        @Override public int getDisplayHeight() { return 78; }
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds) { var widgets=new ArrayList<Widget>(); widgets.add(Widgets.createDrawableWidget((graphics,x,y,partial)->WoodBurnerFuelViews.draw(graphics,display.fuel,bounds.x,bounds.y))); widgets.add(Widgets.createSlot(new Point(bounds.x+(display.fuel.liquid()?75:21),bounds.y+7)).entries(display.getInputEntries().getFirst()).disableBackground().markInput()); if(!display.fuel.ash().isEmpty()) { EntryStack<ItemStack> ash=EntryStacks.of(display.fuel.ash()); ash.tooltipProcessor((entry,tooltip)->{ display.fuel.ashInfo().forEach(tooltip::add); return tooltip; }); widgets.add(Widgets.createSlot(new Point(bounds.x+21,bounds.y+43)).entry(ash).disableBackground().markOutput()); } return widgets; }
    }
}
