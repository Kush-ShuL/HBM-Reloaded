package com.hbm.client.sellafield;
import com.hbm.block.sellafield.SellafieldContent;
import com.hbm.hazard.HbmHazards;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin
public final class ActiveSellafieldJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","active_sellafield_jei");}@Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,SellafieldContent.creativeStacks("block"));}@Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(SellafieldContent.ITEM.get(),new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return getSubtypeData(stack,context).toString();}});}
}
