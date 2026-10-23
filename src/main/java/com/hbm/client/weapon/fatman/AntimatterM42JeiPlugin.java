package com.hbm.client.weapon.fatman;
import com.hbm.weapon.sedna.gun.fatman.AntimatterM42Content;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Exposes the real source cluster and launcher, retaining native workbench and ammunition press recipes. */
@JeiPlugin public final class AntimatterM42JeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:fatman_jei");}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,AntimatterM42Content.indexedItems());}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){for(var stack:java.util.List.of(new ItemStack(AntimatterM42Content.FATMAN.get())))event.registerSubtypeInterpreter(stack.getItem(),new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return stack.getItem();}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();}});}
}
