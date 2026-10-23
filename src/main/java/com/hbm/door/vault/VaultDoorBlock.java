package com.hbm.door.vault;

import com.hbm.door.generic.*;
import com.hbm.multiblock.DummyableBlock;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Actual original VAULT_DOOR extra XR footprint; sole GenericDoor base owns collision, permissions, state, drops and NBT. */
public final class VaultDoorBlock extends GenericDoorBlock {
    private static final int[] EXTRA={0,0,1,-1,2,2};
    public VaultDoorBlock(){super(GenericDoorDeclaration.VAULT_DOOR);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=super.getStateForPlacement(context);if(state==null)return null;var core=coreForPlacement(context);var dimensions=DummyableBlock.rotate(EXTRA,placementFacing(state));for(int x=-dimensions[4];x<=dimensions[5];x++)for(int y=-dimensions[1];y<=dimensions[0];y++)for(int z=-dimensions[2];z<=dimensions[3];z++){var cell=core.offset(x,y,z);if(!context.getLevel().isInWorldBounds(cell)||!context.getLevel().hasChunkAt(cell)||!cell.equals(context.getClickedPos())&&!context.getLevel().getBlockState(cell).canBeReplaced())return null;}return state;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var facing=placementFacing(state);var core=pos.relative(facing,-offset());var dimensions=DummyableBlock.rotate(EXTRA,facing);safely(()->{for(int x=-dimensions[4];x<=dimensions[5];x++)for(int y=-dimensions[1];y<=dimensions[0];y++)for(int z=-dimensions[2];z<=dimensions[3];z++){Direction away=y<0?Direction.DOWN:y>0?Direction.UP:x<0?Direction.WEST:x>0?Direction.EAST:z<0?Direction.NORTH:z>0?Direction.SOUTH:null;if(away!=null)level.setBlock(core.offset(x,y,z),defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),UPDATE_ALL);}});}
}
