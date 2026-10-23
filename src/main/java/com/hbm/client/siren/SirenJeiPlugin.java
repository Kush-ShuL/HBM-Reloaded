package com.hbm.client.siren;
import com.hbm.machine.siren.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Real20 metadata entries; manufacture uses existing native crafting/anvil categories. */
@JeiPlugin public final class SirenJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:siren106");}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(SirenContent.CASSETTE.get(),new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return CassetteItem.metadata(stack);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return Integer.toString(CassetteItem.metadata(stack));}});}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,SirenContent.cassetteStacks());}
}
