package com.hbm.block.structurebasic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Modern render host only: no source gameplay state, inventory, ticking, capabilities or custom save fields. */
public final class ReedsRenderEntity extends BlockEntity {
 public ReedsRenderEntity(BlockPos pos,BlockState state){super(BasicStructureContent.REEDS_ENTITY.get(),pos,state);}
 public static int depth(LevelReader level,BlockPos pos,boolean full){if(!full)return 1;int depth=0;for(int y=pos.getY()-1;y>level.getMinBuildHeight();y--){depth=pos.getY()-y;if(!level.getBlockState(new BlockPos(pos.getX(),y,pos.getZ())).is(Blocks.WATER))break;}return depth;}
}
