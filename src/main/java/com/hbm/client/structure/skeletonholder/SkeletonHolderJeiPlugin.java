package com.hbm.client.structure.skeletonholder;
import com.hbm.block.skeletonholder.SkeletonHolderContent;
import java.util.List;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Source is hidden from creative tabs, not from NEI's registry-item list. No invented recipe category. */
@JeiPlugin public final class SkeletonHolderJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:skeleton_holder_jei");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,List.of(new ItemStack(SkeletonHolderContent.ITEM.get())));}
}
