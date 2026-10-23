package com.hbm.client.arcfurnace;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;
import com.hbm.item.autogen.ScrapsItem;
import com.hbm.machine.arcfurnace.*;
import com.hbm.material.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
/** Original NEIUniversal solid and molten categories use the actual synchronized executable recipe list. */
final class ArcFurnaceViews {
 record View(boolean liquid,List<ItemStack> inputs,List<ItemStack> outputs){}
 static final LegacyRecipeDisplays.Category CATEGORY=new LegacyRecipeDisplays.Category("arc_furnace","Arc Furnace","universal",ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png"),166,65,List.of(new ItemStack(ArcFurnaceContent.ITEM.get())));
 static List<View> recipes(RecipeManager manager,boolean liquid){var views=new ArrayList<View>();for(var recipe:ArcFurnaceRecipes.all(manager))if(recipe.inMode(liquid)&&recipe.input().getItems().length>0)views.add(new View(liquid,com.hbm.ore.OreRecipeInputs.examples(recipe.input(),recipe.inputCount()),liquid?recipe.molten().stream().map(mat->ScrapsItem.create(mat,true)).toList():List.of(recipe.solid().copy())));if(liquid)for(var material:HbmMaterials.ordered())if(material.smeltingBehavior()==NtmMaterial.SmeltingBehavior.SMELTABLE)views.add(new View(true,List.of(ScrapsItem.create(new MaterialStack(material,HbmMaterials.shape("INGOT").q(1)))),List.of(ScrapsItem.create(new MaterialStack(material,HbmMaterials.shape("INGOT").q(1)),true))));return views;}
 static LegacyRecipeDisplays.Display drawing(View view){return new LegacyRecipeDisplays.Display(ResourceLocation.parse("hbm:arc_furnace"),"arc_furnace",List.of(new LegacyRecipeDisplays.Slot(view.inputs)),view.outputs.stream().map(stack->new LegacyRecipeDisplays.Slot(List.of(stack))).toList(),null,null,List.of());}
 static int[][] outputCoords(View view){return LegacyRecipeDisplays.outputCoordinates(CATEGORY,drawing(view));}
 static void draw(GuiGraphics graphics,View view,int x,int y){var display=drawing(view);LegacyDisplayDrawing.background(graphics,CATEGORY,display,x,y);LegacyDisplayDrawing.extras(graphics,CATEGORY,display,x,y);}
 private ArcFurnaceViews(){}
}
