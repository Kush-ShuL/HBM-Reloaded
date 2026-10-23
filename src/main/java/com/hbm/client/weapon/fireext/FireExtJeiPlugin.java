package com.hbm.client.weapon.fireext;
import com.hbm.weapon.sedna.gun.fireext.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin public final class FireExtJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:fire_extinguisher_jei");}
 @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(FireExtContent.AMMO.get(),new ISubtypeInterpreter<ItemStack>(){
  @Override public Object getSubtypeData(ItemStack stack,UidContext context){return FireExtContent.AMMO.get().ordinal(stack);}
  @Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return FireExtAmmoItem.NAMES.get(FireExtContent.AMMO.get().ordinal(stack));}
 });}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,FireExtContent.indexedItems());}
}
