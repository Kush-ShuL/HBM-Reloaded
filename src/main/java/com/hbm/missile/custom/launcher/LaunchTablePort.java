package com.hbm.missile.custom.launcher;

import com.hbm.api.computer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/** Only the real large table exposes its original six computer methods through its actual ports. */
public final class LaunchTablePort extends CustomLauncherPort implements ComputerControl {
    public LaunchTablePort(BlockPos pos, BlockState state) { super(CustomLauncherContent.TABLE_PORT.get(), pos, state); }
    @Override public String computerType() { return "ntm_custom_launch_pad"; }
    @Override public String[] computerMethods() { var core = core(); return core instanceof LaunchTableEntity table ? table.computerMethods() : new String[0]; }
    @Override public Object[] callComputer(String method, ComputerArguments arguments) { var core = core(); if (core instanceof LaunchTableEntity table) return table.callComputer(method, arguments); throw new ComputerException("Machine is not loaded"); }
    @Override public boolean canComputerConnect(Direction side) { return core() instanceof LaunchTableEntity; }
}
