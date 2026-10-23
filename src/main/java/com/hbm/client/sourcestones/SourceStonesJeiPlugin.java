package com.hbm.client.sourcestones;

import com.hbm.block.sourcestones.SourceStonesContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin public final class SourceStonesJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:source_stones249");}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,SourceStonesContent.biomeStacks());}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(SourceStonesContent.BIOME_ITEM.get(),new ISubtypeInterpreter<ItemStack>(){
        @Override public Object getSubtypeData(ItemStack stack,UidContext context){return SourceStonesContent.BIOME_ITEM.get().ordinal(stack);}
        @Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return SourceStonesContent.BIOME.get().spec().variants().get(SourceStonesContent.BIOME_ITEM.get().ordinal(stack));}
    });}
}
