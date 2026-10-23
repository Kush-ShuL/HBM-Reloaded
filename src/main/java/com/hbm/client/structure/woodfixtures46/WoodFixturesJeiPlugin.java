package com.hbm.client.structure.woodfixtures46;

import com.hbm.block.woodfixtures46.WoodFixturesContent;
import com.hbm.block.woodfixtures46.WoodStructureItem;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Three source metadata variants stay distinct in the native crafting category; no new recipe system. */
@JeiPlugin public final class WoodFixturesJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.parse("hbm:wood_fixtures46"); }
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event) { event.addExtraIngredients(VanillaTypes.ITEM_STACK, WoodFixturesContent.creativeStacks("block")); }
    @Override public void registerItemSubtypes(ISubtypeRegistration event) {
        event.registerSubtypeInterpreter(WoodFixturesContent.STRUCTURE_ITEM.get(), new ISubtypeInterpreter<ItemStack>() {
            @Override public Object getSubtypeData(ItemStack stack, UidContext context) { return WoodStructureItem.type(stack); }
            @Override public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) { return Integer.toString(WoodStructureItem.type(stack)); }
        });
    }
}
