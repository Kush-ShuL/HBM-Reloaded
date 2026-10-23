package com.hbm.client.junglemechanics;
import com.hbm.block.junglemechanics.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin public final class JungleMechanicsJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:jungle_mechanics");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,JungleMechanicsContent.allBlockStacks());}
 @Override public void registerItemSubtypes(ISubtypeRegistration event){for(var item:java.util.List.of(JungleMechanicsContent.TRAP_ITEM.get(),JungleMechanicsContent.GLYPH_ITEM.get()))event.registerSubtypeInterpreter(item,new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return JungleMetaItem.metadata(stack);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return Integer.toString(JungleMetaItem.metadata(stack));}});}
}
