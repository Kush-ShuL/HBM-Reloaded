package com.hbm.machine.electricfurnace;

import com.hbm.item.battery.BatteryContent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Registration is separate from basic inert content: these blocks have real machine behavior. */
public final class ElectricFurnaceContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "hbm");
    public static final DeferredBlock<ElectricFurnaceBlock> OFF = BLOCKS.register("machine_electric_furnace_off", () -> new ElectricFurnaceBlock(false));
    public static final DeferredBlock<ElectricFurnaceBlock> ON = BLOCKS.register("machine_electric_furnace_on", () -> new ElectricFurnaceBlock(true));
    public static final DeferredItem<net.minecraft.world.item.BlockItem> ITEM = ITEMS.registerSimpleBlockItem(OFF);
    public static final DeferredItem<net.minecraft.world.item.BlockItem> ON_ITEM = ITEMS.registerSimpleBlockItem(ON);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ElectricFurnaceBlockEntity>> ENTITY = ENTITIES.register("machine_electric_furnace", () -> BlockEntityType.Builder.of(ElectricFurnaceBlockEntity::new, OFF.get(), ON.get()).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<ElectricFurnaceMenu>> MENU = MENUS.register("electric_furnace", () -> new MenuType<>(ElectricFurnaceMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final List<DeferredItem<FurnaceUpgradeItem>> UPGRADES = new ArrayList<>();
    static {
        for (FurnaceUpgradeItem.Type type : FurnaceUpgradeItem.Type.values()) {
            for (int level = 1; level <= 3; level++) {
                int tier = level;
                UPGRADES.add(ITEMS.register("upgrade_" + type.name().toLowerCase(java.util.Locale.ROOT) + "_" + level, () -> new FurnaceUpgradeItem(type, tier)));
            }
        }
    }
    private ElectricFurnaceContent() {}
    public static void register(IEventBus bus) {
        BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); MENUS.register(bus);
        BatteryContent.register(bus);
        bus.addListener(ElectricFurnaceContent::registerCapabilities);
    }
    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ENTITY.get(), (machine, side) -> new SidedInvWrapper(machine, side == null ? net.minecraft.core.Direction.UP : side));
    }
    public static List<ItemStack> machineItems() { return List.of(new ItemStack(ITEM.get())); }
    public static List<ItemStack> controlItems() {
        List<ItemStack> items = new ArrayList<>(BatteryContent.controlItems());
        UPGRADES.forEach(upgrade -> items.add(new ItemStack(upgrade.get())));
        return items;
    }
}
