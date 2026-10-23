package com.hbm.block.lantern;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Single true ModBlocks.lantern identity, with original block-tab item and ordinary-lantern BE. */
public final class LanternContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    public static final DeferredBlock<LanternBlock> BLOCK = BLOCKS.register("lantern", LanternBlock::new);
    public static final DeferredItem<BlockItem> ITEM = ITEMS.register("lantern", () -> new BlockItem(BLOCK.get(), new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LanternEntity>> ENTITY = ENTITIES.register("lantern", () ->
            BlockEntityType.Builder.of(LanternEntity::new, BLOCK.get()).build(null));
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("block") ? List.of(new ItemStack(ITEM.get())) : List.of(); }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(ITEM.get())); }
    private LanternContent() { }
}
