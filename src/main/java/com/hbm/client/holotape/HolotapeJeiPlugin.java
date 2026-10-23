package com.hbm.client.holotape;
import com.hbm.item.holotape.*;import mezz.jei.api.*;import mezz.jei.api.constants.VanillaTypes;import mezz.jei.api.ingredients.subtypes.*;import mezz.jei.api.registration.*;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.item.ItemStack;
@JeiPlugin
public final class HolotapeJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:holotape_jei");}
 @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(HolotapeContent.IMAGE.get(),new ISubtypeInterpreter<ItemStack>(){public Object getSubtypeData(ItemStack stack,UidContext context){return HolotapeImageItem.metadata(stack);}public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return Integer.toString(HolotapeImageItem.metadata(stack));}});}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,HolotapeContent.IMAGE.get().variants());}
}
