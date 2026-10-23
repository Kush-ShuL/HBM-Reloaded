package com.hbm.block.fluid;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** Native world liquids are separate from the original 157 pipe fluid types. */
public final class HbmWorldFluids {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, "hbm");
    private static final DeferredRegister<FluidType> TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, "hbm");
    private static final Map<String, DeferredBlock<? extends Block>> BLOCK_LOOKUP = new LinkedHashMap<>();
    private static final Map<String, DeferredHolder<Fluid, LegacyWorldFluid>> FLUID_LOOKUP = new LinkedHashMap<>();
    private static final Map<String, DeferredHolder<FluidType, FluidType>> TYPE_LOOKUP = new LinkedHashMap<>();
    private static final List<java.util.function.Supplier<? extends Item>> HOST_ITEMS = new ArrayList<>(), BUCKETS = new ArrayList<>();
    private static List<WorldFluidSpec> specifications = List.of();
    private HbmWorldFluids() { }
    public static void register(IEventBus bus) {
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(NativeBucketInteraction::pickup);
        try (var stream = HbmWorldFluids.class.getResourceAsStream("/hbm/world-fluids.json")) {
            if (stream == null) throw new IllegalStateException("Run tools/port_world_fluids.py");
            specifications = List.copyOf(new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Definitions.class).fluids());
        } catch (java.io.IOException exception) { throw new IllegalStateException(exception); }
        for (WorldFluidSpec spec : specifications) {
            boolean water = spec.material().equals("water");
            DeferredHolder<FluidType, FluidType> type = TYPES.<FluidType>register(spec.fluid(), () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("tile." + spec.symbol() + ".name").density(spec.density()).viscosity(spec.viscosity())
                    .temperature(spec.temperature()).lightLevel(spec.luminosity()).canConvertToSource(false)
                    .canPushEntity(water).canSwim(water).canDrown(water).canExtinguish(water).supportsBoating(water)
                    .canHydrate(water)) {});
            TYPE_LOOKUP.put(spec.fluid(), type);
            DeferredHolder<Fluid, LegacyWorldFluid> fluid = FLUIDS.<LegacyWorldFluid>register(spec.fluid(), () -> new LegacyWorldFluid(spec, type, () -> (LegacyFluidBlock) block(spec.symbol())));
            FLUID_LOOKUP.put(spec.fluid(), fluid);
            var block = BLOCKS.register(spec.symbol(), () -> switch (spec.originalClass()) {
                case "VolcanicBlock", "RadBlock" -> new VolcanicFluidBlock(spec, fluid);
                case "GenericFiniteFluid" -> new LegacyFiniteFluidBlock(spec, fluid);
                case "CoriumFinite" -> new CoriumFluidBlock(spec, fluid);
                default -> new ReactiveFluidBlock(spec, fluid);
            });
            BLOCK_LOOKUP.put(spec.symbol(), block);
            if (spec.bucket() != null) {
                var bucket = ITEMS.register(spec.bucket(), () -> new LegacyFluidBucket(fluid.get()));
                BUCKETS.add(bucket);
                // Binding occurs only when the deferred bucket is accessed, after the fluid registry exists.
                fluidBucketBindings.put(spec.fluid(), bucket);
            }
        }
        for (String symbol : List.of("sellafield_slaked", "waste_log", "waste_planks")) {
            var block = BLOCKS.register(symbol, () -> new WorldFluidHostBlock(symbol));
            BLOCK_LOOKUP.put(symbol, block);
            HOST_ITEMS.add(ITEMS.registerSimpleBlockItem(symbol, block));
        }
        TYPES.register(bus); FLUIDS.register(bus); BLOCKS.register(bus); ITEMS.register(bus);
        bus.addListener((net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) -> event.enqueueWork(() ->
                fluidBucketBindings.forEach((id, bucket) -> FLUID_LOOKUP.get(id).get().bucket(bucket))));
    }
    private static final Map<String, java.util.function.Supplier<? extends Item>> fluidBucketBindings = new LinkedHashMap<>();
    public static Block block(String symbol) {
        var block = BLOCK_LOOKUP.get(symbol);
        if (block == null) throw new IllegalArgumentException("Unimplemented world fluid block " + symbol);
        return block.get();
    }
    public static LegacyWorldFluid fluid(String id) { return FLUID_LOOKUP.get(id).get(); }
    public static FluidType type(String id) { return TYPE_LOOKUP.get(id).get(); }
    public static List<WorldFluidSpec> specifications() { return specifications; }
    public static List<ItemStack> creativeStacks(String tab) {
        if (!tab.equals("block")) return List.of();
        return java.util.stream.Stream.concat(HOST_ITEMS.stream(), BUCKETS.stream()).map(item -> new ItemStack(item.get())).toList();
    }
    private record Definitions(List<WorldFluidSpec> fluids) { }
}
