package com.hbm.world.structure.dependencies.industrial44;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Real TileEntityDecoBlock host; original has no custom NBT, inventory, tick or network state. */
public final class SteelRoofEntity extends BlockEntity {
 public SteelRoofEntity(BlockPos pos,BlockState state){super(IndustrialFixturesContent.ROOF_ENTITY.get(),pos,state);}
}
