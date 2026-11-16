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

/** Upstream tracking radar multiblock and client deployment animation. */
public final class TrackingRadarContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    public static final DeferredBlock<TrackingRadarBlock> BLOCK = BLOCKS.register("machine_tracking_radar", TrackingRadarBlock::new);
    public static final DeferredItem<BlockItem> ITEM = ITEMS.register("machine_tracking_radar", () -> new BlockItem(BLOCK.get(), new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RadarEntity>> ENTITY = ENTITIES.register("tracking_radar", () -> BlockEntityType.Builder.of(RadarEntity::new, BLOCK.get()).build(null));
    public static final class RadarEntity extends BlockEntity {
        public static final float SPEED=.25F, ACTIVE_ROT=-15F, ACTIVE_LIFT=-45F, INACTIVE_ROT=0F, INACTIVE_LIFT=-85F;
        public float rot=INACTIVE_ROT,prevRot=INACTIVE_ROT,lift=INACTIVE_LIFT,prevLift=INACTIVE_LIFT;
        public RadarEntity(BlockPos pos, BlockState state) { super(ENTITY.get(), pos, state); }
        public void clientTick(){
            prevRot=rot;prevLift=lift;
            rot=net.minecraft.util.Mth.approach(rot,ACTIVE_ROT,SPEED);
            lift=net.minecraft.util.Mth.approach(lift,ACTIVE_LIFT,SPEED);
        }
    }
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) { return "missile".equals(tab) ? List.of(new ItemStack(ITEM.get())) : List.of(); }
    private TrackingRadarContent() { }
}
