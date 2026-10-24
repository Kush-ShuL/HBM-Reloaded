package com.hbm.block.masonry;

import java.util.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class MasonryContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final List<String> CONCRETE_MATERIALS=List.of("concrete_smooth","concrete","concrete_asbestos","ducrete_smooth","ducrete","asphalt");
    public static final List<String> BRICK_MATERIALS=List.of("brick_concrete","brick_concrete_mossy","brick_concrete_cracked","brick_concrete_broken","brick_ducrete");
    public static final List<String> REINFORCED_MATERIALS=List.of("reinforced_stone","reinforced_brick","brick_obsidian","brick_light","brick_compound","brick_asbestos","brick_fire");
    public static final DeferredBlock<AsphaltBlock> ASPHALT=BLOCKS.register("asphalt",()->new AsphaltBlock());
    public static final DeferredItem<BlockItem> ASPHALT_ITEM=ITEMS.register("asphalt",()->new BlockItem(ASPHALT.get(),new Item.Properties()) {
        @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<net.minecraft.network.chat.Component> lines,TooltipFlag flag) { lines.add(net.minecraft.network.chat.Component.translatable("tooltip.hbm.asphalt.speed").withStyle(net.minecraft.ChatFormatting.BLUE)); }
    });
    public static final DeferredBlock<LegacyMultiSlabBlock> CONCRETE=BLOCKS.register("concrete_slab",()->new LegacyMultiSlabBlock(false,CONCRETE_MATERIALS,()->MasonryContent.CONCRETE.get(),()->MasonryContent.CONCRETE_DOUBLE.get()));
    public static final DeferredBlock<LegacyMultiSlabBlock> CONCRETE_DOUBLE=BLOCKS.register("concrete_double_slab",()->new LegacyMultiSlabBlock(true,CONCRETE_MATERIALS,()->CONCRETE.get(),()->MasonryContent.CONCRETE_DOUBLE.get()));
    public static final DeferredBlock<LegacyMultiSlabBlock> BRICK=BLOCKS.register("concrete_brick_slab",()->new LegacyMultiSlabBlock(false,BRICK_MATERIALS,()->MasonryContent.BRICK.get(),()->MasonryContent.BRICK_DOUBLE.get()));
    public static final DeferredBlock<LegacyMultiSlabBlock> BRICK_DOUBLE=BLOCKS.register("concrete_brick_double_slab",()->new LegacyMultiSlabBlock(true,BRICK_MATERIALS,()->BRICK.get(),()->MasonryContent.BRICK_DOUBLE.get()));
    public static final DeferredBlock<LegacyMultiSlabBlock> REINFORCED=BLOCKS.register("brick_slab",()->new LegacyMultiSlabBlock(false,REINFORCED_MATERIALS,()->MasonryContent.REINFORCED.get(),()->MasonryContent.REINFORCED_DOUBLE.get()));
    public static final DeferredBlock<LegacyMultiSlabBlock> REINFORCED_DOUBLE=BLOCKS.register("brick_double_slab",()->new LegacyMultiSlabBlock(true,REINFORCED_MATERIALS,()->REINFORCED.get(),()->MasonryContent.REINFORCED_DOUBLE.get()));
    public static final DeferredItem<MultiSlabItem> CONCRETE_ITEM=ITEMS.register("concrete_slab",()->new MultiSlabItem(CONCRETE.get(),"concrete_slab")),CONCRETE_DOUBLE_ITEM=ITEMS.register("concrete_double_slab",()->new MultiSlabItem(CONCRETE_DOUBLE.get(),"concrete_slab")),BRICK_ITEM=ITEMS.register("concrete_brick_slab",()->new MultiSlabItem(BRICK.get(),"concrete_brick_slab")),BRICK_DOUBLE_ITEM=ITEMS.register("concrete_brick_double_slab",()->new MultiSlabItem(BRICK_DOUBLE.get(),"concrete_brick_slab"));
    public static final DeferredItem<MultiSlabItem> REINFORCED_ITEM=ITEMS.register("brick_slab",()->new MultiSlabItem(REINFORCED.get(),"brick_slab")),REINFORCED_DOUBLE_ITEM=ITEMS.register("brick_double_slab",()->new MultiSlabItem(REINFORCED_DOUBLE.get(),"brick_slab"));
    private static final Map<String,DeferredBlock<LegacyLadderBlock>> LADDERS=new LinkedHashMap<>();
    private static final List<DeferredItem<BlockItem>> LADDER_ITEMS=new ArrayList<>();
    static {
        for (String name:List.of("ladder_sturdy","ladder_gold","ladder_copper","ladder_titanium","ladder_steel","ladder_tungsten")) {
            var block=BLOCKS.register(name,()->new LegacyLadderBlock());
            LADDERS.put(name,block); LADDER_ITEMS.add(ITEMS.registerSimpleBlockItem(name,block));
        }
    }
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); }
    public static List<MultiSlabItem> slabItems() { return List.of(CONCRETE_ITEM.get(),CONCRETE_DOUBLE_ITEM.get(),BRICK_ITEM.get(),BRICK_DOUBLE_ITEM.get(),REINFORCED_ITEM.get(),REINFORCED_DOUBLE_ITEM.get()); }
    public static List<ItemStack> creativeStacks(String tab) {
        if (!tab.equals("block")) return List.of();
        var stacks=new ArrayList<ItemStack>(); stacks.add(new ItemStack(ASPHALT_ITEM.get()));
        for (int i=0;i<CONCRETE_MATERIALS.size();i++) stacks.add(CONCRETE.get().stack(i,1));
        for (int i=0;i<BRICK_MATERIALS.size();i++) stacks.add(BRICK.get().stack(i,1));
        for (int i=0;i<REINFORCED_MATERIALS.size();i++) stacks.add(REINFORCED.get().stack(i,1));
        LADDER_ITEMS.forEach(item->stacks.add(new ItemStack(item.get())));
        return stacks;
    }
    private MasonryContent() { }
}
