package com.hbm.machine.pinkcloud;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.*;

/** Source PinkCloudBroadcaster: original metadata, yaw mapping and narrow casing collision. */
public final class PinkCloudBroadcasterBlock extends BaseEntityBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    private static final MapCodec<PinkCloudBroadcasterBlock> CODEC=simpleCodec(ignored->new PinkCloudBroadcasterBlock());
    private static final VoxelShape X=box(1,0,4,15,10,12),Z=box(4,0,1,12,10,15),DEFAULT=box(0,0,2,16,16,14);
    public PinkCloudBroadcasterBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,9).requiresCorrectToolForDrops().noOcclusion());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){int index=net.minecraft.util.Mth.floor(context.getRotation()*4F/360F+.5D)&3;return defaultBlockState().setValue(META,new int[]{3,4,2,5}[index]);}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){int meta=state.getValue(META);return meta<2?state:state.setValue(META,rotation.rotate(net.minecraft.core.Direction.from3DDataValue(meta)).get3DDataValue());}
    @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(META);return meta<2?state:rotate(state,mirror.getRotation(net.minecraft.core.Direction.from3DDataValue(meta)));}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return switch(state.getValue(META)){case 2,3->X;case 4,5->Z;default->DEFAULT;};}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new PinkCloudBroadcasterBlockEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,PinkCloudBroadcasterContent.ENTITY.get(),PinkCloudBroadcasterBlockEntity::tick);}
}
