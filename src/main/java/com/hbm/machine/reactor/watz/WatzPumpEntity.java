package com.hbm.machine.reactor.watz;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Source canUpdate=false: no fabricated processing or GUI. */
public final class WatzPumpEntity extends BlockEntity {public WatzPumpEntity(BlockPos pos,BlockState state){super(WatzContent.PUMP_ENTITY.get(),pos,state);}}
