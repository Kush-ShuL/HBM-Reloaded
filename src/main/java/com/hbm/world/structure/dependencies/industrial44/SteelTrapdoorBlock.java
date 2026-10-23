package com.hbm.world.structure.dependencies.industrial44;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
/** Original BlockNTMTrapdoor + Forge1.7 support rules, rather than modern iron-trapdoor manual restrictions. */
public final class SteelTrapdoorBlock extends Block {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 public SteelTrapdoorBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(3,4.8F).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion());registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 private static Direction support(int meta){return switch(meta&3){case 0->Direction.SOUTH;case 1->Direction.NORTH;case 2->Direction.EAST;default->Direction.WEST;};}
 private static boolean oldSupport(BlockState state,BlockGetter world,BlockPos pos){return state.isSolidRender(world,pos)||state.is(Blocks.GLOWSTONE)||state.getBlock() instanceof SlabBlock||state.getBlock() instanceof StairBlock;}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){Direction side=context.getClickedFace();if(side.getAxis().isVertical())return null;BlockPos support=context.getClickedPos().relative(side.getOpposite());var state=context.getLevel().getBlockState(support);if(!oldSupport(state,context.getLevel(),support)&&!state.isFaceSturdy(context.getLevel(),support,Direction.UP))return null;int meta=switch(side){case NORTH->0;case SOUTH->1;case WEST->2;default->3;};if(context.getClickLocation().y-context.getClickedPos().getY()>.5)meta|=8;return defaultBlockState().setValue(META,meta);}
 @Override public boolean isLadder(BlockState state,LevelReader world,BlockPos pos,LivingEntity entity){return (state.getValue(META)&4)!=0&&world.getBlockState(pos.below()).isLadder(world,pos.below(),entity);}
 private static VoxelShape shape(int meta,int thickness){if((meta&4)==0)return (meta&8)!=0?Block.box(0,16-thickness,0,16,16,16):Block.box(0,0,0,16,thickness,16);return switch(meta&3){case 0->Block.box(0,0,16-thickness,16,16,16);case 1->Block.box(0,0,0,16,16,thickness);case 2->Block.box(16-thickness,0,0,16,16,16);default->Block.box(0,0,0,thickness,16,16);};}
 @Override protected VoxelShape getShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){return shape(state.getValue(META),3);}
 @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){boolean ladder=world instanceof LevelReader reader&&isLadder(state,reader,pos,null);return shape(state.getValue(META),ladder?2:3);}
 private void sound(Level world,BlockPos pos,Player player,boolean open){world.playSound(player,pos,open?SoundEvents.WOODEN_DOOR_OPEN:SoundEvents.WOODEN_DOOR_CLOSE,SoundSource.BLOCKS,1,world.random.nextFloat()*.1F+.9F);}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level world,BlockPos pos,Player player,BlockHitResult hit){int meta=state.getValue(META)^4;world.setBlock(pos,state.setValue(META,meta),2);sound(world,pos,player,(meta&4)!=0);return InteractionResult.sidedSuccess(world.isClientSide);}
 @Override protected void neighborChanged(BlockState state,Level world,BlockPos pos,Block neighbor,BlockPos from,boolean moving){if(world.isClientSide)return;int meta=state.getValue(META);Direction side=support(meta);BlockPos support=pos.relative(side);var backing=world.getBlockState(support);if(!oldSupport(backing,world,support)&&!backing.isFaceSturdy(world,support,side.getOpposite())){dropResources(state,world,pos);world.removeBlock(pos,false);return;}boolean powered=world.hasNeighborSignal(pos);if((powered||neighbor.defaultBlockState().isSignalSource())&&((meta&4)!=0)!=powered){world.setBlock(pos,state.setValue(META,meta^4),2);sound(world,pos,null,powered);}}
 @Override protected boolean isPathfindable(BlockState state,PathComputationType type){return (state.getValue(META)&4)!=0;}
 @Override protected int getLightBlock(BlockState state,BlockGetter world,BlockPos pos){return 0;}
 @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter world,BlockPos pos){return true;}
 @Override protected BlockState rotate(BlockState state,Rotation rotation){int meta=state.getValue(META);Direction direction=rotation.rotate(support(meta));return state.setValue(META,(meta&12)|switch(direction){case SOUTH->0;case NORTH->1;case EAST->2;default->3;});}
 @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(META);Direction direction=mirror.mirror(support(meta));return state.setValue(META,(meta&12)|switch(direction){case SOUTH->0;case NORTH->1;case EAST->2;default->3;});}
}
