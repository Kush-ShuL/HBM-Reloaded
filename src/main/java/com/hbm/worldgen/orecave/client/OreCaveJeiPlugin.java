package com.hbm.worldgen.orecave.client;
import com.hbm.worldgen.orecave.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin public final class OreCaveJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:ore_caves_jei");}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,OreCaves.indexedItems());}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){for(var holder:java.util.List.of(OreCaves.STALAGMITE_ITEM,OreCaves.STALACTITE_ITEM))event.registerSubtypeInterpreter(holder.get(),new ISubtypeInterpreter<ItemStack>(){
        @Override public Object getSubtypeData(ItemStack stack,UidContext context){return holder.get().ordinal(stack);}
        @Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return CaveFormationBlock.VARIANTS.get(holder.get().ordinal(stack));}
    });}
}
