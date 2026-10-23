package api.hbm.ntl;
import net.minecraft.core.Direction;
/** dir is the face of this connector, matching the original native pneumatic contract. */
public interface IPneumaticConnector {default boolean canConnectPneumatic(Direction dir){return dir!=null;}}
