package com.hbm.world.structure.templates.client;
import com.hbm.world.structure.templates.materials.TemplateMaterials;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin public final class TemplateMaterialJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:natural_template_materials");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,TemplateMaterials.creativeStacks("block"));}
 @Override public void registerItemSubtypes(ISubtypeRegistration event){for(var holder:TemplateMaterials.enums()){var item=holder.get();event.registerSubtypeInterpreter(item,new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return item.ordinal(stack);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return Integer.toString(item.ordinal(stack));}});}}
}
