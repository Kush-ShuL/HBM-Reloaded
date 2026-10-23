package com.hbm.client.disperser;
import com.hbm.item.disperser.DisperserContent;
import com.hbm.item.fluid.FluidItemContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin public final class DisperserJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return DisperserContent.id("disperser219");}
    @Override public void registerItemSubtypes(ISubtypeRegistration registration){for(var item:java.util.List.of(DisperserContent.CANISTER.get(),DisperserContent.GLAND.get()))registration.registerSubtypeInterpreter(item,new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return FluidItemContent.type(stack).getID();}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return getSubtypeData(stack,context).toString();}});}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration registration){registration.addExtraIngredients(VanillaTypes.ITEM_STACK,DisperserContent.variants());}
}
