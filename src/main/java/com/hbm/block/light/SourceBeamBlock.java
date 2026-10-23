package com.hbm.block.light;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.*;
/** BlockBeamBase native LightBlock adaptation: stored light, no geometry, selection, collision or drops. */
public abstract class SourceBeamBlock extends BaseEntityBlock {
 protected SourceBeamBlock(){super(Properties.of().mapColor(MapColor.NONE).replaceable().noCollission().noOcclusion().lightLevel(state->15).strength(-1,600000).pushReaction(PushReaction.DESTROY).isViewBlocking((s,w,p)->false).isSuffocating((s,w,p)->false));}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.INVISIBLE;}
 @Override protected VoxelShape getShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){return Shapes.empty();}
 @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){return Shapes.empty();}
 @Override protected int getLightBlock(BlockState state,BlockGetter world,BlockPos pos){return 0;}@Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter world,BlockPos pos){return true;}
 @Override protected boolean isPathfindable(BlockState state,PathComputationType type){return true;}@Override protected boolean canBeReplaced(BlockState state,BlockPlaceContext context){return true;}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of();}
}
