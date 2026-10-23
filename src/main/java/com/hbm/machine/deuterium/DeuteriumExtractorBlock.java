package com.hbm.machine.deuterium;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
public final class DeuteriumExtractorBlock extends BaseEntityBlock {
 public DeuteriumExtractorBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());}
 @Override protected MapCodec<? extends DeuteriumExtractorBlock> codec(){return MapCodec.unit(this);}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new DeuteriumEntity(pos,state);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(net.minecraft.world.level.Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,DeuteriumContent.EXTRACTOR_ENTITY.get(),DeuteriumEntity::tick);}
}
