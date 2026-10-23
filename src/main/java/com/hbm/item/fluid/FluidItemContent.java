package com.hbm.item.fluid;

import com.hbm.fluid.FluidContainerRegistry;
import com.hbm.fluid.FluidTank;
import com.hbm.fluid.HbmFluidType;
import com.hbm.fluid.HbmFluids;
import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FluidItemContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "hbm");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FLUID_TYPE = COMPONENTS.register("fluid_type", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final DeferredItem<FluidContainerItem> CANISTER = shell("canister_full", FluidContainerItem.Kind.CANISTER, "canister_empty");
    public static final DeferredItem<FluidContainerItem> GAS = shell("gas_full", FluidContainerItem.Kind.GAS, "gas_empty");
    public static final DeferredItem<FluidContainerItem> TANK = shell("fluid_tank_full", FluidContainerItem.Kind.TANK, "fluid_tank_empty");
    public static final DeferredItem<FluidContainerItem> LEAD_TANK = shell("fluid_tank_lead_full", FluidContainerItem.Kind.LEAD_TANK, "fluid_tank_lead_empty");
    public static final DeferredItem<FluidContainerItem> BARREL = shell("fluid_barrel_full", FluidContainerItem.Kind.BARREL, "fluid_barrel_empty");
    public static final DeferredItem<FluidContainerItem> PACK = shell("fluid_pack_full", FluidContainerItem.Kind.PACK, "fluid_pack_empty");
    public static final DeferredItem<InfiniteFluidItem> INFINITE = ITEMS.register("fluid_barrel_infinite", () -> new InfiniteFluidItem(null, 1_000_000_000, 1, 1, true));
    public static final DeferredItem<InfiniteFluidItem> INF_WATER = ITEMS.register("inf_water", () -> new InfiniteFluidItem("WATER", 50, 1, 1, false));
    public static final DeferredItem<InfiniteFluidItem> INF_WATER_MK2 = ITEMS.register("inf_water_mk2", () -> new InfiniteFluidItem("WATER", 500, 1, 1, false));
    public static final DeferredItem<InfiniteFluidItem> CHLORINE_PINWHEEL = ITEMS.register("chlorine_pinwheel", () -> new InfiniteFluidItem("CHLORINE", 1, 2, 64, false));
    public static final DeferredItem<FluidIconItem> ICON = ITEMS.register("fluid_icon", FluidIconItem::new);
    public static final DeferredItem<FluidIdentifierItem> IDENTIFIER = ITEMS.register("fluid_identifier_multi", FluidIdentifierItem::new);
    private FluidItemContent() {}
    private static DeferredItem<FluidContainerItem> shell(String id, FluidContainerItem.Kind kind, String empty) { return ITEMS.register(id, () -> new FluidContainerItem(kind, empty)); }
    public static void register(IEventBus bus) {
        COMPONENTS.register(bus); ITEMS.register(bus);
        bus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(() -> {
            FluidTank.noDualUnload.add(INFINITE.get());
            FluidContainerRegistry.rebuild();
            com.hbm.registry.HbmOreDictionary.addProvider(FluidContainerRegistry::oreTags, FluidContainerRegistry::oreStacks);
            HbmFluids.additionalListeners.add(FluidContainerRegistry::rebuild);
        }));
        bus.addListener(FluidIdentifierItem::registerPayload);
    }
    public static void register(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, com.hbm.fluid.FluidContainerConfig.SPEC, "hbm-fluid-containers-common.toml");
        register(bus);
    }
    public static HbmFluidType type(ItemStack stack) {
        return HbmFluids.fromID(stack.has(FLUID_TYPE.get()) ? stack.get(FLUID_TYPE.get()) : stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0));
    }
    public static ItemStack stack(Item item, HbmFluidType type) {
        var stack = new ItemStack(item);
        setType(stack, type);
        return stack;
    }
    public static void setType(ItemStack stack, HbmFluidType type) { stack.set(FLUID_TYPE.get(), type.getID()); stack.set(HbmHazards.LEGACY_METADATA.get(), type.getID()); }
    public static List<Item> subtypedItems() { return List.of(CANISTER.get(), GAS.get(), TANK.get(), LEAD_TANK.get(), BARREL.get(), PACK.get(), IDENTIFIER.get(), ICON.get()); }
    public static List<ItemStack> creativeStacks(String tab) {
        var result = new ArrayList<ItemStack>();
        if (tab.equals("parts")) result.add(new ItemStack(CHLORINE_PINWHEEL.get()));
        if (tab.equals("template")) result.addAll(IDENTIFIER.get().creativeStacks());
        if (tab.equals("control")) {
            for (var holder : List.of(CANISTER, GAS, TANK, LEAD_TANK, BARREL)) result.addAll(holder.get().creativeStacks());
            result.add(new ItemStack(INFINITE.get()));
            result.addAll(PACK.get().creativeStacks());
            result.add(new ItemStack(INF_WATER.get())); result.add(new ItemStack(INF_WATER_MK2.get()));
        }
        return result;
    }
}
