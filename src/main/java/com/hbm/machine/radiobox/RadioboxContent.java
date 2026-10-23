package com.hbm.machine.radiobox;

import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** One real block/item and BE. Source ContainerRadiobox is empty and provideGUI returns null. */
public final class RadioboxContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    public static final DeferredBlock<RadioboxBlock> BLOCK = BLOCKS.register("radiobox", RadioboxBlock::new);
    public static final DeferredItem<BlockItem> ITEM = ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RadioboxEntity>> ENTITY =
            ENTITIES.register("radiobox", () -> BlockEntityType.Builder.of(RadioboxEntity::new, BLOCK.get()).build(null));
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("machine") ? List.of(new ItemStack(ITEM.get())) : List.of(); }
    public static Map<String, Item> indexedItems() { return Map.of("radiobox", ITEM.get()); }
    private RadioboxContent() { }
}
