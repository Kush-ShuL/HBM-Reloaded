package com.hbm.bomb.nuclear;

/** Native terrain workers sharing the nuclear server-tick budget. */
public interface NuclearWork {
    default boolean hasNuclearWork() { return true; }
}
