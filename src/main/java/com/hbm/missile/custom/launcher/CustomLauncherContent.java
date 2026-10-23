package com.hbm.missile.custom.launcher;

import java.util.List;
import com.hbm.registry.HbmContent;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

public final class CustomLauncherContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "hbm");
    public static final DeferredBlock<CustomLauncherBlock> COMPACT = BLOCKS.register("compact_launcher", () -> new CustomLauncherBlock(false)), TABLE = BLOCKS.register("launch_table", () -> new CustomLauncherBlock(true));
    public static final DeferredBlock<CustomLauncherCoreBlock> CORE = BLOCKS.register("struct_launcher_core", () -> new CustomLauncherCoreBlock(false)), LARGE_CORE = BLOCKS.register("struct_launcher_core_large", () -> new CustomLauncherCoreBlock(true));
    // These two source BlockGeneric foundations already have their actual basic registrations. Reuse their one identities.
    public static final java.util.function.Supplier<Block> STRUCTURE = () -> HbmContent.block("struct_launcher"), SCAFFOLD = () -> HbmContent.block("struct_scaffold");
    public static final DeferredItem<BlockItem> COMPACT_ITEM = ITEMS.registerSimpleBlockItem("compact_launcher", COMPACT), TABLE_ITEM = ITEMS.registerSimpleBlockItem("launch_table", TABLE),
            CORE_ITEM = ITEMS.registerSimpleBlockItem("struct_launcher_core", CORE), LARGE_CORE_ITEM = ITEMS.registerSimpleBlockItem("struct_launcher_core_large", LARGE_CORE);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CompactLauncherEntity>> COMPACT_ENTITY = ENTITIES.register("compact_launcher", () -> BlockEntityType.Builder.of(CompactLauncherEntity::new, COMPACT.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LaunchTableEntity>> TABLE_ENTITY = ENTITIES.register("launch_table", () -> BlockEntityType.Builder.of(LaunchTableEntity::new, TABLE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CustomLauncherPort>> COMPACT_PORT = ENTITIES.register("compact_launcher_port", () -> BlockEntityType.Builder.of(CustomLauncherPort::new, COMPACT.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LaunchTablePort>> TABLE_PORT = ENTITIES.register("launch_table_port", () -> BlockEntityType.Builder.of(LaunchTablePort::new, TABLE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CustomLauncherCoreEntity>> CORE_ENTITY = ENTITIES.register("custom_launcher_structure", () -> BlockEntityType.Builder.of(CustomLauncherCoreEntity::new, CORE.get(), LARGE_CORE.get()).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<CustomLauncherMenu>> COMPACT_MENU = MENUS.register("compact_launcher", () -> new MenuType<>((IContainerFactory<CustomLauncherMenu>) (id, player, data) -> new CustomLauncherMenu(id, player, data, false), FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>, MenuType<CustomLauncherMenu>> TABLE_MENU = MENUS.register("launch_table", () -> new MenuType<>((IContainerFactory<CustomLauncherMenu>) (id, player, data) -> new CustomLauncherMenu(id, player, data, true), FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); MENUS.register(bus); bus.addListener(CustomLauncherContent::capabilities); bus.addListener(CustomLauncherSync::register); }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        // Source all-side ISidedInventory rejects both insertion and extraction. Null-side access must not bypass that rule.
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, COMPACT_ENTITY.get(), (be, side) -> new SidedInvWrapper(be, side == null ? Direction.DOWN : side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TABLE_ENTITY.get(), (be, side) -> new SidedInvWrapper(be, side == null ? Direction.DOWN : side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, COMPACT_PORT.get(), (be, side) -> new SidedInvWrapper(be, side == null ? Direction.DOWN : side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TABLE_PORT.get(), (be, side) -> new SidedInvWrapper(be, side == null ? Direction.DOWN : side));
    }
    public static List<ItemStack> creativeStacks(String tab) { return "missile".equals(tab) ? List.of(new ItemStack(COMPACT_ITEM.get()), new ItemStack(TABLE_ITEM.get()), new ItemStack(CORE_ITEM.get()), new ItemStack(LARGE_CORE_ITEM.get())) : List.of(); }
    private CustomLauncherContent() {}
}
