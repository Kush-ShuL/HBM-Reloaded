package com.hbm.item.structurewand;

import com.hbm.world.structure.StructureAuthoringJobs;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class StructureWandContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredItem<StructureWandItem> WAND=ITEMS.register("wand_s",StructureWandItem::new);
    public static final DeferredBlock<StructureAuthoringBlock> SAVE_BLOCK=BLOCKS.register("wand_structure_save",()->new StructureAuthoringBlock(false));
    public static final DeferredBlock<StructureAuthoringBlock> LOAD_BLOCK=BLOCKS.register("wand_structure_load",()->new StructureAuthoringBlock(true));
    public static final DeferredItem<StructureAuthoringItem> SAVE_ITEM=ITEMS.register("wand_structure_save",()->new StructureAuthoringItem(SAVE_BLOCK.get(),new Item.Properties()));
    public static final DeferredItem<StructureAuthoringItem> LOAD_ITEM=ITEMS.register("wand_structure_load",()->new StructureAuthoringItem(LOAD_BLOCK.get(),new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<StructureAuthoringEntity>> ENTITY=ENTITIES.register("wand_structure",()->BlockEntityType.Builder.of(StructureAuthoringEntity::new,SAVE_BLOCK.get(),LOAD_BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener(StructureWandPackets::register);StructureAuthoringJobs.register();}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(WAND.get()),new ItemStack(SAVE_ITEM.get()),new ItemStack(LOAD_ITEM.get()));}
    public static List<ItemStack> creativeStacks(String tab){return "consumable".equals(tab)?List.of(new ItemStack(WAND.get())):List.of();} // Source controller has no assigned creative tab.
    private StructureWandContent(){}
}
