package com.hbm.machine.reactor.rbmk.irradiation;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
public final class RbmkOutgasserBlock extends RbmkColumnBlock {
    @Override protected MapCodec<? extends RbmkOutgasserBlock> codec(){return MapCodec.unit(this);}@Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new RbmkOutgasserEntity(pos,state):isPort(state)?new RbmkOutgasserPort(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,RbmkOutgasserContent.ENTITY.get(),RbmkOutgasserEntity::tick):null;}
}
