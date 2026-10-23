package com.hbm.block.specialvehicle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;

/** Original DecoBlock branches actually applicable to boxcar/boat: solid one-cell collision, four placement headings, no screwdriver action. */
public final class VehicleDecoBlock extends BaseEntityBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    public VehicleDecoBlock(){this(Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(10,6).requiresCorrectToolForDrops().noOcclusion());}
    private VehicleDecoBlock(Properties properties){super(properties);registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends VehicleDecoBlock> codec(){return simpleCodec(VehicleDecoBlock::new);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){int yaw=Mth.floor(context.getRotation()*4F/360F+.5D)&3;return defaultBlockState().setValue(META,switch(yaw){case 0->3;case 1->4;case 2->2;default->5;});}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new VehicleDecoBlockEntity(pos,state);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
    @Override protected boolean propagatesSkylightDown(BlockState state,net.minecraft.world.level.BlockGetter world,BlockPos pos){return true;}
    @Override protected int getLightBlock(BlockState state,net.minecraft.world.level.BlockGetter world,BlockPos pos){return 0;}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){int meta=state.getValue(META);if(meta<2||meta>5)return state;return state.setValue(META,rotation.rotate(net.minecraft.core.Direction.from3DDataValue(meta)).get3DDataValue());}
    @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(META);if(meta<2||meta>5)return state;return state.setValue(META,mirror.mirror(net.minecraft.core.Direction.from3DDataValue(meta)).get3DDataValue());}
}
