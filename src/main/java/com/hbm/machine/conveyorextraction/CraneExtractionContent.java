package com.hbm.machine.conveyorextraction;

import com.hbm.conveyor.crane.CraneBaseBlock;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

/** Actual two source inventory endpoints, using the shared original CraneBase contracts. */
public final class CraneExtractionContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<CraneExtractorBlock> EXTRACTOR=BLOCKS.register("crane_extractor",CraneExtractorBlock::new);
    public static final DeferredBlock<CraneInserterBlock> INSERTER=BLOCKS.register("crane_inserter",CraneInserterBlock::new);
    public static final DeferredItem<CraneExtractionItem> EXTRACTOR_ITEM=ITEMS.register("crane_extractor",()->new CraneExtractionItem(EXTRACTOR.get(),"crane_extractor")),INSERTER_ITEM=ITEMS.register("crane_inserter",()->new CraneExtractionItem(INSERTER.get(),"crane_inserter"));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CraneExtractorEntity>> EXTRACTOR_ENTITY=ENTITIES.register("crane_extractor",()->BlockEntityType.Builder.of(CraneExtractorEntity::new,EXTRACTOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CraneInserterEntity>> INSERTER_ENTITY=ENTITIES.register("crane_inserter",()->BlockEntityType.Builder.of(CraneInserterEntity::new,INSERTER.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<CraneExtractionMenu>> EXTRACTOR_MENU=MENUS.register("crane_extractor",()->net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create((IContainerFactory<CraneExtractionMenu>)(id,inv,data)->new CraneExtractionMenu(id,inv,true,data))),INSERTER_MENU=MENUS.register("crane_inserter",()->net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create((IContainerFactory<CraneExtractionMenu>)(id,inv,data)->new CraneExtractionMenu(id,inv,false,data)));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(CraneExtractionControl::register);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(EXTRACTOR_ITEM.get()),new ItemStack(INSERTER_ITEM.get())):List.of();}
    private CraneExtractionContent(){}
}
