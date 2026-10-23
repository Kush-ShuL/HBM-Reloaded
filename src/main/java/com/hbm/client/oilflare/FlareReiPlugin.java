package com.hbm.client.oilflare;
import com.hbm.machine.oilflare.FlareContent;
import java.util.*;
import me.shedaniel.math.*;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.*;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;
/** Same actual trait-backed operation table as JEI, without consuming the machine catalyst. */
@REIPluginClient public final class FlareReiPlugin implements REIClientPlugin {
    static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","gas_flare");
    @Override public void registerCategories(CategoryRegistry e){e.add(new Category());e.addWorkstations(TYPE,EntryStacks.of(FlareContent.ITEM.get()));}
    @Override public void registerDisplays(DisplayRegistry e){for(var operation:FlareViews.operations())e.add(new Display(operation));}
    private static final class Display extends BasicDisplay{final FlareViews.Operation operation;Display(FlareViews.Operation operation){super(List.of(EntryIngredient.of(EntryStacks.of(operation.icon()))),List.of());this.operation=operation;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
    private static final class Category implements DisplayCategory<Display>{
        @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}
        @Override public Component getTitle(){return Component.translatable("container.gasFlare");}
        @Override public Renderer getIcon(){return EntryStacks.of(FlareContent.ITEM.get());}
        @Override public int getDisplayWidth(Display display){return 166;}
        @Override public int getDisplayHeight(){return 112;}
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createRecipeBase(bounds));widgets.add(Widgets.createSlot(new Point(bounds.x+8,bounds.y+8)).entries(display.getInputEntries().getFirst()).markInput());widgets.add(Widgets.createSlot(new Point(bounds.x+36,bounds.y+8)).entry(EntryStacks.of(FlareContent.ITEM.get())));Component[] lines={FlareViews.mode(display.operation),FlareViews.rate(display.operation),FlareViews.output(display.operation),FlareViews.efficiency(display.operation),Component.translatable("gui.hbm.flare.upgrades")};for(int i=0;i<lines.length;i++)widgets.add(Widgets.createLabel(new Point(bounds.x+83,bounds.y+36+i*11),lines[i]).color(0x404040).noShadow());return widgets;}
    }
}
