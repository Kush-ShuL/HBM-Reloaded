package com.hbm.machine.radiationcleanup;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.MapColor;

/** Original ordinary cube with a different top sprite, not an invented chamber/GUI/multiblock. */
public final class DeconBlock extends BaseEntityBlock {
    public DeconBlock(){this(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());}
    private DeconBlock(Properties properties){super(properties);}
    @Override protected MapCodec<? extends DeconBlock> codec(){return simpleCodec(DeconBlock::new);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new DeconBlockEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,RadiationCleanupContent.DECON_ENTITY.get(),DeconBlockEntity::tick);}
}
