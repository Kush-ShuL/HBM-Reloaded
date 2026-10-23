package com.hbm.machine.refinery.deco;

import java.util.List;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class DecoSteelContent {
    public static final List<String> NAMES = List.of("deco_steel", "deco_rusty_steel", "deco_red_copper", "deco_tungsten", "deco_aluminium", "deco_lead");
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredBlock<DecoSteelBlock> STEEL = BLOCKS.register("deco_steel", () -> new DecoSteelBlock());
    public static final DeferredBlock<DecoSteelBlock> RUSTY_STEEL = BLOCKS.register("deco_rusty_steel", () -> new DecoSteelBlock());
    public static final DeferredItem<BlockItem> STEEL_ITEM = ITEMS.registerSimpleBlockItem(STEEL);
    public static final DeferredItem<BlockItem> RUSTY_STEEL_ITEM = ITEMS.registerSimpleBlockItem(RUSTY_STEEL);
    public static final DeferredBlock<DecoSteelBlock> RED_COPPER = BLOCKS.register("deco_red_copper", () -> new DecoSteelBlock());
    public static final DeferredBlock<DecoSteelBlock> TUNGSTEN = BLOCKS.register("deco_tungsten", () -> new DecoSteelBlock());
    public static final DeferredBlock<DecoSteelBlock> ALUMINIUM = BLOCKS.register("deco_aluminium", () -> new DecoSteelBlock());
    public static final DeferredBlock<DecoSteelBlock> LEAD = BLOCKS.register("deco_lead", () -> new DecoSteelBlock());
    public static final DeferredItem<BlockItem> RED_COPPER_ITEM = ITEMS.registerSimpleBlockItem(RED_COPPER);
    public static final DeferredItem<BlockItem> TUNGSTEN_ITEM = ITEMS.registerSimpleBlockItem(TUNGSTEN);
    public static final DeferredItem<BlockItem> ALUMINIUM_ITEM = ITEMS.registerSimpleBlockItem(ALUMINIUM);
    public static final DeferredItem<BlockItem> LEAD_ITEM = ITEMS.registerSimpleBlockItem(LEAD);
    private DecoSteelContent() {}
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("block") ? List.of(new ItemStack(STEEL_ITEM.get()),new ItemStack(RUSTY_STEEL_ITEM.get()),new ItemStack(RED_COPPER_ITEM.get()),new ItemStack(TUNGSTEN_ITEM.get()),new ItemStack(ALUMINIUM_ITEM.get()),new ItemStack(LEAD_ITEM.get())) : List.of(); }
}
