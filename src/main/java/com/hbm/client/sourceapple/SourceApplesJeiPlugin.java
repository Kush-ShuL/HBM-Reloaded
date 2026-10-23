package com.hbm.client.sourceapple;

import com.hbm.item.sourceapple.SourceApplesContent;
import com.hbm.item.sourceapple.SourceMetalAppleItem;
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
public final class SourceApplesJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.parse("hbm:source_apples_jei"); }
    @Override public void registerItemSubtypes(ISubtypeRegistration registration) {
        var interpreter = new ISubtypeInterpreter<ItemStack>() {
            @Override public Object getSubtypeData(ItemStack stack, UidContext context) { return SourceMetalAppleItem.metadata(stack); }
            @Override public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) {
                return Integer.toString(SourceMetalAppleItem.metadata(stack));
            }
        };
        for (var item : SourceApplesContent.subtypedItems()) registration.registerSubtypeInterpreter(item, interpreter);
    }
    @Override public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        registration.addExtraIngredients(VanillaTypes.ITEM_STACK, SourceApplesContent.indexedItems());
    }
}
