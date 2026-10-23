package com.hbm.machine.supercomputer;
import com.hbm.recipe.generic.Blueprints;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Unique original SuperComputerRecipes native type and source config entrypoint. */
public final class SuperComputerRecipes {
 private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
 public static final DeferredHolder<RecipeType<?>,RecipeType<SuperComputerRecipe>> TYPE=TYPES.register("supercomputer",()->new RecipeType<>(){});public static final DeferredHolder<RecipeSerializer<?>,SuperComputerRecipe.Serializer> SERIALIZER=SERIALIZERS.register("supercomputer",SuperComputerRecipe.Serializer::new);
 public static void register(IEventBus bus){TYPES.register(bus);SERIALIZERS.register(bus);Blueprints.declareRecipeSet(ResourceLocation.parse("hbm:supercomputer"),"SuperComputerRecipes");SuperComputerRecipeConfig.register();}
 private SuperComputerRecipes(){}
}
