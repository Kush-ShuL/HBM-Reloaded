package com.hbm.machine.trackingradar;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Upstream tracking radar currently supplies a static multiblock and model. */
public final class TrackingRadarContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    public static final DeferredBlock<TrackingRadarBlock> BLOCK = BLOCKS.register("machine_tracking_radar", TrackingRadarBlock::new);
    public static final DeferredItem<BlockItem> ITEM = ITEMS.register("machine_tracking_radar", () -> new BlockItem(BLOCK.get(), new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RadarEntity>> ENTITY = ENTITIES.register("tracking_radar", () -> BlockEntityType.Builder.of(RadarEntity::new, BLOCK.get()).build(null));
    public static final class RadarEntity extends BlockEntity {
        public RadarEntity(BlockPos pos, BlockState state) { super(ENTITY.get(), pos, state); }
    }
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) { return "missile".equals(tab) ? List.of(new ItemStack(ITEM.get())) : List.of(); }
    private TrackingRadarContent() { }
}
