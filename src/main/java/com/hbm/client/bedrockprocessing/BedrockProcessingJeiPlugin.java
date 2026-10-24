package com.hbm.client.bedrockprocessing;

import com.hbm.item.bedrockprocessing.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin public final class BedrockProcessingJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return BedrockProcessingContent.id("bedrock_processing_jei");}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration registration){registration.addExtraIngredients(VanillaTypes.ITEM_STACK,BedrockProcessingContent.indexedItems());}
}
