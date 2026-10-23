package com.hbm.world.structure.cabinet;
import com.hbm.item.lock.PadlockItem;
import com.hbm.machine.storage.CrateContents;
import com.hbm.machine.storage.LockableBlockEntity;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
/** Original BlockDecoModel rotation bits, physical bounds and BlockDecoContainer activation/ejection. */
public final class FilingCabinetBlock extends BaseEntityBlock {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 private static final VoxelShape[] SHAPES={box(3,0,4,13,16,16),box(3,0,0,13,16,12),box(4,0,3,16,16,13),box(0,0,3,12,16,13)};
 public FilingCabinetBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(10,9).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 public BlockState fromLegacy(int metadata){return defaultBlockState().setValue(META,metadata&15);}
 public static int transformMeta(int meta,int turns){int rot=meta>>2,type=meta&3;switch(turns&3){case 1->rot^=rot<2?3:2;case 2->rot^=1;case 3->rot^=rot<2?2:3;default->{}}return rot<<2|type;}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){int yaw=Mth.floor(context.getRotation()*4/360+.5)&3;int rot=(yaw&1)==0?yaw>>1:yaw==3?2:3;return fromLegacy((rot<<2)|FilingCabinetItem.metadata(context.getItemInHand()));}
 @Override protected BlockState rotate(BlockState state,Rotation rotation){return fromLegacy(transformMeta(state.getValue(META),switch(rotation){case NONE->0;case CLOCKWISE_90->1;case CLOCKWISE_180->2;case COUNTERCLOCKWISE_90->3;}));}
 @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(META),rot=meta>>2;if(mirror==Mirror.LEFT_RIGHT&&rot<2||mirror==Mirror.FRONT_BACK&&rot>=2)rot^=1;return fromLegacy(rot<<2|meta&3);}
 @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return SHAPES[state.getValue(META)>>2];}
 @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}@Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new FilingCabinetBlockEntity(pos,state);}
 @Override public <T extends BlockEntity>BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,FilingCabinetContent.ENTITY.get(),FilingCabinetBlockEntity::tick);}
 private InteractionResult open(Level level,BlockPos pos,Player player){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(level.getBlockEntity(pos)instanceof FilingCabinetBlockEntity cabinet&&cabinet.canAccess(player)&&player instanceof ServerPlayer server){server.openMenu(cabinet,buffer->buffer.writeBlockPos(pos));return InteractionResult.CONSUME;}return InteractionResult.PASS;}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return open(level,pos,player);}
 @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){if(stack.getItem()instanceof PadlockItem||LockableBlockEntity.isItem(stack,"key_kit"))return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;return open(level,pos,player)==InteractionResult.PASS?ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);}
 @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moved){if(!state.is(next.getBlock())&&level.getBlockEntity(pos)instanceof FilingCabinetBlockEntity cabinet){CrateContents.ejectWorld(level,pos.getX(),pos.getY(),pos.getZ(),cabinet);level.updateNeighbourForOutputSignal(pos,this);}super.onRemove(state,level,pos,next,moved);}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){Float radius=context.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);if(radius!=null&&context.getLevel().random.nextFloat()>1F/radius)return List.of();return List.of(FilingCabinetItem.stack(state.getValue(META)&3));}
 @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return FilingCabinetItem.stack(state.getValue(META)&3);}
}
