package com.hbm.client.bobmazon;
import com.hbm.bobmazon.BobmazonContent;
import java.util.List;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin public final class BobmazonJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:bobmazon_catalog");}
 @Override public void onRuntimeAvailable(IJeiRuntime runtime){runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK,List.of(new ItemStack(BobmazonContent.HIDDEN.get())));}
}
