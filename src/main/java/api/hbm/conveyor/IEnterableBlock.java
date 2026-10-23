package api.hbm.conveyor;
import net.minecraft.world.level.Level;import net.minecraft.core.Direction;
/** A null direction is original ForgeDirection.UNKNOWN for diagonal transitions. */
public interface IEnterableBlock {boolean canItemEnter(Level level,int x,int y,int z,Direction side,IConveyorItem item);void onItemEnter(Level level,int x,int y,int z,Direction side,IConveyorItem item);boolean canPackageEnter(Level level,int x,int y,int z,Direction side,IConveyorPackage pack);void onPackageEnter(Level level,int x,int y,int z,Direction side,IConveyorPackage pack);}
