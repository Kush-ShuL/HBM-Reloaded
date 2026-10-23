package com.hbm.machine.converter;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Original two normal single-texture cubes, without a GUI, facing or invented computer interface. */
public final class ConverterBlock extends BaseEntityBlock {
    public final boolean heToRf;
    private final MapCodec<ConverterBlock> codec;
    public ConverterBlock(boolean heToRf){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());this.heToRf=heToRf;codec=simpleCodec(ignored->new ConverterBlock(heToRf));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return codec;}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return heToRf?new HeToRfConverter(pos,state):new RfToHeConverter(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,ConverterContent.ENTITY.get(),ConverterBlockEntity::tick);}
}
