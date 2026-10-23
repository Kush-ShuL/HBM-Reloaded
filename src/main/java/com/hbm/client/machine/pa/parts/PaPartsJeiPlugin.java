package com.hbm.client.machine.pa.parts;
import com.hbm.machine.pa.parts.PaPartsContent;
import mezz.jei.api.*;import mezz.jei.api.constants.VanillaTypes;import mezz.jei.api.ingredients.subtypes.*;import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin public final class PaPartsJeiPlugin implements IModPlugin{
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:pa_parts_jei");}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,PaPartsContent.COIL.get().creativeStacks());}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(PaPartsContent.COIL.get(),new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return PaPartsContent.COIL.get().ordinal(stack);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return getSubtypeData(stack,context).toString();}});}
}
