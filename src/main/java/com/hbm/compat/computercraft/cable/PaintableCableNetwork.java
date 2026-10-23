package com.hbm.compat.computercraft.cable;
/** Optional CC linkage boundary; no CC class is resolved by the ordinary block/entity definitions. */
public interface PaintableCableNetwork {
    void tick();
    void changed();
    void close();
}
