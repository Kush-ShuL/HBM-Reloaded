package com.hbm.machine.reactor.rbmk.boiler;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
public final class RbmkBoilerBlock extends RbmkColumnBlock {
    @Override protected MapCodec<? extends RbmkBoilerBlock> codec(){return MapCodec.unit(this);}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new RbmkBoilerEntity(pos,state):isPort(state)?new RbmkBoilerProxy(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,RbmkBoilerContent.ENTITY.get(),RbmkBoilerEntity::tick):null;}
}
