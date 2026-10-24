package com.hbm.client.autogen;

import com.hbm.item.autogen.AutogenContent;
import com.hbm.item.autogen.ScrapsItem;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

@JeiPlugin
public final class AutogenJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return ResourceLocation.fromNamespaceAndPath("hbm","autogen_jei"); }
    @Override public void registerExtraIngredients(IExtraIngredientRegistration registration) { registration.addExtraIngredients(VanillaTypes.ITEM_STACK,AutogenContent.creativeStacks()); }
    @Override public void onRuntimeAvailable(mezz.jei.api.runtime.IJeiRuntime runtime) { runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK,AutogenContent.viewerHiddenStacks()); }
    @Override public void registerItemSubtypes(ISubtypeRegistration registration) { for(var holder:AutogenContent.all()) if(holder.get() instanceof ScrapsItem) registration.registerSubtypeInterpreter(holder.get(),new ISubtypeInterpreter<ItemStack>() {
        @Override public Object getSubtypeData(ItemStack stack,UidContext context) { return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().toString(); }
        @Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context) { return getSubtypeData(stack,context).toString(); }
    }); }
}
