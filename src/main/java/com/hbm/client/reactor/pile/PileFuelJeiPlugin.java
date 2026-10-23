package com.hbm.client.reactor.pile;

import com.hbm.machine.reactor.pile.*;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Source NEI item-damage identity; depletion never creates a different recipe ingredient. */
@JeiPlugin
public final class PileFuelJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:pile_fuel_jei");}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(PileFuelContent.ITEM.get(),new ISubtypeInterpreter<ItemStack>(){
        @Override public Object getSubtypeData(ItemStack stack,UidContext context){return PileFuelItem.type(stack);}
        @Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return PileFuelItem.type(stack).serializedName();}
    });}
}
