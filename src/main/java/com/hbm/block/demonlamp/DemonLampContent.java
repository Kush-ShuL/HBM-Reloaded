package com.hbm.block.demonlamp;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Sole source lamp identity; no inventory, HE, GUI, fluid or computer interface exists in X5846. */
public final class DemonLampContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<DemonLampBlock> LAMP=BLOCKS.register("lamp_demon",DemonLampBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(LAMP);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<DemonLampEntity>> ENTITY=ENTITIES.register("lamp_demon",()->BlockEntityType.Builder.of(DemonLampEntity::new,LAMP.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(ITEM.get())):List.of();}
    private DemonLampContent(){}
}
