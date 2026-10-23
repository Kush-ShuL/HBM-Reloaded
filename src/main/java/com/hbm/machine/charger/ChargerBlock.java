package com.hbm.machine.charger;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.*;

/** Source Charger: wall-mounted collision and one final yaw state on placement. */
public final class ChargerBlock extends BaseEntityBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    public static final MapCodec<ChargerBlock> CODEC=simpleCodec(properties->new ChargerBlock());
    public ChargerBlock(){super(Properties.of().strength(5,6).noOcclusion().requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){int yaw=Mth.floor(context.getRotation()*4F/360F+.5D)&3;return defaultBlockState().setValue(META,new int[]{2,5,3,4}[yaw]);}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext collision){return switch(state.getValue(META)){
        case 2->box(5,4,12,11,12,16);case 3->box(5,4,0,11,12,4);case 4->box(12,4,5,16,12,11);case 5->box(0,4,5,4,12,11);default->box(5,4,5,11,12,11);};}
    public static Direction port(BlockState state){int meta=state.getValue(META);return meta<6?Direction.from3DDataValue(meta).getOpposite():null;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new ChargerBlockEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,ChargerContent.ENTITY.get(),ChargerBlockEntity::tick);}
}
