package com.hbm.machine.hexafluoride;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Both real X5846 TileEntityMachine*F6Tank classes are empty: only native identity/coordinates persist. */
public final class HexafluorideTankEntity extends BlockEntity {
    public HexafluorideTankEntity(BlockPos pos,BlockState state){
        super(((HexafluorideTankBlock)state.getBlock()).plutonium?HexafluorideTankContent.PUF6_ENTITY.get():HexafluorideTankContent.UF6_ENTITY.get(),pos,state);
    }
}
