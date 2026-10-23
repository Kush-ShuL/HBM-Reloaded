package com.hbm.client.cyclotron;
import com.hbm.machine.cyclotron.*;
import java.util.*;
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
import me.shedaniel.rei.api.common.entry.*;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;
@REIPluginClient public final class CyclotronReiPlugin implements REIClientPlugin {
 static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","cyclotron");
 @Override public void registerCategories(CategoryRegistry e){e.add(new Category());e.addWorkstations(TYPE,EntryStacks.of(CyclotronContent.ITEM.get()));}@Override public void registerDisplays(DisplayRegistry e){for(var recipe:CyclotronRecipes.visibleRecipes(e.getRecipeManager()))e.add(new Display(recipe));}@Override public void registerScreens(ScreenRegistry e){e.registerContainerClickArea(new Rectangle(43,16,34,34),CyclotronScreen.class,TYPE);}@Override public void registerTransferHandlers(TransferHandlerRegistry e){e.register(SimpleTransferHandler.create(CyclotronMenu.class,TYPE,new SimpleTransferHandler.IntRange(0,6)));}
 static final class Display extends BasicDisplay{final CyclotronRecipe recipe;Display(CyclotronRecipe r){super(r.inputItems().stream().map(i->EntryIngredient.of(Arrays.stream(i.ingredient().getItems()).map(s->EntryStacks.of(s.copyWithCount(1))).toList())).toList(),List.of(EntryIngredient.of(EntryStacks.of(r.output()))));recipe=r;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}}
 static final class Category implements DisplayCategory<Display>{@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.cyclotron");}@Override public Renderer getIcon(){return EntryStacks.of(CyclotronContent.ITEM.get());}@Override public int getDisplayWidth(Display d){return 166;}@Override public int getDisplayHeight(){return 65;}@Override public List<Widget> setupDisplay(Display d,Rectangle bounds){var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((g,x,y,partial)->CyclotronViews.draw(g,bounds.x,bounds.y)));widgets.add(Widgets.createSlot(new Point(bounds.x+21,bounds.y+24)).entries(d.getInputEntries().get(0)).disableBackground().markInput());widgets.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+24)).entries(d.getInputEntries().get(1)).disableBackground().markInput());widgets.add(Widgets.createSlot(new Point(bounds.x+129,bounds.y+24)).entries(d.getOutputEntries().getFirst()).disableBackground().markOutput());return widgets;}}
}
