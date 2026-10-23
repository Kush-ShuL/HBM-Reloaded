package com.hbm.client.rebar;
import com.hbm.block.rebar.RebarContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;
@JeiPlugin public final class RebarJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:rebar_jei");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){var stacks=new java.util.ArrayList<net.minecraft.world.item.ItemStack>();stacks.addAll(RebarContent.creativeStacks("block"));stacks.addAll(RebarContent.creativeStacks("consumable"));event.addExtraIngredients(VanillaTypes.ITEM_STACK,stacks);}
}
