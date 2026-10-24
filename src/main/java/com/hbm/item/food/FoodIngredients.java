package com.hbm.item.food;

import com.hbm.fluid.FluidContainerConfig;
import com.hbm.fluid.HbmFluids;
import com.hbm.recipe.SourceFluidContainerIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** Match potion state and current fluid-container contents directly. */
public final class FoodIngredients {
    private static final DeferredRegister<IngredientType<?>> TYPES=DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES,"hbm");
    private static final DeferredHolder<IngredientType<?>,IngredientType<SourceWater>> WATER=TYPES.register("source_water_potion",()->new IngredientType<>(SourceWater.CODEC));
    private static final DeferredHolder<IngredientType<?>,IngredientType<NativeFluid>> FLUID=TYPES.register("native_drink_fluid",()->new IngredientType<>(NativeFluid.CODEC));
    public static void register(IEventBus bus){TYPES.register(bus);}
    public record SourceWater() implements ICustomIngredient {
        public static final MapCodec<SourceWater> CODEC=MapCodec.unit(new SourceWater());
        @Override public boolean test(ItemStack stack){return stack.is(Items.POTION)&&stack.getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY).potion().filter(Potions.WATER::equals).isPresent();}
        @Override public Stream<ItemStack> getItems(){var stack=new ItemStack(Items.POTION);stack.set(DataComponents.POTION_CONTENTS,new PotionContents(Potions.WATER));return Stream.of(stack);}
        @Override public boolean isSimple(){return false;}
        @Override public IngredientType<?> getType(){return WATER.get();}
    }
    public record NativeFluid(String fluid,int amount) implements ICustomIngredient {
        public static final MapCodec<NativeFluid> CODEC=RecordCodecBuilder.mapCodec(builder->builder.group(Codec.STRING.fieldOf("fluid").forGetter(NativeFluid::fluid),Codec.intRange(1,Integer.MAX_VALUE).fieldOf("amount").forGetter(NativeFluid::amount)).apply(builder,NativeFluid::new));
        private SourceFluidContainerIngredient containers(){return new SourceFluidContainerIngredient(fluid,amount);}
        @Override public boolean test(ItemStack stack){return containers().test(stack);}
        @Override public Stream<ItemStack> getItems(){return containers().getItems();}
        @Override public boolean isSimple(){return false;}
        @Override public IngredientType<?> getType(){return FLUID.get();}
    }
    private FoodIngredients(){}
}
