package api.hbm.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** X5846 insertion contract: success does not consume the caller's stack. */
public interface IInsertable {
    boolean insertItem(Level level,BlockPos pos,Direction direction,ItemStack stack);
}
