package com.hbm.client.electrolyser;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;
import com.hbm.item.autogen.ScrapsItem;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.electrolyser.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
/** Both original NEIUniversal input/output layouts read the synchronized executable native recipes. */
final class ElectrolyserViews {
 record View(ElectrolyserRecipe recipe,List<List<ItemStack>> inputs,List<ItemStack> outputs){}
 static final LegacyRecipeDisplays.Category CATEGORY=new LegacyRecipeDisplays.Category("electrolyser","Electrolyser","universal",ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png"),166,65,List.of(new ItemStack(ElectrolyserContent.ITEM.get())));
 static List<View> recipes(RecipeManager manager,boolean metal){var views=new ArrayList<View>();for(var r:ElectrolyserRecipes.all(manager))if(r.metal()==metal){var inputs=new ArrayList<List<ItemStack>>();if(metal)inputs.add(r.inputItems().getFirst().processingExamples());inputs.add(List.of(FluidIconItem.make(r.inputFluids().getFirst())));var outputs=new ArrayList<ItemStack>();if(metal){if(r.left()!=null)outputs.add(ScrapsItem.create(r.left(),true));if(r.right()!=null)outputs.add(ScrapsItem.create(r.right(),true));}else for(var f:r.outputFluids())if(f.type!=com.hbm.fluid.HbmFluids.NONE)outputs.add(FluidIconItem.make(f));for(var product:r.outputItems())outputs.add(product.single().copy());views.add(new View(r,inputs,outputs));}return views;}
 static LegacyRecipeDisplays.Display drawing(View v){return new LegacyRecipeDisplays.Display(ResourceLocation.parse("hbm:electrolyser"),"electrolyser",v.inputs.stream().map(LegacyRecipeDisplays.Slot::new).toList(),v.outputs.stream().map(s->new LegacyRecipeDisplays.Slot(List.of(s))).toList(),null,null,List.of());}
 static int[][] inputCoords(View v){return LegacyRecipeDisplays.inputCoordinates(CATEGORY,v.inputs.size());}static int[][] outputCoords(View v){return LegacyRecipeDisplays.outputCoordinates(CATEGORY,drawing(v));}
 static void draw(GuiGraphics g,View v,int x,int y){var display=drawing(v);LegacyDisplayDrawing.background(g,CATEGORY,display,x,y);LegacyDisplayDrawing.extras(g,CATEGORY,display,x,y);}
 private ElectrolyserViews(){}
}
