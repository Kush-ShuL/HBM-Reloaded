package com.hbm.machine.reactor.rbmk.thermal;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
public final class RbmkThermalBlock extends RbmkColumnBlock {
    private final boolean heater;public RbmkThermalBlock(boolean heater){this.heater=heater;}
    @Override protected MapCodec<? extends RbmkThermalBlock> codec(){return MapCodec.unit(this);}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?heater?new RbmkHeaterEntity(pos,state):new RbmkCoolerEntity(pos,state):isPort(state)?new RbmkThermalPort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){if(!isCore(state))return null;return heater?createTickerHelper(type,RbmkThermalContent.HEATER_ENTITY.get(),RbmkHeaterEntity::tick):createTickerHelper(type,RbmkThermalContent.COOLER_ENTITY.get(),RbmkCoolerEntity::tick);}
}
