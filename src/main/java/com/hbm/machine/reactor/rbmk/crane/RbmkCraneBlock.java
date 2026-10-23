package com.hbm.machine.reactor.rbmk.crane;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.machine.reactor.rbmk.console.RbmkLinkerItem;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
/** Source two-high three-wide console plus one-row floor extension, upper dummy collision halfheight. */
public final class RbmkCraneBlock extends DummyableBlock implements ToolableBlock {
    public RbmkCraneBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(3,18).sound(SoundType.STONE).requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends RbmkCraneBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{1,0,0,0,1,1};}@Override public int offset(){return 1;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=super.getStateForPlacement(context);if(state==null)return null;var core=coreForPlacement(context);var d=rotate(new int[]{0,0,0,1,1,1},context.getHorizontalDirection().getOpposite());for(var p:BlockPos.betweenClosed(core.offset(-d[4],0,-d[2]),core.offset(d[5],0,d[3])))if(!context.getLevel().isInWorldBounds(p)||!context.getLevel().hasChunkAt(p)||!p.equals(context.getClickedPos())&&!context.getLevel().getBlockState(p).canBeReplaced())return null;return state;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity entity,ItemStack stack){super.setPlacedBy(level,pos,state,entity,stack);if(level.isClientSide)return;var facing=placementFacing(state);var core=pos.relative(facing,-offset());var d=rotate(new int[]{0,0,0,1,1,1},facing);safely(()->{for(var raw:BlockPos.betweenClosed(core.offset(-d[4],0,-d[2]),core.offset(d[5],0,d[3]))){var p=raw.immutable();if(p.equals(core))continue;var away=p.getX()<core.getX()?Direction.WEST:p.getX()>core.getX()?Direction.EAST:p.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH;level.setBlock(p,defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),UPDATE_ALL);}});}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new RbmkCraneEntity(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,RbmkCraneContent.ENTITY.get(),RbmkCraneEntity::tick):null;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){return state.getValue(LEGACY_META)==1?box(0,0,0,16,8,16):Shapes.block();}@Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){return getShape(state,world,pos,context);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return stack.getItem() instanceof RbmkLinkerItem?ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION:super.useItemOn(stack,state,level,pos,player,hand,hit);}
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){if(tool!=ToolType.SCREWDRIVER)return false;var core=findCore(level,pos);if(!level.isClientSide&&core!=null&&level.getBlockEntity(core) instanceof RbmkCraneEntity crane)crane.cycleCraneRotation();return true;}
}
