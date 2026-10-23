package com.hbm.machine.pneumaticstorage;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Original inventory-free access terminal over the same real accessor cache. */
public final class PneumaticAccess extends PneumaticMachineBase {
    public PneumaticAccess(BlockPos pos,BlockState state){super(PneumaticKind.ACCESS,pos,state);}
    @Override public boolean stillValid(net.minecraft.world.entity.player.Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=225;}
}
