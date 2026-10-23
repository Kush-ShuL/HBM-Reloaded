package com.hbm.api.computer;

import net.minecraft.core.Direction;

/** Machine control contract without a dependency on the optional computer mod. */
public interface ComputerControl {
    String computerType();

    String[] computerMethods();

    Object[] callComputer(String method, ComputerArguments arguments) throws ComputerException;

    default boolean canComputerConnect(Direction side) {
        return true;
    }
}
