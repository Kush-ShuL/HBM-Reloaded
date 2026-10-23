package com.hbm.client.weapon.legendary;
import com.hbm.weapon.sedna.gun.legendary.LegendaryGunsContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Original NEI exposes these real legendary identities although they have no creative-tab assignment. */
@JeiPlugin public final class LegendaryGunsJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:legendary_guns_jei");}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,LegendaryGunsContent.indexedItems());}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){for(var stack:LegendaryGunsContent.variants())event.registerSubtypeInterpreter(stack.getItem(),new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return stack.getItem();}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();}});}
}
