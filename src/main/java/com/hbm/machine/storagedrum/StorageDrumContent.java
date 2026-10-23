package com.hbm.machine.storagedrum;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class StorageDrumContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "hbm");
    public static final DeferredBlock<StorageDrumBlock> BLOCK = BLOCKS.register("machine_storage_drum", StorageDrumBlock::new);
    public static final DeferredItem<BlockItem> ITEM = ITEMS.register("machine_storage_drum", () -> new BlockItem(BLOCK.get(), new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StorageDrumEntity>> ENTITY = ENTITIES.register("storage_drum", () -> BlockEntityType.Builder.of(StorageDrumEntity::new, BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<StorageDrumMenu>> MENU = MENUS.register("storage_drum", () -> new MenuType<>((IContainerFactory<StorageDrumMenu>) StorageDrumMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus) {
        BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); MENUS.register(bus);
        bus.addListener(StorageDrumContent::capabilities);
    }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ENTITY.get(), (drum, side) -> side == null ? new InvWrapper(drum) : new SidedInvWrapper(drum, side));
    }
    public static List<ItemStack> creativeStacks(String tab) { return "machine".equals(tab) ? List.of(new ItemStack(ITEM.get())) : List.of(); }
    private StorageDrumContent() {}
}
