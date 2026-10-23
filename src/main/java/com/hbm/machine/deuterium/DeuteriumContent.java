package com.hbm.machine.deuterium;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Original two registrations; no menu, new fluid, compatibility capability or substitute filter item. */
public final class DeuteriumContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<DeuteriumExtractorBlock> EXTRACTOR=BLOCKS.register("machine_deuterium_extractor",DeuteriumExtractorBlock::new);
    public static final DeferredBlock<DeuteriumTowerBlock> TOWER=BLOCKS.register("machine_deuterium_tower",DeuteriumTowerBlock::new);
    public static final DeferredItem<BlockItem> EXTRACTOR_ITEM=ITEMS.register("machine_deuterium_extractor",()->new BlockItem(EXTRACTOR.get(),new Item.Properties()));
    public static final DeferredItem<BlockItem> TOWER_ITEM=ITEMS.register("machine_deuterium_tower",()->new BlockItem(TOWER.get(),new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<DeuteriumEntity>> EXTRACTOR_ENTITY=ENTITIES.register("deuterium_extractor",()->BlockEntityType.Builder.of(DeuteriumEntity::new,EXTRACTOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<DeuteriumTowerEntity>> TOWER_ENTITY=ENTITIES.register("deuterium_tower",()->BlockEntityType.Builder.of(DeuteriumTowerEntity::new,TOWER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<DeuteriumPort>> TOWER_PORT=ENTITIES.register("deuterium_tower_port",()->BlockEntityType.Builder.of(DeuteriumPort::new,TOWER.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(EXTRACTOR_ITEM.get()),new ItemStack(TOWER_ITEM.get())):List.of();}
    private DeuteriumContent(){}
}
