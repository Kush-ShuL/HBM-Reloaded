package com.hbm.compat.computercraft.cable;
/** Optional computer linkage boundary; ordinary block entities do not resolve either computer API. */
public interface PaintableCableNetwork {
    void tick();
    void changed();
    void close();
    default void load(net.minecraft.nbt.CompoundTag tag) {}
    default void save(net.minecraft.nbt.CompoundTag tag) {}
}
