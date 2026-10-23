package com.hbm.satellite.science;

import com.hbm.satellite.*;
import com.hbm.recipe.generic.Blueprints;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Reuses the existing three source payload metadata and the sole orbital repository; registers no duplicate items. */
public final class ScienceSatelliteContent {
    private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredHolder<RecipeType<?>,RecipeType<SpaceAssemblerRecipe>> TYPE=TYPES.register("space_assembler",()->new RecipeType<SpaceAssemblerRecipe>(){});
    public static final DeferredHolder<RecipeSerializer<?>,SpaceAssemblerRecipe.Serializer> SERIALIZER=SERIALIZERS.register("space_assembler",SpaceAssemblerRecipe.Serializer::new);
    public static void register(IEventBus bus){
        SatelliteRegistry.bind(12,SatelliteScience.class,SatelliteScience::new);
        Blueprints.declareRecipeSet(ResourceLocation.parse("hbm:space_assembler"),"SpaceAssemblerRecipes");
        TYPES.register(bus);SERIALIZERS.register(bus);SpaceAssemblerRecipes.register();
    }
    public static List<ItemStack> allStacks(){return List.of(com.hbm.satellite.item.SatelliteItem.stack(SatelliteKind.SCIENCE),com.hbm.satellite.item.SatelliteItem.stack(SatelliteKind.SCIENCE_ASSEMBLER),com.hbm.satellite.item.SatelliteItem.stack(SatelliteKind.SCIENCE_SENSOR));}
    // The existing SatelliteContent.creativeStacks("missile") discovers these through the narrow SatelliteKind.ready patch.
    private ScienceSatelliteContent(){}
}
