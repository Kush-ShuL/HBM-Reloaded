package api.hbm.item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
/** Source target readiness/coordinates contract, for pads and any actual target-designating item. */
public interface IDesignatorItem {
    boolean isReady(Level world,ItemStack stack,int x,int y,int z);
    Vec3 getCoords(Level world,ItemStack stack,int x,int y,int z);
}
