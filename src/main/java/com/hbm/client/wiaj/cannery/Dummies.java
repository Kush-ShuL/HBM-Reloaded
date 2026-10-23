package com.hbm.client.wiaj.cannery;
import com.hbm.energy.SidedEnergyConnector;import com.hbm.fluid.*;import com.hbm.fluid.network.FluidUser;import net.minecraft.core.BlockPos;import net.minecraft.world.level.block.Blocks;import net.minecraft.world.level.block.entity.BlockEntity;import net.minecraft.world.level.block.entity.BlockEntityType;
/** Source local connector faux tile; not registered as game content and never ticked/networked. */
public final class Dummies {
 public static final class JarDummyConnector extends BlockEntity implements SidedEnergyConnector,FluidUser {
  private static final BlockEntityType<JarDummyConnector> TYPE=BlockEntityType.Builder.of((pos,state)->new JarDummyConnector(),Blocks.AIR).build(null);
  public JarDummyConnector(){super(TYPE,BlockPos.ZERO,Blocks.AIR.defaultBlockState());}
  @Override public boolean isLoaded(){return true;}@Override public FluidTank[] getAllTanks(){return FluidTank.EMPTY_ARRAY;}
 }
 private Dummies(){}
}
