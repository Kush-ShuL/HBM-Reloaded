package com.hbm.machine.tapedrive;

import com.mojang.serialization.MapCodec;
import java.util.Random;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
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

/** Source ordinary rotatable BlockMachineBase, not a Dummyable block. All16 source metadata remain valid. */
public final class TapeDriveBlock extends BaseEntityBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    private final Random dropRandom=new Random();
    public TapeDriveBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends TapeDriveBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return switch(state.getValue(META)){case 5->box(0,0,0,12,16,16);case 3->box(0,0,0,16,16,12);case 4->box(4,0,0,16,16,16);case 2->box(0,0,4,16,16,16);default->Shapes.block();};}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return getShape(state,level,pos,context);}
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,context.getHorizontalDirection().getOpposite().get3DDataValue());}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){int meta=state.getValue(META);return meta>=2&&meta<=5?state.setValue(META,rotation.rotate(Direction.from3DDataValue(meta)).get3DDataValue()):state;}
    @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(META);return meta>=2&&meta<=5?rotate(state,mirror.getRotation(Direction.from3DDataValue(meta))):state;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new TapeDriveEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,TapeDriveContent.ENTITY.get(),TapeDriveEntity::tick);}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(stack.has(DataComponents.CUSTOM_NAME)&&level.getBlockEntity(pos) instanceof TapeDriveEntity machine)machine.setCustomName(stack.getHoverName());}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(player.isShiftKeyDown())return InteractionResult.PASS;if(level.isClientSide)return InteractionResult.SUCCESS;if(player instanceof ServerPlayer server&&level.getBlockEntity(pos) instanceof TapeDriveEntity machine)server.openMenu(machine);return InteractionResult.CONSUME;}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){
        if(state.is(next.getBlock()))return;
        if(!level.isClientSide&&level.getBlockEntity(pos) instanceof TapeDriveEntity machine){
            for(int slot=0;slot<12;slot++){
                ItemStack stack=machine.getItem(slot);if(stack.isEmpty())continue;
                float x=dropRandom.nextFloat()*.8F+.1F,y=dropRandom.nextFloat()*.8F+.1F,z=dropRandom.nextFloat()*.8F+.1F;
                while(!stack.isEmpty()){
                    ItemStack drop=stack.split(Math.min(dropRandom.nextInt(21)+10,stack.getCount()));
                    ItemEntity item=new ItemEntity(level,pos.getX()+x,pos.getY()+y,pos.getZ()+z,drop);
                    item.setDeltaMovement((float)dropRandom.nextGaussian()*.05F,(float)dropRandom.nextGaussian()*.05F+.2F,(float)dropRandom.nextGaussian()*.05F);level.addFreshEntity(item);
                }
            }
            level.updateNeighbourForOutputSignal(pos,this);
        }
        super.onRemove(state,level,pos,next,moving);
    }
}
