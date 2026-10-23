package com.hbm.client.radiationcleanup;

import com.hbm.machine.radiationcleanup.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class RadiationCleanupJei implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:radiation_cleanup");}
    @Override public void registerItemSubtypes(ISubtypeRegistration registration){registration.registerSubtypeInterpreter(RadiationCleanupContent.ABSORBER_ITEM.get(),new ISubtypeInterpreter<ItemStack>(){
        @Override public Object getSubtypeData(ItemStack stack,UidContext context){return AbsorberBlock.rectify(RadiationCleanupContent.metadata(stack));}
        @Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return getSubtypeData(stack,context).toString();}
    });}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration registration){registration.addExtraIngredients(VanillaTypes.ITEM_STACK,RadiationCleanupContent.absorberStacks());}
}
