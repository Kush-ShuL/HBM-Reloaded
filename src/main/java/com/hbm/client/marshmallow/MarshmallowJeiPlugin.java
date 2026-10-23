package com.hbm.client.marshmallow;

import com.hbm.item.marshmallow.ItemMarshmallow;
import com.hbm.item.marshmallow.MarshmallowContent;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class MarshmallowJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.parse("hbm:marshmallow_jei"); }
    @Override public void registerItemSubtypes(ISubtypeRegistration event) {
        event.registerSubtypeInterpreter(MarshmallowContent.MARSHMALLOW.get(), new ISubtypeInterpreter<ItemStack>() {
            @Override public Object getSubtypeData(ItemStack stack, UidContext context) { return ItemMarshmallow.metadata(stack); }
            @Override public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) { return Integer.toString(ItemMarshmallow.metadata(stack)); }
        });
    }
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event) { event.addExtraIngredients(VanillaTypes.ITEM_STACK, MarshmallowContent.indexedItems()); }
}
