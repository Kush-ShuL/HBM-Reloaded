package com.hbm.world.structure.forest;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** TileEntityDecoPoleSatelliteReceiver has no tick, inventory, network or saved fields. */
public final class PoleReceiverBlockEntity extends BlockEntity {
 public PoleReceiverBlockEntity(BlockPos pos,BlockState state){super(ForestFixtureContent.RECEIVER_ENTITY.get(),pos,state);}
}
