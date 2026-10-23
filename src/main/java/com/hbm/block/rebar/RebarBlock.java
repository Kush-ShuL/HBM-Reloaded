package com.hbm.block.rebar;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** Original full collision block with transparent12-rod rendering and genuine curing/network BE. */
public final class RebarBlock extends BaseEntityBlock {
 public RebarBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(15,12).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
 @Override protected MapCodec<? extends RebarBlock> codec(){return simpleCodec(p->new RebarBlock());}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new RebarBlockEntity(pos,state);}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,RebarContent.ENTITY.get(),RebarBlockEntity::tick);}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
 @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block block,BlockPos from,boolean moving){if(level.getBlockEntity(pos) instanceof RebarBlockEntity machine)machine.updateConnection();}
 @Override protected boolean skipRendering(BlockState state,BlockState adjacent,Direction side){return false;}@Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}@Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
}
