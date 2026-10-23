package com.hbm.client.purex;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.purex.*;
import com.hbm.recipe.generic.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
public final class PurexViews {
    static final LegacyRecipeDisplays.Category CATEGORY=new LegacyRecipeDisplays.Category("purex","PUREX Processing Plant","generic",ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei.png"),166,65,List.of(new ItemStack(PurexContent.ITEM.get())));
    static List<List<ItemStack>> inputs(PurexRecipe recipe){var values=new ArrayList<List<ItemStack>>();recipe.inputItems().forEach(in->values.add(in.examples()));recipe.inputFluids().forEach(in->values.add(List.of(FluidIconItem.make(in))));return values;}
    static List<List<ItemStack>> outputs(PurexRecipe recipe){var values=new ArrayList<List<ItemStack>>();recipe.outputItems().forEach(out->values.add(out.pool().stream().map(c->c.stack().copy()).toList()));recipe.outputFluids().forEach(out->values.add(List.of(FluidIconItem.make(out))));return values;}
    static LegacyRecipeDisplays.Display drawing(PurexRecipe recipe){return new LegacyRecipeDisplays.Display(ResourceLocation.fromNamespaceAndPath("hbm","purex"),"purex",inputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),outputs(recipe).stream().map(LegacyRecipeDisplays.Slot::new).toList(),recipe.duration(),recipe.power(),List.of());}
    static int offset(PurexRecipe recipe){return 0;}
    static List<ItemStack> blueprints(PurexRecipe recipe){return recipe.pools().stream().map(Blueprints::make).toList();}
    static List<Component> outputTooltip(PurexRecipe recipe,int lane,ItemStack stack){if(lane>=recipe.outputItems().size())return List.of();var output=recipe.outputItems().get(lane);var values=new ArrayList<Component>();for(var choice:output.pool())if(choice.stack().getItem()==stack.getItem()&&Blueprints.metadata(choice.stack())==Blueprints.metadata(stack)){if(choice.chance()<1)values.add(Component.translatable("gui.hbm.chemical.per_item",(int)(choice.chance()*1000)/10F).withStyle(net.minecraft.ChatFormatting.RED));if(output.weighted())values.add(Component.translatable("gui.hbm.chemical.selection",(int)(output.selectionProbability(choice)*1000)/10F).withStyle(net.minecraft.ChatFormatting.RED));}return values;}
    static void draw(GuiGraphics graphics,PurexRecipe recipe,int x,int y){var display=drawing(recipe);LegacyDisplayDrawing.background(graphics,CATEGORY,display,x,y);if(recipe.isPooled())graphics.blit(CATEGORY.texture(),x+74+offset(recipe),y+7,77,87,18,50,256,256);String line=shortNumber(recipe.duration())+" ticks   "+shortNumber(recipe.power())+"HE/t";var font=LegacyFonts.get();graphics.drawString(font,line,x+164-font.width(line),y+57,0x306030,false);}
    private static String shortNumber(long number){String[] units={"","k","M","G","T","P","E"};int index=0;double value=number;while(Math.abs(value)>=1000&&index<6){value/=1000;index++;}if(index==0)return Long.toString(number);value=value<=-100?Math.round(value*10)/10D:Math.round(value*100)/100D;return value+units[index];}
    static boolean visible(PurexRecipe recipe){return recipe.pools().stream().noneMatch(pool->pool.startsWith(Blueprints.SECRET));}
}
