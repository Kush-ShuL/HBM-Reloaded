package com.hbm.client.crt;
import com.hbm.block.crt.DecoCRTContent;
import com.hbm.block.crt.DecoCRTItem;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin public final class DecoCRTJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:deco_crt_jei");}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(DecoCRTContent.ITEM.get(),new ISubtypeInterpreter<ItemStack>(){
        @Override public Object getSubtypeData(ItemStack stack,UidContext context){return DecoCRTItem.variant(stack);}
        @Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return Integer.toString(DecoCRTItem.variant(stack));}
    });}
}
