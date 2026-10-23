package com.hbm.block.devtesters;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Source passive TileEntity: no ticking, inventory, animation state, power or made-up GUI. */
public final class ObjTesterEntity extends BlockEntity {public ObjTesterEntity(BlockPos pos,BlockState state){super(DevTestersContent.OBJ_ENTITY.get(),pos,state);}}
