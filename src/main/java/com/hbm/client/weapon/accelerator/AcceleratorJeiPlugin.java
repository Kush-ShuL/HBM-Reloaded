package com.hbm.client.weapon.accelerator;
import com.hbm.weapon.sedna.gun.accelerator.AcceleratorContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin public final class AcceleratorJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:accelerator_jei");}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,AcceleratorContent.indexedItems());}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){for(var stack:AcceleratorContent.indexedItems())event.registerSubtypeInterpreter(stack.getItem(),new ISubtypeInterpreter<ItemStack>(){
        @Override public Object getSubtypeData(ItemStack stack,UidContext context){return stack.getItem();}
        @Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();}
    });}
}
