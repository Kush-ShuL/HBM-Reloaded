package com.hbm.client.weapon.pepperbox;

import com.hbm.weapon.sedna.gun.PepperboxContent;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Original NEI exposes the unmodified gun. Runtime mag/wear/timers do not split the same gun's crafting identity. */
@JeiPlugin
public final class PepperboxJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:pepperbox_jei");}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(PepperboxContent.PEPPERBOX.get(),new ISubtypeInterpreter<ItemStack>(){
        @Override public Object getSubtypeData(ItemStack stack,UidContext context){return "pepperbox";}
        @Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return "pepperbox";}
    });}
}
