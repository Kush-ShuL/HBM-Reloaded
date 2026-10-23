package com.hbm.block.gas;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/** Client implementation is installed without loading client classes on a server. */
public final class GasVisuals {
    @FunctionalInterface public interface Display{void display(Level level,BlockPos pos,GasKind kind,RandomSource random);}
    private static Display display=(level,pos,kind,random)->{};
    private GasVisuals(){}
    public static void install(Display display){GasVisuals.display=java.util.Objects.requireNonNull(display);}
    public static void display(Level level,BlockPos pos,GasKind kind,RandomSource random){display.display(level,pos,kind,random);}
}
