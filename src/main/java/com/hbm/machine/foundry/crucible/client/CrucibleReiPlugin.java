package com.hbm.machine.foundry.crucible.client;
import com.hbm.machine.foundry.crucible.*;
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
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
@REIPluginClient
public final class CrucibleReiPlugin implements REIClientPlugin {
 static final CategoryIdentifier<Display> SMELTING=CategoryIdentifier.of("hbm","crucible_smelting"),ALLOYING=CategoryIdentifier.of("hbm","crucible_alloying");
 @Override public void registerCategories(CategoryRegistry event){event.add(new Category(false));event.add(new Category(true));event.addWorkstations(SMELTING,EntryStacks.of(CrucibleContent.CRUCIBLE.get()));event.addWorkstations(ALLOYING,EntryStacks.of(CrucibleContent.CRUCIBLE.get()));}
 @Override public void registerDisplays(DisplayRegistry event){var items=new ArrayList<ItemStack>();EntryRegistry.getInstance().getEntryStacks().filter(e->e.getType().equals(VanillaEntryTypes.ITEM)).forEach(e->items.add((ItemStack)e.getValue()));for(var recipe:CrucibleViews.smelting(items))event.add(new Display(recipe));for(var recipe:CrucibleViews.alloying())event.add(new Display(recipe));}
 static final class Display extends BasicDisplay{final CrucibleViews.View recipe;Display(CrucibleViews.View recipe){super(recipe.inputs().stream().map(variants->EntryIngredient.of(variants.stream().map(EntryStacks::of).toList())).toList(),recipe.outputs().stream().map(s->EntryIngredient.of(EntryStacks.of(s))).toList());this.recipe=recipe;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return recipe.alloy()?ALLOYING:SMELTING;}}
 static final class Category implements DisplayCategory<Display>{final boolean alloy;Category(boolean alloy){this.alloy=alloy;}@Override public CategoryIdentifier<Display> getCategoryIdentifier(){return alloy?ALLOYING:SMELTING;}@Override public Component getTitle(){return Component.translatable(alloy?"recipe.hbm.crucible.alloying":"recipe.hbm.crucible.smelting");}@Override public Renderer getIcon(){return EntryStacks.of(CrucibleContent.CRUCIBLE.get());}@Override public int getDisplayWidth(Display display){return 166;}@Override public int getDisplayHeight(){return 65;}@Override public List<Widget> setupDisplay(Display d,Rectangle bounds){var list=new ArrayList<Widget>();list.add(Widgets.createDrawableWidget((g,x,y,p)->CrucibleViews.draw(g,bounds.x,bounds.y,alloy)));for(int i=0;i<d.getInputEntries().size();i++)list.add(Widgets.createSlot(new Point(bounds.x+CrucibleViews.inputX(d.recipe,i),bounds.y+CrucibleViews.inputY(d.recipe,i))).entries(d.getInputEntries().get(i)).disableBackground().markInput());list.add(Widgets.createSlot(new Point(bounds.x+75,bounds.y+42)).entry(EntryStacks.of(CrucibleContent.CRUCIBLE.get())).disableBackground());for(int i=0;i<d.getOutputEntries().size();i++)list.add(Widgets.createSlot(new Point(bounds.x+102+i%3*18,bounds.y+6+i/3*18)).entries(d.getOutputEntries().get(i)).disableBackground().markOutput());return list;}}
}
