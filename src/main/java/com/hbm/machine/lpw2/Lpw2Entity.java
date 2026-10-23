package com.hbm.machine.lpw2;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Source TileEntityMachineLPW2 has only render bounds; intentionally no tick, state or fake interfaces. */
public final class Lpw2Entity extends BlockEntity {
    public Lpw2Entity(BlockPos pos,BlockState state){super(Lpw2Content.ENTITY.get(),pos,state);}
    @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&Lpw2Block.isCore(state);}
}
