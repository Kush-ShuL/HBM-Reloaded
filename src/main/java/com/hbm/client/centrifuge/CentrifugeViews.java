package com.hbm.client.centrifuge;

import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.machine.centrifuge.*;
import java.util.Arrays;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

final class CentrifugeViews {
    static final LegacyRecipeDisplays.Category CATEGORY=new LegacyRecipeDisplays.Category("centrifuge","Centrifuge","universal",ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei.png"),166,65,List.of(new ItemStack(CentrifugeContent.ITEM.get())));
    static LegacyRecipeDisplays.Display drawing(CentrifugeRecipe recipe) { return new LegacyRecipeDisplays.Display(ResourceLocation.fromNamespaceAndPath("hbm","centrifuge"),"centrifuge",List.of(new LegacyRecipeDisplays.Slot(Arrays.asList(recipe.input().getItems()))),recipe.outputs().stream().map(stack->new LegacyRecipeDisplays.Slot(stack.isEmpty()?List.of():List.of(stack))).toList(),null,null,List.of()); }
    private CentrifugeViews() {}
}
