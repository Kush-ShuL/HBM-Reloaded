package com.hbm.client.dye;
import com.hbm.item.dye.*;import mezz.jei.api.*;import mezz.jei.api.ingredients.subtypes.*;import mezz.jei.api.registration.ISubtypeRegistration;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.item.ItemStack;
@JeiPlugin public final class ChemicalDyeJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ChemicalDyeContent.id("chemical_dyes_jei");}
 @Override public void registerItemSubtypes(ISubtypeRegistration event){var interpreter=new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return ChemicalDyeItem.type(stack);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return ChemicalDyeItem.type(stack).serializedName();}};event.registerSubtypeInterpreter(ChemicalDyeContent.DYE.get(),interpreter);event.registerSubtypeInterpreter(ChemicalDyeContent.CRAYON.get(),interpreter);}
}
