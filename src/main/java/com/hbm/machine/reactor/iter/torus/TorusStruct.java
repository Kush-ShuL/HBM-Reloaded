package com.hbm.machine.reactor.iter.torus;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** Actual luminous BlockFusionTorusStruct. */
public final class TorusStruct extends BaseEntityBlock {
    public TorusStruct(){super(Properties.of().strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion().lightLevel(state->15));}
    @Override protected MapCodec<? extends TorusStruct> codec(){return MapCodec.unit(this);}@Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}@Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new TorusStructEntity(pos,state);}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,TorusContent.STRUCT_ENTITY.get(),TorusStructEntity::tick);}
}
