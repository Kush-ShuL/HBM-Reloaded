package com.hbm.client.meteoritesword;
import com.hbm.item.meteoritesword.MeteoriteSwordContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
/** Source ModItems.excludeNEI contains both intermediate swords; recipe display ingredients stay real. */
@JeiPlugin
public final class MeteoriteSwordJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:meteorite_sword_stages");}@Override public void onRuntimeAvailable(IJeiRuntime runtime){runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK,MeteoriteSwordContent.indexedItems());}
}
