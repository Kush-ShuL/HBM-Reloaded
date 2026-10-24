package com.hbm.client.reactor.pwr;

import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;
import com.hbm.machine.reactor.pwr.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

/** Original PWRRecipeHandler fifteen exact fresh->hot entries, no invented fixed duration. */
public record PwrFuelCycles(PwrFuelType type) {
    static final LegacyRecipeDisplays.Category CATEGORY=new LegacyRecipeDisplays.Category("pwr","PWR Controller","universal",PwrContent.id("textures/gui/nei/gui_nei.png"),166,65,List.of(new ItemStack(PwrContent.CONTROLLER_ITEM.get())));
    public ItemStack input(){return PwrContent.fuel(type);}
    public ItemStack output(){return PwrContent.spent(type.ordinal());}
    static List<PwrFuelCycles> recipes(){return Arrays.stream(PwrFuelType.values()).map(PwrFuelCycles::new).toList();}
    LegacyRecipeDisplays.Display display(){return new LegacyRecipeDisplays.Display(PwrContent.id("pwr/"+type.serializedName()),"pwr",List.of(new LegacyRecipeDisplays.Slot(List.of(input()))),List.of(new LegacyRecipeDisplays.Slot(List.of(output()))),null,null,List.of());}
    void draw(GuiGraphics graphics,int x,int y){LegacyDisplayDrawing.background(graphics,CATEGORY,display(),x,y);}
}
