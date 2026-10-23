package com.hbm.machine.reactor.dfc.components;
import com.hbm.api.computer.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.StandardFluidReceiver;
import com.hbm.machine.reactor.dfc.DfcCoreEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
/** Source two identifier pairs, independent128000mB fluids and unobstructed15-block injection ray. */
public final class DfcInjectorEntity extends DfcComponentEntity implements StandardFluidReceiver {
 public final FluidTank[] tanks={new FluidTank(HbmFluids.DEUTERIUM,128000),new FluidTank(HbmFluids.TRITIUM,128000)};
 public DfcInjectorEntity(BlockPos pos,BlockState state){super(DfcComponentKind.INJECTOR,pos,state);}
 @Override public void tickServer(ServerLevel server){ports(server);var inventory=slots.toArray(ItemStack[]::new);tanks[0].setType(0,1,inventory);tanks[1].setType(2,3,inventory);for(int i=0;i<4;i++)slots.set(i,inventory[i]);beam=0;var dir=direction();if(dir!=null)for(int i=1;i<=15;i++){var pos=worldPosition.relative(dir,i);if(server.getBlockEntity(pos) instanceof DfcCoreEntity core){for(int t=0;t<2;t++){var source=tanks[t];var destination=core.tanks[t];if(destination.getTankType()==source.getTankType()||destination.getFill()==0){if(destination.getTankType()!=source.getTankType())destination.setTankType(source.getTankType());int amount=Math.min(source.getFill(),destination.getMaxFill()-destination.getFill());source.setFill(source.getFill()-amount);destination.setFill(destination.getFill()+amount);core.setChanged();}}beam=i;break;}if(!server.getBlockState(pos).isAir())break;}sync(250);}
 @Override public FluidTank[] getReceivingTanks(){return tanks;}@Override public FluidTank[] getAllTanks(){return tanks;}
 @Override protected void saveMachine(CompoundTag tag){tanks[0].writeToNBT(tag,"fuel1");tanks[1].writeToNBT(tag,"fuel2");}
 @Override protected void loadMachine(CompoundTag tag){tanks[0].readFromNBT(tag,"fuel1");tanks[1].readFromNBT(tag,"fuel2");}
 @Override public String[] computerMethods(){return new String[]{"getFuel","getTypes","getInfo"};}
 @Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "getFuel"->new Object[]{tanks[0].getFill(),tanks[1].getFill()};case "getTypes"->new Object[]{tanks[0].getTankType().getName(),tanks[1].getTankType().getName()};case "getInfo"->new Object[]{tanks[0].getFill(),tanks[0].getTankType().getName(),tanks[1].getFill(),tanks[1].getTankType().getName()};default->throw new ComputerException("No such method: "+method);};}
}
