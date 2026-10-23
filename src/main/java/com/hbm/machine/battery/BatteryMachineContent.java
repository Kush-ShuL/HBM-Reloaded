package com.hbm.machine.battery;

import java.util.EnumMap;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** All five registered blocks share the original storage machine behavior. */
public final class BatteryMachineContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "hbm");
    public static final EnumMap<BatteryTier, DeferredBlock<BatteryBlock>> BLOCKS_BY_TIER = new EnumMap<>(BatteryTier.class);
    public static final EnumMap<BatteryTier, DeferredItem<BatteryMachineItem>> ITEMS_BY_TIER = new EnumMap<>(BatteryTier.class);
    static {
        for (BatteryTier tier : BatteryTier.values()) {
            var block = BLOCKS.register(tier.id(), () -> new BatteryBlock(tier));
            BLOCKS_BY_TIER.put(tier, block);
            ITEMS_BY_TIER.put(tier, ITEMS.register(tier.id(), () -> new BatteryMachineItem(block.get())));
        }
    }
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BatteryBlockEntity>> ENTITY =
            ENTITIES.register("machine_battery", () -> BlockEntityType.Builder.of(BatteryBlockEntity::new,
                    BLOCKS_BY_TIER.values().stream().map(DeferredBlock::get).toArray(BatteryBlock[]::new)).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<BatteryMenu>> MENU =
            MENUS.register("machine_battery", () -> new MenuType<>(BatteryMenu::new, FeatureFlags.DEFAULT_FLAGS));

    private BatteryMachineContent() {}

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        MENUS.register(bus);
        bus.addListener(BatteryMachineContent::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ENTITY.get(),
                (machine, side) -> side == null ? new InvWrapper(machine) : new SidedInvWrapper(machine, side));
    }

    public static List<ItemStack> machineItems() {
        return ITEMS_BY_TIER.values().stream().map(item -> new ItemStack(item.get())).toList();
    }
}
