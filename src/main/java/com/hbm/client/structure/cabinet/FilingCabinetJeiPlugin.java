package com.hbm.client.structure.cabinet;
import com.hbm.world.structure.cabinet.*;import mezz.jei.api.*;import mezz.jei.api.constants.VanillaTypes;import mezz.jei.api.ingredients.subtypes.*;import mezz.jei.api.registration.*;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.item.ItemStack;
/** The two actual original cabinet variants are distinct JEI entries, without fabricating a recipe category. */
@JeiPlugin public final class FilingCabinetJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:filing_cabinet_jei");}
 @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(FilingCabinetContent.ITEM.get(),new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return Math.abs(FilingCabinetItem.metadata(stack)%2);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return Integer.toString(Math.abs(FilingCabinetItem.metadata(stack)%2));}});}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,FilingCabinetContent.creativeStacks("block"));}
}
