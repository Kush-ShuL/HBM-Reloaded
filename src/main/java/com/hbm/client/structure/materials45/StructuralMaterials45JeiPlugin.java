package com.hbm.client.structure.materials45;
import com.hbm.world.structure.dependencies.materials45.StructuralMaterials45Content;
import com.hbm.hazard.HbmHazards;
import mezz.jei.api.*;import mezz.jei.api.constants.VanillaTypes;import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import mezz.jei.api.ingredients.subtypes.*;
@JeiPlugin public final class StructuralMaterials45JeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:structural_materials45_jei");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,StructuralMaterials45Content.plateStacks());}
 @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(StructuralMaterials45Content.PLATEMETAL_ITEM.get(),new ISubtypeInterpreter<ItemStack>(){public Object getSubtypeData(ItemStack stack,UidContext context){return com.hbm.world.structure.dependencies.materials45.PlatemetalBlock.rectify(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0));}public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return getSubtypeData(stack,context).toString();}});}
}
