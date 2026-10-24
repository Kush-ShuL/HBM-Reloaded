package com.hbm.client.precisionassembler;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.precisionassembler.*;
import com.hbm.recipe.generic.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
public final class PrecisionAssemblerViews {
    static final LegacyRecipeDisplays.Category CATEGORY=new LegacyRecipeDisplays.Category("precision_assembly","Precision Assembly Machine","generic",ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei.png"),166,65,List.of(new ItemStack(PrecisionAssemblerContent.ITEM.get())));
    static List<List<ItemStack>> inputs(PrecisionAssemblerRecipe recipe){var values=new ArrayList<List<ItemStack>>();recipe.inputItems().forEach(in->values.add(in.examples()));recipe.inputFluids().forEach(in->values.add(List.of(FluidIconItem.make(in))));return values;}
    static List<List<ItemStack>> outputs(PrecisionAssemblerRecipe recipe){var values=new ArrayList<List<ItemStack>>();recipe.outputItems().forEach(out->values.add(out.pool().stream().map(c->c.stack().copy()).toList()));recipe.outputFluids().forEach(out->values.add(List.of(FluidIconItem.make(out))));return values;}
    static LegacyRecipeDisplays.Display drawing(PrecisionAssemblerRecipe recipe){return new LegacyRecipeDisplays.Display(ResourceLocation.fromNamespaceAndPath("hbm","precision_assembly"),"precision_assembly",inputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),outputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),recipe.duration(),recipe.power(),List.of());}
    static int offset(PrecisionAssemblerRecipe recipe){return recipe.inputItems().size()+recipe.inputFluids().size()>12?27:recipe.inputItems().size()+recipe.inputFluids().size()>9?18:0;}
    static List<ItemStack> blueprints(PrecisionAssemblerRecipe recipe){return recipe.pools().stream().map(Blueprints::make).toList();}
    static List<Component> outputTooltip(PrecisionAssemblerRecipe recipe,int lane,ItemStack stack){if(lane>=recipe.outputItems().size())return List.of();var output=recipe.outputItems().get(lane);var values=new ArrayList<Component>();for(var choice:output.pool())if(ItemStack.isSameItemSameComponents(choice.stack(),stack)){if(choice.chance()<1)values.add(Component.translatable("gui.hbm.precass.per_item",((int)(choice.chance()*1000)/10F)).withStyle(net.minecraft.ChatFormatting.RED));if(output.weighted())values.add(Component.translatable("gui.hbm.precass.selection",((int)(output.selectionProbability(choice)*1000)/10F)).withStyle(net.minecraft.ChatFormatting.RED));}return values;}
    static void draw(GuiGraphics graphics,PrecisionAssemblerRecipe recipe,int x,int y){var display=drawing(recipe);LegacyDisplayDrawing.background(graphics,CATEGORY,display,x,y);if(recipe.isPooled())graphics.blit(CATEGORY.texture(),x+74+offset(recipe),y+7,77,87,18,50,256,256);var font=LegacyFonts.get();var duration=Component.translatable("gui.hbm.precass.duration",shortNumber(recipe.duration()));var power=Component.translatable("gui.hbm.precass.power",shortNumber(recipe.power()));graphics.drawString(font,duration,x+164-font.width(duration),y+45,0x404040,false);graphics.drawString(font,power,x+164-font.width(power),y+57,0x404040,false);}
    private static String shortNumber(long value){String[] units={"","k","M","G","T","P","E"};int unit=0;double n=value;while(Math.abs(n)>=1000&&unit<6){n/=1000;unit++;}if(unit==0)return Long.toString(value);n=n<=-100?Math.round(n*10)/10D:Math.round(n*100)/100D;return n+units[unit];}
    static boolean visible(PrecisionAssemblerRecipe recipe){return com.hbm.compat.recipeviewer.RecipeViewerVisibility.visible(recipe.pools(),inputs(recipe),outputs(recipe));}
}
