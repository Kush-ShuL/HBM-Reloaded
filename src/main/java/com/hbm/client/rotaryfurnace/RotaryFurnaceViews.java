package com.hbm.client.rotaryfurnace;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;
import com.hbm.fluid.HbmFluids;
import com.hbm.item.autogen.ScrapsItem;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.rotaryfurnace.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
public final class RotaryFurnaceViews {
 static final LegacyRecipeDisplays.Category CATEGORY=new LegacyRecipeDisplays.Category("rotary_furnace","Rotary Furnace","universal",ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png"),166,65,List.of(new ItemStack(RotaryFurnaceContent.ITEM.get())));
 static List<List<ItemStack>> inputs(RotaryFurnaceRecipe recipe){var values=new ArrayList<List<ItemStack>>();recipe.inputItems().forEach(in->values.add(in.processingExamples()));recipe.inputFluids().forEach(fluid->values.add(List.of(FluidIconItem.make(fluid))));return values;}
 static List<List<ItemStack>> outputs(RotaryFurnaceRecipe recipe){return List.of(List.of(ScrapsItem.create(recipe.molten(),true)));}
 static LegacyRecipeDisplays.Display drawing(RotaryFurnaceRecipe recipe){return new LegacyRecipeDisplays.Display(ResourceLocation.parse("hbm:rotary_furnace"),"rotary_furnace",inputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),outputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),null,null,List.of());}
 static void draw(GuiGraphics g,RotaryFurnaceRecipe recipe,int x,int y){LegacyDisplayDrawing.background(g,CATEGORY,drawing(recipe),x,y);var font=LegacyFonts.get();String duration=String.format(Locale.US,"%,d",recipe.duration())+" ticks";Component steam=HbmFluids.STEAM.displayName().copy().append(": "+String.format(Locale.US,"%,d",recipe.steam())+" mB/t");g.drawString(font,duration,x+160-font.width(duration),y+43,0x404040,false);g.drawString(font,steam,x+160-font.width(steam),y+55,0x404040,false);}
}
