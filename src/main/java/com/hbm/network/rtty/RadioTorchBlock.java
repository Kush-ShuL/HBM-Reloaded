package com.hbm.network.rtty;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;
/** Exact source six-face torch placement/support/no collision/quarter-wide selection and weak output only. */
public final class RadioTorchBlock extends BaseEntityBlock {
 public static final IntegerProperty LEGACY_META=IntegerProperty.create("legacy_meta",0,15);public static Consumer<RadioTorchEntity> OPEN_SCREEN=ignored->{};public final boolean sender;
 public RadioTorchBlock(boolean sender){super(Properties.of().strength(.1F,6).sound(SoundType.STONE).noOcclusion().noCollission());this.sender=sender;registerDefaultState(stateDefinition.any().setValue(LEGACY_META,1));}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(LEGACY_META);}
 public static Direction outward(BlockState state){int meta=state.getValue(LEGACY_META);return meta<6?Direction.from3DDataValue(meta):null;}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}@Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}@Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}@Override protected VoxelShape getOcclusionShape(BlockState state,BlockGetter level,BlockPos pos){return Shapes.empty();}
 @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Shapes.empty();}
 @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){int meta=state.getValue(LEGACY_META)&7;var dir=meta<6?Direction.from3DDataValue(meta):null;int x=dir==null?0:dir.getStepX(),y=dir==null?0:dir.getStepY(),z=dir==null?0:dir.getStepZ();return Shapes.box(x==1?0:.375,y==1?0:.375,z==1?0:.375,x==-1?1:.625,y==-1?1:.625,z==-1?1:.625);}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=defaultBlockState().setValue(LEGACY_META,context.getClickedFace().get3DDataValue());return state.canSurvive(context.getLevel(),context.getClickedPos())?state:null;}
 @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){var dir=outward(state);var support=dir==null?pos:pos.relative(dir.getOpposite());var target=level.getBlockState(support);return dir!=null&&target.isFaceSturdy(level,support,dir)||target.hasAnalogOutputSignal()||target.isSignalSource()||target.isSolidRender(level,support);}
 @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){if(!state.canSurvive(level,pos))level.destroyBlock(pos,true);}
 @Override protected boolean isSignalSource(BlockState state){return !sender;}@Override protected int getSignal(BlockState state,BlockGetter level,BlockPos pos,Direction side){return !sender&&level.getBlockEntity(pos) instanceof RadioTorchReceiverEntity receiver?receiver.lastState:0;}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(player.isShiftKeyDown())return InteractionResult.PASS;if(level.isClientSide&&level.getBlockEntity(pos) instanceof RadioTorchEntity radio)OPEN_SCREEN.accept(radio);return InteractionResult.sidedSuccess(level.isClientSide);}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return sender?new RadioTorchSenderEntity(pos,state):new RadioTorchReceiverEntity(pos,state);}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){if(level.isClientSide)return null;return sender?createTickerHelper(type,RadioTorchContent.SENDER_ENTITY.get(),RadioTorchSenderEntity::tick):createTickerHelper(type,RadioTorchContent.RECEIVER_ENTITY.get(),RadioTorchReceiverEntity::tick);}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder builder){return List.of(new ItemStack(this));}
}
