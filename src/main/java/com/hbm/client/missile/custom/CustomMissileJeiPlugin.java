package com.hbm.client.missile.custom;

import com.hbm.missile.custom.CustomMissileContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/** Real component items plus NBT-aware custom missile identity; source has no dedicated assembly NEI handler. */
@JeiPlugin
public final class CustomMissileJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.parse("hbm:custom_missile_parts"); }
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event) { event.addExtraIngredients(VanillaTypes.ITEM_STACK, CustomMissileContent.creativeStacks("missile")); }
    @Override public void registerItemSubtypes(ISubtypeRegistration event) {
        event.registerSubtypeInterpreter(CustomMissileContent.MISSILE.get(), new ISubtypeInterpreter<ItemStack>() {
            @Override public Object getSubtypeData(ItemStack stack, UidContext context) { return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag(); }
            @Override public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) { return getSubtypeData(stack, context).toString(); }
        });
    }
}
