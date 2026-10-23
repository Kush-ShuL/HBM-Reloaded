package com.hbm.client.structurebasic;
import com.hbm.block.structurebasic.BasicStructureContent;
import com.hbm.hazard.HbmHazards;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin public final class BasicStructureJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:structure_basic_blocks");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,BasicStructureContent.creativeStacks("block"));}
 @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(BasicStructureContent.VINYL_ITEM.get(),new ISubtypeInterpreter<ItemStack>(){
  @Override public Object getSubtypeData(ItemStack stack,UidContext context){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);}
  @Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return Integer.toString(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0));}
 });}
}
