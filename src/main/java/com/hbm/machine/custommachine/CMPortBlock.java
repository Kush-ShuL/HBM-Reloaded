package com.hbm.machine.custommachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Real source BlockCMPort: inventory/power/fluid Combo proxy only; no own reservoir or processing. */
public final class CMPortBlock extends CMComponentBlock implements EntityBlock {
    public CMPortBlock(){super(Kind.PORT);}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new CMPortEntity(pos,state);}
}
