package com.hbm.machine.reactor.dfc.components;
import api.hbm.block.ILaserable;
import com.hbm.api.computer.*;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.StandardFluidReceiver;
import java.math.BigInteger;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
/** Source front-face SPK receiver. Its joules*5000 HE conversion and temporary buffer are exact integers. */
public final class DfcReceiverEntity extends DfcComponentEntity implements EnergyProvider,SidedEnergyConnector,ILaserable,StandardFluidReceiver {
 public BigInteger joules=BigInteger.ZERO;public final FluidTank tank=new FluidTank(HbmFluids.CRYOGEL,64000);
 public DfcReceiverEntity(BlockPos pos,BlockState state){super(DfcComponentKind.RECEIVER,pos,state);}
 @Override public void tickServer(ServerLevel server){ports(server);power=joules.multiply(BigInteger.valueOf(5000));if(joules.signum()>0){if(tank.getFill()>=20)tank.setFill(tank.getFill()-20);else{server.setBlockAndUpdate(worldPosition,Blocks.LAVA.defaultBlockState());return;}}sync(50);joules=BigInteger.ZERO;}
 @Override public BigInteger getMaxPowerExact(){return power;}@Override public BigInteger getProviderSpeedExact(){return power;}
 @Override public void addEnergy(Level level,int x,int y,int z,long energy,Direction incoming){if(incoming!=null&&incoming.getOpposite()==direction()){joules=joules.add(BigInteger.valueOf(energy));setChanged();}else if(!level.isClientSide){level.destroyBlock(worldPosition,false);level.explode(null,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,2.5F,Level.ExplosionInteraction.BLOCK);}}
 @Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tank};}@Override public FluidTank[] getAllTanks(){return getReceivingTanks();}
 @Override protected void saveMachine(CompoundTag tag){writeExact(tag,"power",power);writeExact(tag,"joules",joules);tank.writeToNBT(tag,"tank");}
 @Override protected void loadMachine(CompoundTag tag){power=readExact(tag,"power");joules=readExact(tag,"joules");tank.readFromNBT(tag,"tank");}
 @Override public String[] computerMethods(){return new String[]{"getEnergyInfo","getCryogel","getInfo","getEnergyInfoExact","getInfoExact"};}
 @Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "getEnergyInfo"->new Object[]{joules.doubleValue(),power.doubleValue()};case "getCryogel"->new Object[]{tank.getFill()};case "getInfo"->new Object[]{joules.doubleValue(),power.doubleValue(),tank.getFill()};case "getEnergyInfoExact"->new Object[]{joules.toString(),power.toString()};case "getInfoExact"->new Object[]{joules.toString(),power.toString(),tank.getFill()};default->throw new ComputerException("No such method: "+method);};}
}
