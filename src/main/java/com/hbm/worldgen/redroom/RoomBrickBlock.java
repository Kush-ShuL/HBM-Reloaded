package com.hbm.worldgen.redroom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** Original side==metadata face selection, including the all-black metadata6. */
public class RoomBrickBlock extends Block {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    public RoomBrickBlock(){super(Properties.of().strength(0,6000).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){
        var player=context.getPlayer();var pos=context.getClickedPos();Direction facing=context.getHorizontalDirection().getOpposite();
        if(player!=null&&Math.abs(player.getX()-pos.getX())<2&&Math.abs(player.getZ()-pos.getZ())<2){if(player.getEyeY()-pos.getY()>2)facing=Direction.UP;else if(pos.getY()-player.getEyeY()>0)facing=Direction.DOWN;}
        return defaultBlockState().setValue(META,facing.get3DDataValue());
    }
    @Override protected BlockState rotate(BlockState state,Rotation rotation){int meta=state.getValue(META);return meta<6?state.setValue(META,rotation.rotate(Direction.from3DDataValue(meta)).get3DDataValue()):state;}
    @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(META);return meta<6?state.setValue(META,mirror.mirror(Direction.from3DDataValue(meta)).get3DDataValue()):state;}
}
