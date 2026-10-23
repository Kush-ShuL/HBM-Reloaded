package api.hbm.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

/** X5846 IBlowable: the first blowable obstruction receives one server callback per powered tick. */
public interface IBlowable {
    void applyFan(Level level, BlockPos pos, Direction direction, int distance);
}
