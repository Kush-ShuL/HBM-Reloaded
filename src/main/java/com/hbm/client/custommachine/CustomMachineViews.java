package com.hbm.client.custommachine;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.custommachine.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
/** Source per-definition recipe sets retain full configured key/type and controller identity. */
public final class CustomMachineViews {
 static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei_custom.png");
 public record Display(CustomMachineRecipe recipe,CustomMachinesConfig.MachineConfiguration config,int index){ItemStack machine(){return CustomMachineContent.controller(100+index);}String sourceCategory(){return "ntm_"+config.unlocalizedName;}}
 static List<Display> all(RecipeManager manager){var result=new ArrayList<Display>();for(int i=0;i<CustomMachinesConfig.niceList.size();i++){var config=CustomMachinesConfig.niceList.get(i);for(var recipe:CustomMachineRecipes.recipes(manager,config.recipeKey))result.add(new Display(recipe,config,i));}return result;}
 static List<List<ItemStack>> inputs(Display d){var out=new ArrayList<List<ItemStack>>();for(int i=0;i<Math.min(3,d.recipe.inputFluids().size());i++)out.add(List.of(FluidIconItem.make(d.recipe.inputFluids().get(i))));for(int i=0;i<Math.min(6,d.recipe.inputItems().size());i++)out.add(d.recipe.inputItems().get(i).examples());return out;}
 static List<List<ItemStack>> outputs(Display d){var out=new ArrayList<List<ItemStack>>();for(int i=0;i<Math.min(3,d.recipe.outputFluids().size());i++)out.add(List.of(FluidIconItem.make(d.recipe.outputFluids().get(i))));for(int i=0;i<Math.min(6,d.recipe.outputItems().size());i++)out.add(d.recipe.outputItems().get(i).pool().stream().map(choice->choice.stack().copy()).toList());return out;}
 static int[][] coordinates(Display d,boolean output){int fluids=Math.min(3,output?d.recipe.outputFluids().size():d.recipe.inputFluids().size()),items=Math.min(6,output?d.recipe.outputItems().size():d.recipe.inputItems().size());int[][] out=new int[fluids+items][2];int x=output?102:12;for(int i=0;i<fluids;i++)out[i]=new int[]{x+18*i,6};for(int i=0;i<items;i++)out[fluids+i]=new int[]{x+18*(i%3),24+18*(i/3)};return out;}
 static List<Component> tooltip(Display d,int lane){int item=lane-Math.min(3,d.recipe.outputFluids().size());if(item<0||item>=d.recipe.outputItems().size())return List.of();float chance=d.recipe.outputItems().get(item).pool().getFirst().chance();return chance==1?List.of():List.of(Component.literal(((int)(chance*1000))/10D+"%").withStyle(ChatFormatting.RED));}
 static void draw(GuiGraphics g,Display d,int x,int y){g.blit(TEXTURE,x,y,0,0,166,87,256,256);var font=LegacyFonts.get();if(d.recipe.radiationAmount()!=0){var text=Component.translatable("gui.hbm.custom_machine.radiation",d.recipe.radiationAmount());g.drawString(font,text,x+160-font.width(text),y+63,0x08ff00,false);}if(d.recipe.pollutionAmount()!=0){var text=Component.literal(d.recipe.pollutionType()+":"+d.recipe.pollutionAmount());g.drawString(font,text,x+160-font.width(text),y+75,0x404040,false);}if(d.config.fluxMode){var text=Component.translatable("gui.hbm.custom_machine.flux",d.recipe.flux());g.drawString(font,text,x+83-font.width(text)/2,y+16,0x08ff00,false);}if(d.config.maxHeat>0&&d.recipe.heat()>0){var text=Component.translatable("gui.hbm.custom_machine.heat_required",d.recipe.heat());g.drawString(font,text,x+83-font.width(text)/2,y+8,0xff0000,false);}}
}
