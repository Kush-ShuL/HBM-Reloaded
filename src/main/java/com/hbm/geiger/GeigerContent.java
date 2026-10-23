package com.hbm.geiger;

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

public final class GeigerContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<GeigerBlock> BLOCK=BLOCKS.register("geiger",GeigerBlock::new);
    public static final DeferredItem<BlockItem> BLOCK_ITEM=ITEMS.register("geiger",()->new BlockItem(BLOCK.get(),new Item.Properties()));
    public static final DeferredItem<GeigerCounterItem> HANDHELD=ITEMS.register("geiger_counter",GeigerCounterItem::new);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<GeigerBlockEntity>> ENTITY=ENTITIES.register("geiger",()->BlockEntityType.Builder.of(GeigerBlockEntity::new,BLOCK.get()).build(null));
    private GeigerContent() {}
    public static void register(IEventBus bus) { BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("machine")?List.of(new ItemStack(BLOCK_ITEM.get())):tab.equals("consumable")?List.of(new ItemStack(HANDHELD.get())):List.of(); }
}
