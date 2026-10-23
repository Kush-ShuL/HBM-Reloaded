package com.hbm.client.reactor.fuelpool;

import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;
import com.hbm.machine.reactor.fuelpool.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

/** Original FuelPoolHandler universal entries from the server-synchronized actual recipe map. */
public record FuelPoolViews(net.minecraft.resources.ResourceLocation id,FuelPoolRecipe recipe) {
    static final LegacyRecipeDisplays.Category CATEGORY=new LegacyRecipeDisplays.Category("ntmSpentDrum","Spent Fuel Pool Drum","universal",FuelPoolContent.id("textures/gui/nei/gui_nei.png"),166,65,List.of(new ItemStack(FuelPoolContent.ITEM.get())));
    public List<ItemStack> inputs(){return Arrays.asList(recipe.input().getItems());}
    public ItemStack output(){return recipe.result().copy();}
    static List<FuelPoolViews> recipes(){var level=net.minecraft.client.Minecraft.getInstance().level;if(level==null)return List.of();return level.getRecipeManager().getAllRecipesFor(FuelPoolContent.TYPE.get()).stream().sorted(Comparator.comparingInt(holder->holder.value().order())).map(holder->new FuelPoolViews(holder.id(),holder.value())).toList();}
    LegacyRecipeDisplays.Display display(){return new LegacyRecipeDisplays.Display(id,"ntmSpentDrum",List.of(new LegacyRecipeDisplays.Slot(inputs())),List.of(new LegacyRecipeDisplays.Slot(List.of(output()))),null,null,List.of());}
    void draw(GuiGraphics graphics,int x,int y){LegacyDisplayDrawing.background(graphics,CATEGORY,display(),x,y);}
}
