package com.hbm.client.reactor.pile.core;
import com.hbm.machine.reactor.pile.core.*;import mezz.jei.api.*;import mezz.jei.api.ingredients.subtypes.*;import mezz.jei.api.registration.ISubtypeRegistration;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.item.ItemStack;
@JeiPlugin public final class PileDeviceJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return PileCoreContent.id("pile_device_jei");}
 @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(PileCoreContent.DEVICE_ITEM.get(),new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return Math.abs(PileDeviceItem.metadata(stack)%3);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return PileDeviceItem.kind(stack);}});}
}
