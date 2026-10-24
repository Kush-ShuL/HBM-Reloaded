package com.hbm.client.starcomponents;
import com.hbm.item.starcomponents.StarComponentsContent;import mezz.jei.api.*;import mezz.jei.api.constants.VanillaTypes;import mezz.jei.api.ingredients.subtypes.*;import mezz.jei.api.registration.*;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.item.ItemStack;
/** Four genuine metadata variants remain separate ingredients in the existing native crafting/machine categories. */
@JeiPlugin
public final class StarComponentsJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:star_components305_jei");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,StarComponentsContent.indexedItems());}
}
