package com.hbm.item.structureexport;

import java.util.Arrays;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class StructureExportContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<StructureExportItem> SINGLE=ITEMS.register("structure_single",()->new StructureExportItem(StructureExportItem.Mode.SINGLE));
    public static final DeferredItem<StructureExportItem> SOLID=ITEMS.register("structure_solid",()->new StructureExportItem(StructureExportItem.Mode.SOLID));
    public static final DeferredItem<StructureExportItem> PATTERN=ITEMS.register("structure_pattern",()->new StructureExportItem(StructureExportItem.Mode.PATTERN));
    public static final DeferredItem<StructureExportItem> RANDOMIZED=ITEMS.register("structure_randomized",()->new StructureExportItem(StructureExportItem.Mode.RANDOMIZED));
    public static final DeferredItem<StructureExportItem> RANDOMLY=ITEMS.register("structure_randomly",()->new StructureExportItem(StructureExportItem.Mode.RANDOMLY));
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static List<ItemStack> indexedItems(){return Arrays.asList(new ItemStack(SINGLE.get()),new ItemStack(SOLID.get()),new ItemStack(PATTERN.get()),new ItemStack(RANDOMIZED.get()),new ItemStack(RANDOMLY.get()));}
    public static List<ItemStack> creativeStacks(String tab){return List.of();} // All five original items setCreativeTab(null).
    private StructureExportContent(){}
}
