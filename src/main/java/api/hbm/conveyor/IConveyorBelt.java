package api.hbm.conveyor;
import net.minecraft.world.level.Level;import net.minecraft.world.phys.Vec3;
/** Original belt movement/snapping contract, not a parallel item capability. */
public interface IConveyorBelt {boolean canItemStay(Level level,int x,int y,int z,Vec3 itemPos);Vec3 getTravelLocation(Level level,int x,int y,int z,Vec3 itemPos,double speed);Vec3 getClosestSnappingPosition(Level level,int x,int y,int z,Vec3 itemPos);}
