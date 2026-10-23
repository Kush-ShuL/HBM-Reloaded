package com.hbm.client.meteordeco;
import com.hbm.block.meteordeco.MeteorDecorationContent;
import com.hbm.hazard.HbmHazards;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin
public final class MeteorDecorationJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","meteor_decorations_jei");}@Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,MeteorDecorationContent.creativeStacks("machine"));}@Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(MeteorDecorationContent.FENCE_ITEM.get(),new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return getSubtypeData(stack,context).toString();}});}
}
