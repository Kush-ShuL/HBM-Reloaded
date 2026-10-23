package com.hbm.client.ingotmetal;
import com.hbm.item.ingotmetal.FlatIngotMetalContent;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
/** Exact unconditional X5846 NEIConfig51 hide rule, after replacing its nine metadata stacks. */
@JeiPlugin
public final class IngotMetalJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","ingot_metal_source_hidden");}
    @Override public void onRuntimeAvailable(IJeiRuntime runtime){runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK,FlatIngotMetalContent.allStacks());}
}
