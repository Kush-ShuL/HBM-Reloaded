package com.hbm.door.sourceblast;

import com.hbm.interfaces.IBomb;
import com.hbm.item.lock.PadlockItem;
import com.hbm.machine.storage.LockableBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;

/** Original BlastDoor, not BlockDoorGeneric/SLIDE_DOOR. All seven occupied cells retain source full-cube collision. */
public final class SourceBlastDoorBlock extends BaseEntityBlock implements IBomb {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    public SourceBlastDoorBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(10,600).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.INVISIBLE;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new SourceBlastDoorEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,SourceBlastDoorContent.ENTITY.get(),SourceBlastDoorEntity::tick);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return context.getLevel().isClientSide||SourceBlastDoorContent.neighborsReady(context.getLevel(),context.getClickedPos())?defaultBlockState():null;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){
        if(level.isClientSide)return;int quadrant=net.minecraft.util.Mth.floor(placer.getYRot()*4F/360F+.5D)&3;
        level.setBlock(pos,state.setValue(META,quadrant%2==0?2:3),2);
        if(level.getBlockEntity(pos)instanceof SourceBlastDoorEntity door)door.beginPlacement();
    }
    static boolean yieldsToItem(Player player){var held=player.getMainHandItem();return held.getItem()instanceof PadlockItem||LockableBlockEntity.isItem(held,"key_kit");}
    private InteractionResult activate(Level level,BlockPos pos,Player player){
        if(level.isClientSide)return InteractionResult.SUCCESS;
        if(yieldsToItem(player)||player.isShiftKeyDown())return InteractionResult.PASS;
        if(level.getBlockEntity(pos)instanceof SourceBlastDoorEntity door&&(!door.isLocked()||door.canAccess(player)))door.tryToggle();
        return InteractionResult.CONSUME;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return activate(level,pos,player);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        // Modern split item/block interaction must let the original padlock/key-kit item execute on the client as well.
        if(stack.getItem()instanceof PadlockItem||LockableBlockEntity.isItem(stack,"key_kit"))return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        return activate(level,pos,player).consumesAction()?ItemInteractionResult.sidedSuccess(level.isClientSide):ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }
    @Override public BombReturnCode explode(Level level,int x,int y,int z){
        if(!level.isClientSide&&level.getBlockEntity(new BlockPos(x,y,z))instanceof SourceBlastDoorEntity door){if(!door.isLocked()){door.tryToggle();return BombReturnCode.TRIGGERED;}return BombReturnCode.ERROR_INCOMPATIBLE;}return BombReturnCode.UNDEFINED;
    }
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
    @Override protected VoxelShape getOcclusionShape(BlockState state,BlockGetter level,BlockPos pos){return Shapes.empty();}
}
