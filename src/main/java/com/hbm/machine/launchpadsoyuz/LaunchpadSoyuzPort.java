package com.hbm.machine.launchpadsoyuz;

import com.hbm.multiblock.proxy.SourceStandaloneProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Genuine source .power().fluid().inventory() Combo; sole114 dispatcher/storage/network delegation, no copied proxy backend. */
public final class LaunchpadSoyuzPort extends SourceStandaloneProxy {
    public LaunchpadSoyuzPort(BlockPos pos,BlockState state){super(LaunchpadSoyuzContent.PORT.get(),pos,state);}
}
