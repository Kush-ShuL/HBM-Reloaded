package com.hbm.machine.pa.recipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Only real native recipe identities; machine blocks register separately once their full runtime chain closes. */
public final class PaRecipeContent{
    private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredHolder<RecipeType<?>,RecipeType<PaRecipe>> TYPE=TYPES.register("particle_accelerator",()->new RecipeType<>(){public String toString(){return "hbm:particle_accelerator";}});public static final DeferredHolder<RecipeSerializer<?>,RecipeSerializer<PaRecipe>> SERIALIZER=SERIALIZERS.register("particle_accelerator",PaRecipe.Serializer::new);
    public static void register(IEventBus bus){TYPES.register(bus);SERIALIZERS.register(bus);PaRecipes.register();}private PaRecipeContent(){}
}
