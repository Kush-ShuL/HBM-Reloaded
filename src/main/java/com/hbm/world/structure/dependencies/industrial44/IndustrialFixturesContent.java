package com.hbm.world.structure.dependencies.industrial44;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Four real X5846 natural-structure industrial fixtures; no substituted vanilla blocks. */
public final class IndustrialFixturesContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<ReinforcedPaneBlock> PANE=BLOCKS.register("reinforced_glass_pane",ReinforcedPaneBlock::new);
 public static final DeferredBlock<PoleTopBlock> POLE=BLOCKS.register("pole_top",PoleTopBlock::new);
 public static final DeferredBlock<SteelRoofBlock> ROOF=BLOCKS.register("steel_roof",SteelRoofBlock::new);
 public static final DeferredBlock<SteelTrapdoorBlock> TRAPDOOR=BLOCKS.register("trapdoor_steel",SteelTrapdoorBlock::new);
 public static final DeferredItem<BlastPaneItem> PANE_ITEM=ITEMS.register("reinforced_glass_pane",()->new BlastPaneItem(PANE.get()));
 public static final DeferredItem<BlockItem> POLE_ITEM=ITEMS.registerSimpleBlockItem(POLE),ROOF_ITEM=ITEMS.registerSimpleBlockItem(ROOF),TRAPDOOR_ITEM=ITEMS.registerSimpleBlockItem(TRAPDOOR);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SteelRoofEntity>> ROOF_ENTITY=ENTITIES.register("deco_steel_roof",()->BlockEntityType.Builder.of(SteelRoofEntity::new,ROOF.get()).build(null));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(PANE_ITEM.get()),new ItemStack(POLE_ITEM.get()),new ItemStack(ROOF_ITEM.get()),new ItemStack(TRAPDOOR_ITEM.get())):List.of();}
 private IndustrialFixturesContent(){}
}
