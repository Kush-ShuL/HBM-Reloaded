package com.hbm.client.pedestal;
import com.hbm.machine.pedestal.*;
import java.util.*;
import me.shedaniel.math.*;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.*;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
@REIPluginClient
public final class PedestalReiPlugin implements REIClientPlugin {
    private static final CategoryIdentifier<Display> TYPE=CategoryIdentifier.of("hbm","pedestal");
    @Override public void registerCategories(CategoryRegistry event){if(!PedestalViewerConfig.showRecipesInViewers())return;event.add(new Category());event.addWorkstations(TYPE,EntryStacks.of(PedestalContent.ITEM.get()),EntryStacks.of(PedestalContent.TABLET.get()));}
    @Override public void registerDisplays(DisplayRegistry event){if(!PedestalViewerConfig.showRecipesInViewers())return;event.registerRecipeFiller(PedestalRecipe.class,PedestalContent.TYPE.get(),Display::new);}
    @Override public void registerEntries(EntryRegistry event){PedestalContent.indexedItems().forEach(stack->event.addEntry(EntryStacks.of(stack)));}
    public static final class Display extends BasicDisplay {
        final PedestalRecipe recipe;
        Display(RecipeHolder<PedestalRecipe> holder){super(holder.value().inputs().stream().map(slot->EntryIngredient.of(slot.examples().stream().map(EntryStacks::of).toList())).toList(),List.of(EntryIngredient.of(EntryStacks.of(holder.value().result()))),Optional.of(holder.id()));recipe=holder.value();}
        @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}
    }
    private static final class Category implements DisplayCategory<Display>{
        @Override public CategoryIdentifier<Display> getCategoryIdentifier(){return TYPE;}
        @Override public Component getTitle(){return Component.translatable("block.hbm.pedestal");}
        @Override public Renderer getIcon(){return EntryStacks.of(PedestalContent.ITEM.get());}
        @Override public int getDisplayHeight(){return 114;}
        @Override public int getDisplayWidth(Display display){return 142;}
        @Override public List<Widget> setupDisplay(Display display,Rectangle bounds){
            var widgets=new ArrayList<Widget>();widgets.add(Widgets.createDrawableWidget((graphics,x,y,partial)->{
                graphics.blit(ClayTabletScreen.TEXTURE,bounds.x,bounds.y+30,0,0,142,84,256,256);
                for(int i=0;i<9;i++)if(display.recipe.inputs().get(i).count()==0)graphics.blit(ClayTabletScreen.TEXTURE,bounds.x+7+i%3*27,bounds.y+37+i/3*27,142,0,16,16,256,256);
                int condition=switch(display.recipe.extra()){case FULL_MOON->32;case NEW_MOON->48;case SUN->64;default->-1;};if(condition>=0)graphics.blit(ClayTabletScreen.TEXTURE,bounds.x+120,bounds.y+92,142,condition,16,16,256,256);
                if(display.recipe.extra()!=PedestalRecipe.Extra.NONE&&bounds.contains(x,y))graphics.renderTooltip(Minecraft.getInstance().font,Component.translatable("gui.hbm.pedestal.condition."+display.recipe.extra().name().toLowerCase(Locale.ROOT)),x,y);
            }));
            for(int i=0;i<9;i++)if(display.recipe.inputs().get(i).count()>0)widgets.add(Widgets.createSlot(new Point(bounds.x+7+i%3*27,bounds.y+37+i/3*27)).entries(display.getInputEntries().get(i)).disableBackground().markInput());
            widgets.add(Widgets.createSlot(new Point(bounds.x+63,bounds.y+10)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());return widgets;
        }
    }
}
