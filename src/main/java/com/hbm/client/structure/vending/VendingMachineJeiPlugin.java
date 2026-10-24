package com.hbm.client.structure.vending;
import com.hbm.world.structure.vending.*;import mezz.jei.api.*;import mezz.jei.api.constants.VanillaTypes;import mezz.jei.api.registration.*;import net.minecraft.resources.ResourceLocation;
/** The two actual source vending variants are distinct JEI entries, without fabricating a recipe category. */
@JeiPlugin public final class VendingMachineJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:vending_machine_jei");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,VendingMachineContent.creativeStacks("block"));}
}
