package com.hbm.block.swamp;

import java.util.List;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** X5863's swamp dirt, with the final authored hardness of five. */
public final class SwampDirtContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredBlock<Block> DIRT = BLOCKS.register("dirt_swamp",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).strength(5).sound(SoundType.GRAVEL)));
    public static final DeferredItem<BlockItem> ITEM = ITEMS.register("dirt_swamp", () -> new BlockItem(DIRT.get(), new Item.Properties()));

    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("block") ? List.of(new ItemStack(ITEM.get())) : List.of(); }
    private SwampDirtContent() { }
}
