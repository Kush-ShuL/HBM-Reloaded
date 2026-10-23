package api.hbm.block;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
/** Original common contract for blocks and tile entities accepting DFC laser SPK. */
public interface ILaserable {void addEnergy(Level level,int x,int y,int z,long energy,Direction direction);}
