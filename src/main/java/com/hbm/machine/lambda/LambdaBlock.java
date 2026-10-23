package com.hbm.machine.lambda;

import com.hbm.multiblock.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public final class LambdaBlock extends DummyableBlock {
    public LambdaBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return simpleCodec(properties->new LambdaBlock());}
    @Override public int[] dimensions(){return LambdaLayout.DIMENSIONS[0].clone();}
    @Override public int offset(){return 7;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){
        var facing=context.getHorizontalDirection().getOpposite();var core=coreForPlacement(context);
        for(var p:LambdaLayout.parts(core,facing))if(!context.getLevel().isInWorldBounds(p)||!context.getLevel().hasChunkAt(p)||!p.equals(context.getClickedPos())&&!context.getLevel().getBlockState(p).canBeReplaced())return null;
        return defaultBlockState().setValue(LEGACY_META,facing.get3DDataValue()+(core.equals(context.getClickedPos())?10:0));
    }
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){
        if(level.isClientSide)return;var facing=placementFacing(state);var core=pos.relative(facing,-offset());
        safely(()->{if(!pos.equals(core))level.removeBlock(pos,false);level.setBlock(core,defaultBlockState().setValue(LEGACY_META,facing.get3DDataValue()+10),UPDATE_ALL);
            for(var p:LambdaLayout.parts(core,facing))if(!p.equals(core)){var away=p.getY()<core.getY()?Direction.DOWN:p.getY()>core.getY()?Direction.UP:p.getX()<core.getX()?Direction.WEST:p.getX()>core.getX()?Direction.EAST:p.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH;level.setBlock(p,defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),UPDATE_ALL);}
            for(var p:LambdaLayout.ports(core,facing))setPort(level,p,true);
        });
        if(stack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME)&&level.getBlockEntity(core) instanceof NamedMultiblock named)named.setCustomName(stack.getHoverName());
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new LambdaEntity(pos,state):isPort(state)?new LambdaPort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,LambdaContent.ENTITY.get(),LambdaEntity::tick):null;}
}
