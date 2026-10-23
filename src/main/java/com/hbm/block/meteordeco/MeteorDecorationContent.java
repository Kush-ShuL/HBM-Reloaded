package com.hbm.block.meteordeco;
import java.util.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class MeteorDecorationContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");public static final DeferredBlock<MetalFenceBlock> FENCE=BLOCKS.register("fence_metal",MetalFenceBlock::new);public static final DeferredBlock<SteelPanelBlock> WALL=BLOCKS.register("steel_wall",()->new SteelPanelBlock(false)),CORNER=BLOCKS.register("steel_corner",()->new SteelPanelBlock(true));public static final DeferredItem<MetalFenceItem> FENCE_ITEM=ITEMS.register("fence_metal",()->new MetalFenceItem(FENCE.get()));public static final DeferredItem<BlockItem> WALL_ITEM=ITEMS.registerSimpleBlockItem("steel_wall",WALL),CORNER_ITEM=ITEMS.registerSimpleBlockItem("steel_corner",CORNER);
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}public static List<ItemStack> creativeStacks(String tab){return switch(tab){case "machine"->List.of(FENCE.get().stack(0,1),FENCE.get().stack(1,1));case "block"->List.of(new ItemStack(WALL_ITEM.get()),new ItemStack(CORNER_ITEM.get()));default->List.of();};}private MeteorDecorationContent(){}
}
