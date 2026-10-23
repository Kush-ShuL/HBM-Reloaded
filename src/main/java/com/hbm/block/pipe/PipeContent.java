package com.hbm.block.pipe;

import com.hbm.fluid.network.FluidNetworks;
import java.util.ArrayList;
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

public final class PipeContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    public static final DeferredBlock<NativePipeBlock> BLOCK = BLOCKS.register("fluid_duct_neo", () -> new NativePipeBlock(false));
    public static final DeferredBlock<NativePipeBlock> VALVE = BLOCKS.register("fluid_valve", () -> new NativePipeBlock(true));
    public static final DeferredItem<PipeBlockItem> ITEM = ITEMS.register("fluid_duct_neo", () -> new PipeBlockItem(BLOCK.get()));
    public static final DeferredItem<BlockItem> VALVE_ITEM = ITEMS.register("fluid_valve", () -> new BlockItem(VALVE.get(), new Item.Properties()));
    public static final DeferredItem<TypedPipeItem> TYPED_ITEM = ITEMS.register("fluid_duct", TypedPipeItem::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NativePipeBlockEntity>> ENTITY = ENTITIES.register("fluid_duct", () -> BlockEntityType.Builder.<NativePipeBlockEntity>of((pos, state) -> ((NativePipeBlock) state.getBlock()).kind().instrument() ? new InstrumentPipeBlockEntity(pos, state) : new NativePipeBlockEntity(pos, state), entityBlocks()).build(null));
    public static final java.util.Map<PipeKind, DeferredBlock<SpecialPipeBlock>> SPECIAL_BLOCKS = new java.util.LinkedHashMap<>();
    public static final java.util.Map<PipeKind, DeferredItem<PipeBlockItem>> SPECIAL_ITEMS = new java.util.LinkedHashMap<>();
    public static final DeferredItem<com.hbm.item.tool.WrenchItem> WRENCH = ITEMS.register("wrench", com.hbm.item.tool.WrenchItem::new);
    static {
        special("fluid_duct_box", PipeKind.BOX); special("fluid_duct_exhaust", PipeKind.EXHAUST);
        special("fluid_duct_paintable_block_exhaust", PipeKind.PAINTABLE_EXHAUST);
        special("pipe_anchor", PipeKind.ANCHOR); special("pipe_anchor_exhaust", PipeKind.EXHAUST_ANCHOR);
        special("fluid_duct_paintable", PipeKind.PAINTABLE); special("fluid_duct_gauge", PipeKind.GAUGE);
        special("fluid_counter_valve", PipeKind.COUNTER);
    }
    private static void special(String id, PipeKind kind) {
        var block = BLOCKS.register(id, () -> new SpecialPipeBlock(kind)); SPECIAL_BLOCKS.put(kind, block);
        SPECIAL_ITEMS.put(kind, ITEMS.register(id, () -> new PipeBlockItem(block.get())));
    }
    private static net.minecraft.world.level.block.Block[] entityBlocks() {
        var blocks = new ArrayList<net.minecraft.world.level.block.Block>(); blocks.add(BLOCK.get()); blocks.add(VALVE.get());
        SPECIAL_BLOCKS.values().forEach(value -> blocks.add(value.get())); return blocks.toArray(net.minecraft.world.level.block.Block[]::new);
    }
    public static ItemStack stack(SpecialPipeBlock block, int metadata) { return SPECIAL_ITEMS.get(block.kind()).get().stack(metadata); }
    public static List<PipeBlockItem> subtypeItems() { var items = new ArrayList<PipeBlockItem>(); items.add(ITEM.get()); SPECIAL_ITEMS.values().stream().map(DeferredItem::get).forEach(items::add); return items; }
    public static final java.util.Map<String, DeferredBlock<com.hbm.block.pipe.decorative.DecorativePipeBlock>> DECORATIVE_BLOCKS = new java.util.LinkedHashMap<>();
    public static final java.util.Map<String, DeferredItem<com.hbm.block.pipe.decorative.DecorativePipeItem>> DECORATIVE_ITEMS = new java.util.LinkedHashMap<>();
    static {
        for (String shape : new String[]{"", "_rim", "_framed", "_quad"}) for (String material : new String[]{"", "_rusted", "_green", "_green_rusted", "_red", "_marked"}) {
            String id = "deco_pipe" + shape + material;
            var block = BLOCKS.register(id, com.hbm.block.pipe.decorative.DecorativePipeBlock::new); DECORATIVE_BLOCKS.put(id, block);
            DECORATIVE_ITEMS.put(id, ITEMS.register(id, () -> new com.hbm.block.pipe.decorative.DecorativePipeItem(block.get())));
        }
    }
    private PipeContent() {}
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); FluidNetworks.register(bus); SourcePipeMarkingRecipes.register(); bus.addListener(PipeControls::register); }
    public static List<ItemStack> creativeStacks(String tab) {
        var result = new ArrayList<ItemStack>();
        if (tab.equals("machine")) { for (int i = 0; i < 3; i++) result.add(ITEM.get().stack(i)); SPECIAL_ITEMS.entrySet().stream().filter(entry -> entry.getKey() != PipeKind.COUNTER).forEach(entry -> result.addAll(entry.getValue().get().creativeStacks())); result.add(new ItemStack(VALVE_ITEM.get())); result.addAll(SPECIAL_ITEMS.get(PipeKind.COUNTER).get().creativeStacks()); }
        if (tab.equals("block")) DECORATIVE_ITEMS.values().forEach(item -> result.add(new ItemStack(item.get())));
        if (tab.equals("parts")) result.add(new ItemStack(WRENCH.get()));
        if (tab.equals("template")) result.addAll(TYPED_ITEM.get().creativeStacks());
        return result;
    }
}
