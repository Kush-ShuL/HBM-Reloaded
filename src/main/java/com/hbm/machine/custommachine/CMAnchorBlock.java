package com.hbm.machine.custommachine;
import net.minecraft.core.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.MapColor;
/** Actual positioning anchor, full ordinary cube and source yaw metadata, no BE. */
public final class CMAnchorBlock extends Block {
    public CMAnchorBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(CMComponentBlock.META,0));}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(CMComponentBlock.META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(CMComponentBlock.META,context.getHorizontalDirection().getOpposite().get3DDataValue());}
}
