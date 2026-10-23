package com.hbm.client.assemblyfactory;
import com.hbm.machine.assembler.AssemblerRecipe;
import com.hbm.machine.assemblyfactory.AssemblyFactoryContent;
import mezz.jei.api.*;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.resources.ResourceLocation;
/** Adds the real four-field assembler to the unique existing source assembly category. */
@JeiPlugin
public final class AssemblyFactoryJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:assembly_factory_jei");}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(RecipeType.create("hbm","assembly",AssemblerRecipe.class),AssemblyFactoryContent.ITEM.get());}
}
