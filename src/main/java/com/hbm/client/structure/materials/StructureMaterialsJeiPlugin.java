package com.hbm.client.structure.materials;
import com.hbm.world.structure.dependencies.materials.StructureMaterialsContent;
import mezz.jei.api.*;import mezz.jei.api.constants.VanillaTypes;import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;
@JeiPlugin public final class StructureMaterialsJeiPlugin implements IModPlugin {
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:structure_materials_jei");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,StructureMaterialsContent.allStacks());}
}
