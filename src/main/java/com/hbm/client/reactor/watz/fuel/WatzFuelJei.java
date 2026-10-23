package com.hbm.client.reactor.watz.fuel;

import com.hbm.machine.reactor.watz.fuel.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class WatzFuelJei implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:watz_fuel");}
    @Override public void registerItemSubtypes(ISubtypeRegistration registration){for(var item:new net.minecraft.world.item.Item[]{WatzFuelContent.FRESH.get(),WatzFuelContent.DEPLETED.get()})registration.registerSubtypeInterpreter(item,new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return WatzPelletItem.metadata(stack);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return getSubtypeData(stack,context).toString();}});}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration registration){registration.addExtraIngredients(VanillaTypes.ITEM_STACK,WatzFuelContent.stacks());}
}
