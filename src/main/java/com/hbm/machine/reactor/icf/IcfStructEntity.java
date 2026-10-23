package com.hbm.machine.reactor.icf;
import net.minecraft.core.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Exact407 cbarp checks in source order, once each20ticks, then actual MachineICF formation. */
public final class IcfStructEntity extends BlockEntity{
 public IcfStructEntity(BlockPos pos,BlockState state){super(IcfReactorContent.STRUCT_ENTITY.get(),pos,state);}
 public static void tick(Level level,BlockPos pos,BlockState state,IcfStructEntity struct){if(level.isClientSide||level.getGameTime()%20!=0)return;var dir=Direction.from3DDataValue(state.getValue(IcfStructBlock.META));for(var cell:IcfStructureProfile.CELLS){var target=IcfStructureProfile.position(pos,dir,cell);var block=level.getBlockState(target);if(!block.is(IcfReactorContent.COMPONENT.get())||block.getValue(IcfMaterialBlock.META)!=cell.material())return;}IcfReactorContent.REACTOR.get().assembleAt(level,pos,dir);}
}
