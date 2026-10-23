package com.hbm.block.skeletonholder;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Original hidden BlockSkeletonHolder and true source TileEntitySkeletonHolder. */
public final class SkeletonHolderContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<SkeletonHolderBlock> SKELETON_HOLDER=BLOCKS.register("skeleton_holder",SkeletonHolderBlock::new);
 public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(SKELETON_HOLDER);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SkeletonHolderEntity>> ENTITY=ENTITIES.register("skeleton_holder",()->BlockEntityType.Builder.of(SkeletonHolderEntity::new,SKELETON_HOLDER.get()).build(null));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
 public static List<ItemStack> creativeStacks(String tab){return List.of();}
 private SkeletonHolderContent(){}
}
