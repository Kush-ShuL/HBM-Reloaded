package com.hbm.client.weapon.aberrator;
import com.hbm.weapon.sedna.gun.aberrator.AberratorContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Exposes both real source .35-800 gun identities. */
@JeiPlugin public final class AberratorJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:aberrator_jei");}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,AberratorContent.indexedItems());}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){for(var stack:AberratorContent.allStacks())event.registerSubtypeInterpreter(stack.getItem(),new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return stack.getItem();}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();}});}
}
