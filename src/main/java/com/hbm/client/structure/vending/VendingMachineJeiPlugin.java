package com.hbm.client.structure.vending;
import com.hbm.world.structure.vending.*;import mezz.jei.api.*;import mezz.jei.api.constants.VanillaTypes;import mezz.jei.api.ingredients.subtypes.*;import mezz.jei.api.registration.*;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.item.ItemStack;
/** The two actual source vending variants are distinct JEI entries, without fabricating a recipe category. */
@JeiPlugin public final class VendingMachineJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:vending_machine_jei");}
 @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(VendingMachineContent.ITEM.get(),new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return Math.abs(VendingMachineItem.metadata(stack)%2);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return Integer.toString(Math.abs(VendingMachineItem.metadata(stack)%2));}});}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,VendingMachineContent.creativeStacks("block"));}
}
