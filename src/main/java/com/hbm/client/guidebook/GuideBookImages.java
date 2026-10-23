package com.hbm.client.guidebook;

import com.hbm.fluid.HbmFluids;
import com.hbm.item.fluid.FluidItemContent;
import com.hbm.item.guidebook.GuideBooks.GuideImage;
import com.hbm.machine.chemicalplant.ChemicalPlantRecipeConfig;
import com.hbm.recipe.generic.Blueprints;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

/** Repairs only two image references already absent from X5846, using the actual current item renderers. */
final class GuideBookImages {
    static ItemStack actualItem(GuideImage image){
        return switch(image.image.toString()){
            case "hbm:textures/items/canister_gasoline.png" -> FluidItemContent.stack(FluidItemContent.CANISTER.get(),HbmFluids.GASOLINE);
            case "hbm:textures/items/chemistry_template.png" -> {
                var level=Minecraft.getInstance().level;
                // Old single-purpose chemistry_template was retired by source's GenericRecipes/ItemBlueprints.
                // Pick an actual synchronized chemical recipe's real pool, preserving its real item/model/NBT.
                yield level==null?ItemStack.EMPTY:ChemicalPlantRecipeConfig.recipes(level.getRecipeManager()).stream().flatMap(recipe->recipe.pools().stream()).filter(pool->!pool.startsWith(Blueprints.SECRET)).findFirst().map(Blueprints::make).orElse(ItemStack.EMPTY);
            }
            default -> ItemStack.EMPTY;
        };
    }
    private GuideBookImages(){}
}
