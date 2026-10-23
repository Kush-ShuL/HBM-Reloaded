package com.hbm.client.weapon.crucible;
import com.hbm.weapon.crucible.CrucibleContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin public final class CrucibleJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:crucible_weapon_jei");}
 @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(CrucibleContent.CRUCIBLE.get(),new ISubtypeInterpreter<ItemStack>(){
  @Override public Object getSubtypeData(ItemStack stack,UidContext context){return stack.getDamageValue();}
  @Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return Integer.toString(stack.getDamageValue());}
 });}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,CrucibleContent.viewerStacks());}
}
