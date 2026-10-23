package com.hbm.client.soyuz;

import com.hbm.world.soyuz.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin public final class SoyuzCapsulesJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.parse("hbm:source_soyuz_capsules"); }
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event) { event.addExtraIngredients(VanillaTypes.ITEM_STACK, SoyuzCapsulesContent.soyuzSkins()); }
    @Override public void registerItemSubtypes(ISubtypeRegistration event) {
        event.registerSubtypeInterpreter(SoyuzCapsulesContent.SOYUZ.get(), new ISubtypeInterpreter<ItemStack>() {
            @Override public Object getSubtypeData(ItemStack stack, UidContext context) { return SoyuzItem.metadata(stack); }
            @Override public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) { return Integer.toString(SoyuzItem.metadata(stack)); }
        });
    }
}
