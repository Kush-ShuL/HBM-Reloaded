package com.hbm.block.specialvehicle;

import com.hbm.weapon.specialvehicle.VehicleContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Source TileEntityDecoBlock contains no data, no ticking, no GUI or inventory. */
public final class VehicleDecoBlockEntity extends BlockEntity {
    public VehicleDecoBlockEntity(BlockPos pos,BlockState state){super(VehicleContent.TILE.get(),pos,state);}
}
