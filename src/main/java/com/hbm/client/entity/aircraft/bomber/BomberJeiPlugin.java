package com.hbm.client.entity.aircraft.bomber;
import com.hbm.entity.aircraft.bomber.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin public final class BomberJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:source_airstrike");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,BomberContent.sourceVisible());}
 @Override public void registerItemSubtypes(ISubtypeRegistration event){var subtype=new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return ItemBombCaller.metadata(stack);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return Integer.toString(ItemBombCaller.metadata(stack));}};event.registerSubtypeInterpreter(BomberContent.CALLER.get(),subtype);event.registerSubtypeInterpreter(BomberContent.RANGEFINDER.get(),subtype);}
}
