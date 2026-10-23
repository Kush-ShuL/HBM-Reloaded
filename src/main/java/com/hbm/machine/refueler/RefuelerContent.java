package com.hbm.machine.refueler;

import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Actual BlockRefueler/TileEntityRefueler, a player-equipment fluid filling station. */
public final class RefuelerContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> TYPES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<RefuelerBlock> BLOCK=BLOCKS.register("refueler",RefuelerBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("refueler",BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RefuelerEntity>> ENTITY=TYPES.register("refueler",()->BlockEntityType.Builder.of(RefuelerEntity::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);TYPES.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return "block".equals(tab)?List.of(new ItemStack(ITEM.get())):List.of();}
    public static Map<String,Item> indexedItems(){return Map.of("refueler",ITEM.get());}
    private RefuelerContent(){}
}
