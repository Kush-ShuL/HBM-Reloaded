package com.hbm.client.bedrockprocessing;

import com.hbm.item.bedrockprocessing.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin public final class BedrockProcessingJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return BedrockProcessingContent.id("bedrock_processing_jei");}
    public static int subtype(ItemStack stack){return stack.getItem() instanceof LegacyBedrockItem item?item.ordinal(stack):BedrockProcessingData.canonical(BedrockProcessingData.metadata(stack));}
    @Override public void registerItemSubtypes(ISubtypeRegistration registration){var interpreter=new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return subtype(stack);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return Integer.toString(subtype(stack));}};for(var item:BedrockProcessingContent.allItems())registration.registerSubtypeInterpreter(item,interpreter);}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration registration){registration.addExtraIngredients(VanillaTypes.ITEM_STACK,BedrockProcessingContent.indexedItems());}
}
