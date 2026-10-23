package com.hbm.machine.teleport;

import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** X5846's automatic teleporter, passive anchor, destination linker and rechargeable recall device. */
public final class TeleportContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<TeleporterBlock> TELEPORTER=BLOCKS.register("machine_teleporter",TeleporterBlock::new);
    public static final DeferredBlock<TeleanchorBlock> ANCHOR=BLOCKS.register("teleanchor",TeleanchorBlock::new);
    public static final DeferredItem<BlockItem> TELEPORTER_ITEM=ITEMS.register("machine_teleporter",()->new BlockItem(TELEPORTER.get(),new Item.Properties()));
    public static final DeferredItem<BlockItem> ANCHOR_ITEM=ITEMS.register("teleanchor",()->new BlockItem(ANCHOR.get(),new Item.Properties()));
    public static final DeferredItem<TelelinkerItem> LINKER=ITEMS.register("linker",TelelinkerItem::new);
    public static final DeferredItem<AnchorRemoteItem> REMOTE=ITEMS.register("anchor_remote",AnchorRemoteItem::new);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TeleporterEntity>> ENTITY=ENTITIES.register("machine_teleporter",()->BlockEntityType.Builder.of(TeleporterEntity::new,TELEPORTER.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static Map<String,Item> indexedItems(){return Map.of("machine_teleporter",TELEPORTER_ITEM.get(),"teleanchor",ANCHOR_ITEM.get(),"linker",LINKER.get(),"anchor_remote",REMOTE.get());}
    public static List<ItemStack> allStacks(){return List.of(new ItemStack(TELEPORTER_ITEM.get()),new ItemStack(ANCHOR_ITEM.get()),new ItemStack(LINKER.get()),REMOTE.get().emptyStack());}
    public static List<ItemStack> creativeStacks(String tab){return switch(tab){case "machine"->List.of(new ItemStack(TELEPORTER_ITEM.get()),new ItemStack(ANCHOR_ITEM.get()));case "consumable"->List.of(REMOTE.get().emptyStack(),new ItemStack(LINKER.get()));default->List.of();};}
    private TeleportContent(){}
}
