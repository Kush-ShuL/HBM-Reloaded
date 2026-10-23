package com.hbm.client.massstorage;

import com.hbm.machine.massstorage.*;
import mezz.jei.api.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Four genuine tiers remain distinct in JEI; source MSU has no separate processing/NEI category. */
@JeiPlugin
public final class MassStorageJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:mass_storage_jei");}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(MassStorageContent.ITEM.get(),new mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter<ItemStack>(){
        @Override public Object getSubtypeData(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){return MassStorageItem.tier(stack);}
        @Override public String getLegacyStringSubtypeInfo(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){return getSubtypeData(stack,context).toString();}
    });}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(mezz.jei.api.constants.VanillaTypes.ITEM_STACK,MassStorageContent.creativeStacks("machine"));}
}
