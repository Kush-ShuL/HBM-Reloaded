package com.hbm.machine.pistoninserter;

import com.hbm.conveyor.crane.CraneBaseBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
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

/** Original registered hidden PistonInserter, full collision and four solid sides around its insertion axis. */
public final class PistonInserterBlock extends BaseEntityBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    private static final VoxelShape X_SUPPORT=Shapes.or(Block.box(0,0,0,16,.25,16),Block.box(0,15.75,0,16,16,16),Block.box(0,0,0,16,16,.25),Block.box(0,0,15.75,16,16,16));
    private static final VoxelShape Y_SUPPORT=Shapes.or(Block.box(0,0,0,.25,16,16),Block.box(15.75,0,0,16,16,16),Block.box(0,0,0,16,16,.25),Block.box(0,0,15.75,16,16,16));
    private static final VoxelShape Z_SUPPORT=Shapes.or(Block.box(0,0,0,.25,16,16),Block.box(15.75,0,0,16,16,16),Block.box(0,0,0,16,.25,16),Block.box(0,15.75,0,16,16,16));
    public PistonInserterBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
    public static Direction direction(BlockState state){int meta=state.getValue(META);return meta<6?Direction.from3DDataValue(meta):null;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,context.getPlayer()==null?0:CraneBaseBlock.placement(context.getClickedPos(),context.getPlayer()));}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new PistonInserterEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,PistonInserterContent.ENTITY.get(),PistonInserterEntity::tick);}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){if(level.getBlockEntity(pos)instanceof PistonInserterEntity piston)piston.neighborState();}
    private static boolean front(BlockState state,BlockHitResult hit){var direction=direction(state);return direction!=null&&direction==hit.getDirection();}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(!front(state,hit)||player.isShiftKeyDown()||stack.isEmpty())return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if(!level.isClientSide&&level.getBlockEntity(pos)instanceof PistonInserterEntity piston&&piston.slot.isEmpty()){piston.slot=stack.split(1);player.inventoryMenu.broadcastChanges();}
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){
        if(!front(state,hit)||!player.isShiftKeyDown())return InteractionResult.PASS;
        if(!level.isClientSide&&level.getBlockEntity(pos)instanceof PistonInserterEntity piston&&!piston.slot.isEmpty()&&piston.isRetracting){var direction=direction(state);var entity=new ItemEntity(level,pos.getX()+.5+direction.getStepX()*.75,pos.getY()+.5+direction.getStepY()*.75,pos.getZ()+.5+direction.getStepZ()*.75,piston.slot);piston.slot=ItemStack.EMPTY;entity.setDeltaMovement(direction.getStepX()*.25,direction.getStepY()*.25,direction.getStepZ()*.25);level.addFreshEntity(entity);}
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
    @Override protected VoxelShape getOcclusionShape(BlockState state,BlockGetter level,BlockPos pos){return Shapes.empty();}
    /** Nonphysical support-only thin walls: four complete sturdy faces; open axis fails FULL/CENTER/RIGID support. */
    @Override protected VoxelShape getBlockSupportShape(BlockState state,BlockGetter level,BlockPos pos){var direction=direction(state);return direction==null?Shapes.block():switch(direction.getAxis()){case X->X_SUPPORT;case Y->Y_SUPPORT;case Z->Z_SUPPORT;};}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){
        if(!state.is(next.getBlock())&&level.getBlockEntity(pos)instanceof PistonInserterEntity piston&&!piston.slot.isEmpty()){
            float x=level.random.nextFloat()*.8F+.1F,y=level.random.nextFloat()*.8F+.1F,z=level.random.nextFloat()*.8F+.1F;
            while(!piston.slot.isEmpty()){int count=Math.min(level.random.nextInt(21)+10,piston.slot.getCount());var entity=new ItemEntity(level,pos.getX()+x,pos.getY()+y,pos.getZ()+z,piston.slot.split(count));float speed=.05F;entity.setDeltaMovement((float)level.random.nextGaussian()*speed,(float)level.random.nextGaussian()*speed+.2F,(float)level.random.nextGaussian()*speed);level.addFreshEntity(entity);}level.updateNeighbourForOutputSignal(pos,this);
        }super.onRemove(state,level,pos,next,moving);
    }
}
