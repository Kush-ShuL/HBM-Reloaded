package com.hbm.network.fluidcontrols;
import com.hbm.block.pipe.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
/** Reuses the one actual duct-node/type/NBT/copy lifecycle. Powered=connected, unlike manual fluid_valve. */
public final class FluidSwitchEntity extends NativePipeBlockEntity {
 public FluidSwitchEntity(BlockPos pos,BlockState state){super(FluidControlsContent.SWITCH_ENTITY.get(),pos,state);}
 @Override public PipeKind kind(){return PipeKind.VALVE;}
}
