package com.hbm.client.weapon.rifle762;
import com.hbm.weapon.sedna.gun.rifle762.Rifle762Content;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Exposes all five real source 7.62 gun identities. */
@JeiPlugin public final class Rifle762JeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:rifle762_jei");}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,Rifle762Content.indexedItems());}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){for(var stack:Rifle762Content.allStacks())event.registerSubtypeInterpreter(stack.getItem(),new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return stack.getItem();}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();}});}
}
