package com.hbm.machine.reactor.iter;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Actual FusionRecipes registry, not a surrogate crafting block. */
public final class IterRecipes {
    private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredHolder<RecipeType<?>,RecipeType<FusionRecipe>> TYPE=TYPES.register("fusion",()->new RecipeType<>(){});
    public static final DeferredHolder<RecipeSerializer<?>,FusionRecipe.Serializer> SERIALIZER=SERIALIZERS.register("fusion",FusionRecipe.Serializer::new);
    public static void register(IEventBus bus){TYPES.register(bus);SERIALIZERS.register(bus);FusionRecipeConfig.register();}
    private IterRecipes(){}
}
