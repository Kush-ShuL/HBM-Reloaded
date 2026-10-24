package com.hbm.recipe;

import com.hbm.fluid.FluidContainerRegistry;
import com.hbm.fluid.HbmFluids;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** 按当前服务端容器表匹配指定流体及容量，保留容器组件状态。 */
public record SourceFluidContainerIngredient(String fluid, int amount) implements ICustomIngredient {
    public static final MapCodec<SourceFluidContainerIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("fluid").forGetter(SourceFluidContainerIngredient::fluid),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("amount").forGetter(SourceFluidContainerIngredient::amount))
            .apply(instance, SourceFluidContainerIngredient::new));
    private static final DeferredRegister<IngredientType<?>> TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, "hbm");
    public static final DeferredHolder<IngredientType<?>, IngredientType<SourceFluidContainerIngredient>> TYPE =
            TYPES.register("source_fluid_container", () -> new IngredientType<>(CODEC));

    public static void register(IEventBus bus) { TYPES.register(bus); }

    @Override
    public boolean test(ItemStack stack) {
        return !stack.isEmpty() && FluidContainerRegistry.matchingContainers(HbmFluids.fromName(fluid), amount)
                .anyMatch(container -> FluidContainerRegistry.sameItem(container.fullContainer(), stack));
    }

    @Override
    public Stream<ItemStack> getItems() {
        var distinct = net.minecraft.world.item.ItemStackLinkedSet.createTypeAndComponentsSet();
        return FluidContainerRegistry.matchingContainers(HbmFluids.fromName(fluid), amount)
                .map(container -> container.fullContainer().copy())
                .filter(distinct::add);
    }

    @Override public boolean isSimple() { return false; }
    @Override public IngredientType<?> getType() { return TYPE.get(); }
}
