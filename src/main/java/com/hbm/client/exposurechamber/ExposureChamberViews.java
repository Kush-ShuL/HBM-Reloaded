package com.hbm.client.exposurechamber;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;
import com.hbm.machine.exposurechamber.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Source NEI shows eight ingredients + one capsule -> eight output units, including its custom-output-count quirk. */
public final class ExposureChamberViews {
 static final LegacyRecipeDisplays.Category CATEGORY=new LegacyRecipeDisplays.Category("exposure_chamber","Exposure Chamber","universal",ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png"),166,65,List.of(new ItemStack(ExposureChamberContent.ITEM.get())));
 static List<List<ItemStack>> inputs(ExposureChamberRecipe recipe){return List.of(recipe.inputItems().get(1).examples().stream().map(s->s.copyWithCount(8)).toList(),recipe.inputItems().get(0).examples().stream().map(ItemStack::copy).toList());}
 static List<List<ItemStack>> outputs(ExposureChamberRecipe recipe){return List.of(List.of(recipe.outputItems().getFirst().pool().getFirst().stack().copyWithCount(8)));}
 static LegacyRecipeDisplays.Display drawing(ExposureChamberRecipe recipe){return new LegacyRecipeDisplays.Display(ResourceLocation.parse("hbm:exposure_chamber"),"exposure_chamber",inputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),outputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),null,null,List.of());}
 static void draw(GuiGraphics graphics,ExposureChamberRecipe recipe,int x,int y){LegacyDisplayDrawing.background(graphics,CATEGORY,drawing(recipe),x,y);}
}
