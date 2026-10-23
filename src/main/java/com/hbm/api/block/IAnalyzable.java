package com.hbm.api.block;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/** Source IAnalyzable: a block may return actual diagnostic text, or null when no data is available. */
public interface IAnalyzable {
    /** Native merged block classes can retain a source subclass that never implemented IAnalyzable. */
    default boolean supportsAnalysis(){return true;}
    List<String> getDebugInfo(Level level,BlockPos position);
}
