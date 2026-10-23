package com.hbm.machine.fractiontower;
import com.hbm.multiblock.DummyableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Source TileEntitySpacer has no ticking, storage, energy, fluids or GUI. */
public final class FractionSpacerEntity extends BlockEntity {
 public FractionSpacerEntity(BlockPos pos,BlockState state){super(FractionContent.SPACER_ENTITY.get(),pos,state);}@Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}
}
