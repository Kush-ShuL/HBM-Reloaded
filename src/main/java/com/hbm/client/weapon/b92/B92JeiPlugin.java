package com.hbm.client.weapon.b92;
import com.hbm.hazard.HbmHazards;import com.hbm.weapon.b92.B92Content;import mezz.jei.api.*;import mezz.jei.api.ingredients.subtypes.*;import mezz.jei.api.registration.ISubtypeRegistration;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.item.ItemStack;
@JeiPlugin
public final class B92JeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return B92Content.id("legacy_b92");}
 @Override public void registerItemSubtypes(ISubtypeRegistration registration){registration.registerSubtypeInterpreter(B92Content.CELL.get(),new ISubtypeInterpreter<ItemStack>(){public Object getSubtypeData(ItemStack stack,UidContext context){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return getSubtypeData(stack,context).toString();}});}
}
