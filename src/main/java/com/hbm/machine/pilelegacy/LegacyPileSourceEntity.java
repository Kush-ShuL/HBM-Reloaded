package com.hbm.machine.pilelegacy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Ra226Be emits12 streams of1; the actual plutonium product emits12 streams of2. */
public final class LegacyPileSourceEntity extends LegacyPileEntity {
    public LegacyPileSourceEntity(BlockPos pos,BlockState state){super(LegacyPileContent.SOURCE.get(),pos,state);}
    @Override public void updateEntity(){if(!level.isClientSide){int flux=getBlockState().is(LegacyPileContent.CHANNELS.get(LegacyPileKind.SOURCE).get())?1:2;for(int i=0;i<12;i++)castRay(flux);}}
}
