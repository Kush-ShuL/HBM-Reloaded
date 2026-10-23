package com.hbm.world.structure.templates.radio;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
/** Original RadioRec: all raw metadata, final source yaw placement, exact source bounds and GUI-only client activation. */
public final class RadioReceiverBlock extends BaseEntityBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    private static final VoxelShape NORMAL=box(0,0,2,16,16,14),NS=box(1,0,4,15,10,12),EW=box(4,0,1,12,10,15);
    public RadioReceiverBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5F,6F).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion().isSuffocating((s,w,p)->false).isViewBlocking((s,w,p)->false).isValidSpawn((s,w,p,t)->false));registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){return switch(state.getValue(META)){case 2,3->NS;case 4,5->EW;default->NORMAL;};}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){return getShape(state,world,pos,context);}
    @Override protected int getLightBlock(BlockState state,BlockGetter world,BlockPos pos){return 0;}
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter world,BlockPos pos){return true;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){int yaw=Mth.floor(context.getRotation()*4F/360F+.5D)&3;return defaultBlockState().setValue(META,switch(yaw){case 0->3;case 1->4;case 2->2;default->5;});}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new RadioReceiverBlockEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,RadioReceiverContent.ENTITY.get(),(world,pos,block,tile)->tile.tickServer());}
    private InteractionResult open(Level world,BlockPos pos,Player player){if(player.isShiftKeyDown())return InteractionResult.PASS;if(world.isClientSide&&world.getBlockEntity(pos) instanceof RadioReceiverBlockEntity tile)RadioReceiverClientBridge.open.accept(tile);return InteractionResult.sidedSuccess(world.isClientSide);}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level world,BlockPos pos,Player player,BlockHitResult hit){return open(world,pos,player);}
    @Override protected ItemInteractionResult useItemOn(ItemStack item,BlockState state,Level world,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return open(world,pos,player)==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(world.isClientSide);}
}
