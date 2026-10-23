package com.hbm.machine.reactor.watz;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Every20worldticks checks36elements,26coolers and48actuallyboltedcasings, then source symmetric NORTH formation. */
public final class WatzStructEntity extends BlockEntity {
    public WatzStructEntity(BlockPos pos,BlockState state){super(WatzContent.STRUCT_ENTITY.get(),pos,state);}
    public static void tick(Level level,BlockPos pos,BlockState state,WatzStructEntity entity){if(level.isClientSide||level.getGameTime()%20!=0)return;for(var part:WatzLayout.COMPONENTS){var at=part.at(pos);if(!level.hasChunkAt(at))return;var actual=level.getBlockState(at);var required=part.state();if(!actual.is(required.getBlock())||part.kind()==2&&actual.getValue(WatzCasing.META)!=1)return;}WatzContent.REACTOR.get().form(level,pos);}
}
