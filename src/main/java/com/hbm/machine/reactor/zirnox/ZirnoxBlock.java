package com.hbm.machine.reactor.zirnox;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** Exact union of the original four multiblock cuboids, including the narrow upper side port columns. */
public final class ZirnoxBlock extends DummyableBlock {
    private static final MapCodec<ZirnoxBlock> CODEC=simpleCodec(ignored->new ZirnoxBlock());
    private static final int[][] DIMENSIONS={{1,0,2,2,2,2},{4,-2,1,1,1,1},{4,-2,0,0,2,-2},{4,-2,0,0,-2,2}};
    public ZirnoxBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,100).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return CODEC;}@Override public int[] dimensions(){return DIMENSIONS[0].clone();}@Override public int offset(){return 2;}
    static Set<BlockPos> positions(BlockPos core,Direction facing){var positions=new LinkedHashSet<BlockPos>();for(var original:DIMENSIONS){int[] d=rotate(original,facing);for(var pos:BlockPos.betweenClosed(core.offset(-d[4],-d[1],-d[2]),core.offset(d[5],d[0],d[3])))positions.add(pos.immutable());}return positions;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=super.getStateForPlacement(context);if(state==null)return null;var core=coreForPlacement(context);for(var pos:positions(core,placementFacing(state))){if(!context.getLevel().isInWorldBounds(pos)||!context.getLevel().hasChunkAt(pos)||!pos.equals(context.getClickedPos())&&!context.getLevel().getBlockState(pos).canBeReplaced())return null;}return state;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var facing=placementFacing(state);var core=pos.relative(facing,-offset());safely(()->{for(var part:positions(core,facing)){if(part.equals(core)||level.getBlockState(part).is(this))continue;Direction away=part.getY()>core.getY()?Direction.UP:part.getY()<core.getY()?Direction.DOWN:part.getX()<core.getX()?Direction.WEST:part.getX()>core.getX()?Direction.EAST:part.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH;level.setBlock(part,defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),3);}for(var port:fluidPorts(core,facing))setPort(level,port,true);setPort(level,core.above(4),true);});}
    public static Direction rotation(Direction facing){return facing.getClockWise();}
    public static BlockPos[] fluidPorts(BlockPos core,Direction facing){var side=rotation(facing);return new BlockPos[]{core.relative(side,2).above(),core.relative(side,2).above(3),core.relative(side,-2).above(),core.relative(side,-2).above(3)};}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new ZirnoxBlockEntity(pos,state):isPort(state)?new ZirnoxPort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,ZirnoxContent.REACTOR_ENTITY.get(),ZirnoxBlockEntity::tick):null;}
    @Override protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,net.minecraft.world.phys.BlockHitResult hit){if(!level.isClientSide&&!player.isShiftKeyDown()){var tag=player.getPersistentData();var persistent=tag.getCompound(Player.PERSISTED_NBT_TAG);persistent.putLong("fbiMark",level.getGameTime()+24000);tag.put(Player.PERSISTED_NBT_TAG,persistent);}return super.useWithoutItem(state,level,pos,player,hit);}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){super.neighborChanged(state,level,pos,neighbor,from,moving);if(level.isClientSide)return;var core=findCore(level,pos);if(core==null||!(level.getBlockEntity(core) instanceof ZirnoxBlockEntity reactor))return;boolean powered=false;for(int dx=-2;dx<=2&&!powered;dx++)for(int dy=0;dy<=4&&!powered;dy++)for(int dz=-2;dz<=2&&!powered;dz++)if(dx==-2||dx==2||dy==0||dy==4||dz==-2||dz==2){var scan=core.offset(dx,dy,dz);if(level.hasNeighborSignal(scan)||level.getBestNeighborSignal(scan)>0)powered=true;}reactor.setRedstonePowered(powered);}
}
