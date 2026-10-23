package com.hbm.machine.hexafluoride;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;

/** Actual retained MachineUF6Tank/MachinePuF6Tank: empty source BE, full-cube physics, no survival drop. */
public final class HexafluorideTankBlock extends BaseEntityBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    public final boolean plutonium;
    public HexafluorideTankBlock(boolean plutonium){
        super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE)
                .requiresCorrectToolForDrops().noLootTable().noOcclusion()
                .isSuffocating((s,w,p)->false).isViewBlocking((s,w,p)->false));
        this.plutonium=plutonium;registerDefaultState(stateDefinition.any().setValue(META,0));
    }
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){
        int yaw=Mth.floor(context.getRotation()*4F/360F+.5D)&3;
        return defaultBlockState().setValue(META,switch(yaw){case 0->2;case 1->5;case 2->3;default->4;});
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new HexafluorideTankEntity(pos,state);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
    @Override protected int getLightBlock(BlockState state,BlockGetter world,BlockPos pos){return 0;}
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter world,BlockPos pos){return true;}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){int meta=state.getValue(META);return meta>=2&&meta<=5?state.setValue(META,rotation.rotate(Direction.from3DDataValue(meta)).get3DDataValue()):state;}
    @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(META);return meta>=2&&meta<=5?state.setValue(META,mirror.mirror(Direction.from3DDataValue(meta)).get3DDataValue()):state;}
}
