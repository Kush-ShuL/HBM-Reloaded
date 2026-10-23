package com.hbm.network.cargo;
import com.hbm.network.rtty.RadioTorchBlock;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;
public final class DroneWaypointBlock extends BaseEntityBlock {
 public DroneWaypointBlock(){super(Properties.of().strength(.1F,6).sound(SoundType.STONE).noOcclusion().noCollission());registerDefaultState(stateDefinition.any().setValue(RadioTorchBlock.LEGACY_META,1));}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(RadioTorchBlock.LEGACY_META);}@Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
 @Override protected int getLightBlock(BlockState s,BlockGetter l,BlockPos p){return 0;}@Override protected boolean propagatesSkylightDown(BlockState s,BlockGetter l,BlockPos p){return true;}@Override protected VoxelShape getOcclusionShape(BlockState s,BlockGetter l,BlockPos p){return Shapes.empty();}@Override protected VoxelShape getCollisionShape(BlockState s,BlockGetter l,BlockPos p,CollisionContext c){return Shapes.empty();}
 @Override protected VoxelShape getShape(BlockState state,BlockGetter l,BlockPos p,CollisionContext c){int meta=state.getValue(RadioTorchBlock.LEGACY_META)&7;var dir=meta<6?Direction.from3DDataValue(meta):null;int x=dir==null?0:dir.getStepX(),y=dir==null?0:dir.getStepY(),z=dir==null?0:dir.getStepZ();return Shapes.box(x==1?0:.375,y==1?0:.375,z==1?0:.375,x==-1?1:.625,y==-1?1:.625,z==-1?1:.625);}
 @Override public BlockState getStateForPlacement(BlockPlaceContext c){var s=defaultBlockState().setValue(RadioTorchBlock.LEGACY_META,c.getClickedFace().get3DDataValue());return s.canSurvive(c.getLevel(),c.getClickedPos())?s:null;}
 @Override protected boolean canSurvive(BlockState s,LevelReader l,BlockPos p){var d=RadioTorchBlock.outward(s);var support=d==null?p:p.relative(d.getOpposite());var b=l.getBlockState(support);return d!=null&&b.isFaceSturdy(l,support,d)||!b.isAir()&&b.isSolidRender(l,support);}
 @Override protected void neighborChanged(BlockState s,Level l,BlockPos p,Block b,BlockPos from,boolean moving){if(!s.canSurvive(l,p))l.destroyBlock(p,true);}
 @Override protected InteractionResult useWithoutItem(BlockState s,Level l,BlockPos p,Player player,BlockHitResult hit){if(player.getMainHandItem().is(CargoDroneContent.LINKER.get()))return InteractionResult.PASS;if(!l.isClientSide&&l.getBlockEntity(p) instanceof DroneWaypointEntity tile)tile.addHeight(player.isShiftKeyDown()?-1:1);return InteractionResult.sidedSuccess(l.isClientSide);}
 @Override public BlockEntity newBlockEntity(BlockPos p,BlockState s){return new DroneWaypointEntity(p,s);}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level l,BlockState s,BlockEntityType<T> t){return createTickerHelper(t,CargoDroneContent.WAYPOINT_ENTITY.get(),DroneWaypointEntity::tick);}@Override protected List<ItemStack> getDrops(BlockState s,LootParams.Builder b){return List.of(new ItemStack(this));}
}
