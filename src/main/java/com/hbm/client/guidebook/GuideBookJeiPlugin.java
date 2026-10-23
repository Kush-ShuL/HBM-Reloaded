package com.hbm.client.guidebook;
import com.hbm.item.guidebook.*;import mezz.jei.api.*;import mezz.jei.api.constants.VanillaTypes;import mezz.jei.api.ingredients.subtypes.*;import mezz.jei.api.registration.*;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.item.ItemStack;
@JeiPlugin
public final class GuideBookJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:guidebook_jei");}
 @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(GuideBookContent.BOOK.get(),new ISubtypeInterpreter<ItemStack>(){public Object getSubtypeData(ItemStack stack,UidContext context){return GuideBookItem.metadata(stack);}public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return Integer.toString(GuideBookItem.metadata(stack));}});}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,GuideBookContent.BOOK.get().visibleVariants());}
}
