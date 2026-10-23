package com.hbm.client.generator.combustion;

import com.hbm.machine.generator.combustion.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class CombustionJei implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:combustion_engine");}
    @Override public void registerItemSubtypes(ISubtypeRegistration registration){registration.registerSubtypeInterpreter(CombustionContent.PISTONS.get(),new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return PistonSetItem.metadata(stack);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return getSubtypeData(stack,context).toString();}});}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration registration){registration.addExtraIngredients(VanillaTypes.ITEM_STACK,CombustionContent.pistonStacks());}
}
