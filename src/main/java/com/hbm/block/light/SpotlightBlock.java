package com.hbm.block.light;
import com.hbm.api.block.SpotlightSource;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
/** Complete source Spotlight/SpotlightModular behavior. These three lamps require no HE in X5846. */
public final class SpotlightBlock extends Block implements SpotlightSource {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);public final SpotlightKind kind;public final boolean isOn;
 public SpotlightBlock(SpotlightKind kind,boolean on){super(Properties.of().mapColor(MapColor.NONE).strength(.5F,.5F).sound(SoundType.STONE).noCollission().noOcclusion().lightLevel(state->on?15:0));this.kind=kind;isOn=on;registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends Block> codec(){return simpleCodec(ignored->new SpotlightBlock(kind,isOn));}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 public BlockState fromLegacy(int meta){return defaultBlockState().setValue(META,meta&15);}public static Direction direction(int meta){int side=meta>>1;return side>=0&&side<6?Direction.from3DDataValue(side):null;}
 @Override public int getBeamLength(){return kind.beamLength;}public boolean broken(int meta){return(meta&1)==1;}public SpotlightBlock on(){return SourceLightContent.ON.get(kind).get();}public SpotlightBlock off(){return SourceLightContent.OFF.get(kind).get();}
 public static int rotatedMeta(int meta,int turns){int side=meta>>1;for(int i=0;i<(turns&3);i++)side=switch(side){case 2->5;case 5->3;case 3->4;case 4->2;default->side;};return side<<1|meta&1;}
 @Override protected BlockState rotate(BlockState state,Rotation rotation){return fromLegacy(rotatedMeta(state.getValue(META),switch(rotation){case NONE->0;case CLOCKWISE_90->1;case CLOCKWISE_180->2;case COUNTERCLOCKWISE_90->3;}));}
 @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(META),side=meta>>1;if(mirror==Mirror.LEFT_RIGHT&&(side==2||side==3)||mirror==Mirror.FRONT_BACK&&(side==4||side==5))side^=1;return fromLegacy(side<<1|meta&1);}
 @Override protected VoxelShape getShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){var dir=direction(state.getValue(META));if(dir==null)return Shapes.empty();float a,b,c;switch(kind){case FLUORESCENT->{a=.5F;b=.5F;c=.1F;}case HALOGEN->{a=.35F;b=.25F;c=.2F;}default->{a=.25F;b=.2F;c=.15F;}}float x=a,y=b,z=c;if(dir.getAxis()==Direction.Axis.X){x=c;z=a;}else if(dir.getAxis()==Direction.Axis.Y){x=b;y=c;z=a;}float ox=.5F-dir.getStepX()*(.5F-x),oy=.5F-dir.getStepY()*(.5F-y),oz=.5F-dir.getStepZ()*(.5F-z);return Shapes.box(ox-x,oy-y,oz-z,ox+x,oy+y,oz+z);}
 @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){return Shapes.empty();}@Override protected int getLightBlock(BlockState state,BlockGetter world,BlockPos pos){return 0;}@Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter world,BlockPos pos){return true;}
 private static boolean supported(LevelReader world,BlockPos pos,Direction dir){if(dir==null)return false;var at=pos.relative(dir.getOpposite());var state=world.getBlockState(at);if(state.getBlock()instanceof SlabBlock){var half=state.getValue(SlabBlock.TYPE);return half==SlabType.DOUBLE||dir==(half==SlabType.TOP?Direction.UP:Direction.DOWN);}return state.isFaceSturdy(world,at,dir);}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){var side=context.getClickedFace();return supported(context.getLevel(),context.getClickedPos(),side)?fromLegacy(side.get3DDataValue()<<1):null;}
 private boolean updatePower(Level world,BlockPos pos,BlockState state){if(broken(state.getValue(META)))return false;boolean powered=world.hasNeighborSignal(pos);if(isOn&&powered){world.scheduleTick(pos,this,4);return true;}else if(!isOn&&!powered){world.setBlock(pos,on().fromLegacy(state.getValue(META)),2);return true;}return false;}
 private void updateBeam(Level world,BlockPos pos,BlockState state){if(isOn)propagate(world,pos,direction(state.getValue(META)),kind.beamLength,0);}
 @Override protected void onPlace(BlockState state,Level world,BlockPos pos,BlockState old,boolean moving){if(world.isClientSide||old.is(this))return;if(updatePower(world,pos,state))return;updateBeam(world,pos,state);}
 @Override protected void onRemove(BlockState state,Level world,BlockPos pos,BlockState next,boolean moving){if(!state.is(next.getBlock())&&!world.isClientSide)unpropagate(world,pos,direction(state.getValue(META)));super.onRemove(state,world,pos,next,moving);}
 @Override protected void tick(BlockState state,ServerLevel world,BlockPos pos,RandomSource random){if(isOn&&world.hasNeighborSignal(pos))world.setBlock(pos,off().fromLegacy(state.getValue(META)),2);}
 @Override protected void neighborChanged(BlockState state,Level world,BlockPos pos,Block neighbor,BlockPos from,boolean moving){if(world.isClientSide||neighbor instanceof SpotlightBeamBlock||neighbor==Blocks.AIR)return;var dir=direction(state.getValue(META));if(!supported(world,pos,dir)){popResource(world,pos,new ItemStack(on()));world.setBlock(pos,Blocks.AIR.defaultBlockState(),3);return;}if(updatePower(world,pos,state))return;updateBeam(world,pos,state);}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level world,BlockPos pos,Player player,BlockHitResult hit){if(!broken(state.getValue(META)))return InteractionResult.PASS;repair(world,pos);return InteractionResult.sidedSuccess(world.isClientSide);}
 private void repair(Level world,BlockPos pos){var state=world.getBlockState(pos);if(!state.is(this)||!broken(state.getValue(META)))return;world.setBlock(pos,on().fromLegacy(state.getValue(META)-1),2);for(var dir:Direction.values()){var at=pos.relative(dir);if(world.getBlockState(at).is(this))repair(world,at);}}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){Float radius=params.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);return radius!=null&&params.getLevel().random.nextFloat()>1F/radius?List.of():List.of(new ItemStack(on()));}@Override public ItemStack getCloneItemStack(LevelReader world,BlockPos pos,BlockState state){return new ItemStack(on());}
 public static void propagate(Level world,BlockPos start,Direction dir,int distance,int meta){if(dir==null)return;var pos=start;while(--distance>0){pos=pos.relative(dir);var state=world.getBlockState(pos);if(!state.isAir()&&!(state.getBlock()instanceof SourceBeamBlock))return;if(!(state.getBlock()instanceof SpotlightBeamBlock))world.setBlock(pos,SourceLightContent.BEAM.get().defaultBlockState().setValue(META,meta),3);if(SpotlightBeamBlock.setDirection(world,pos,dir,true)==0)return;}}
 public static void unpropagate(Level world,BlockPos start,Direction dir){if(dir==null)return;var pos=start;while(true){pos=pos.relative(dir);if(!(world.getBlockState(pos).getBlock()instanceof SpotlightBeamBlock))return;if(SpotlightBeamBlock.setDirection(world,pos,dir,false)==0)world.setBlock(pos,Blocks.AIR.defaultBlockState(),3);}}
 public static void backPropagate(Level world,BlockPos start,Direction dir,int meta){if(dir==null)return;var pos=start;while(true){pos=pos.relative(dir.getOpposite());var block=world.getBlockState(pos).getBlock();if(block instanceof SpotlightSource source)propagate(world,pos,dir,source.getBeamLength(),meta);else if(!(block instanceof SpotlightBeamBlock))return;}}
}
