package api.hbm.energymk2;
import net.minecraft.core.*;
import net.minecraft.world.level.BlockGetter;
/** Actual source interface: blocks without energy TEs may advertise a visual cable connection only. */
public interface IEnergyConnectorBlock {boolean canConnect(BlockGetter world,BlockPos pos,Direction side);}
