package com.hbm.machine.reactor.rbmk.thermal;
import com.hbm.api.computer.*;
import com.hbm.fluid.*;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Source61-tick cached5x5 area,50mB cryogel exchange and200-degree cooling with20C floor. */
public final class RbmkCoolerEntity extends RbmkThermalColumn implements ComputerControl {
    private int timer;private final RbmkColumnEntity[] neighbors=new RbmkColumnEntity[25];
    public final FluidTank cold=new FluidTank(HbmFluids.PERFLUOROMETHYL_COLD,4000),hot=new FluidTank(HbmFluids.PERFLUOROMETHYL,4000);
    public RbmkCoolerEntity(BlockPos pos,BlockState state){super(RbmkThermalContent.COOLER_ENTITY.get(),pos,state);}
    public static void tick(Level level,BlockPos pos,BlockState state,RbmkCoolerEntity machine){if(level instanceof ServerLevel world)machine.tick(world);}
    private void tick(ServerLevel world){ports(world);if(timer<=0){timer=60;for(int i=0;i<25;i++){var pos=worldPosition.offset(-2+i/5,0,-2+i%5);var tile=world.getBlockEntity(pos);neighbors[i]=tile instanceof RbmkColumnEntity column?column:null;}}else timer--;if(cold.getFill()>=50&&hot.getMaxFill()-hot.getFill()>=50){cold.setFill(cold.getFill()-50);hot.setFill(hot.getFill()+50);for(var neighbor:neighbors)if(neighbor!=null){neighbor.heat-=200;if(neighbor.heat<20)neighbor.heat=20;neighbor.setChanged();}}baseTick(world);setChanged();}
    @Override public String consoleType(){return "COOLER";}
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{cold,hot};}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{cold};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{hot};}
    @Override protected void saveState(CompoundTag tag){super.saveState(tag);cold.writeToNBT(tag,"t0");hot.writeToNBT(tag,"t1");}@Override protected void loadState(CompoundTag tag){super.loadState(tag);cold.readFromNBT(tag,"t0");hot.readFromNBT(tag,"t1");}
    @Override public String computerType(){return "rbmk_cooler";}@Override public String[] computerMethods(){return new String[]{"getHeat","getCoolant","getCoordinates","getInfo"};}
    @Override public Object[] callComputer(String method,ComputerArguments args)throws ComputerException{return switch(method){case "getHeat"->new Object[]{heat};case "getCoolant"->new Object[]{cold.getFill(),cold.getMaxFill(),hot.getFill(),hot.getMaxFill()};case "getCoordinates"->new Object[]{worldPosition.getX(),worldPosition.getY(),worldPosition.getZ()};case "getInfo"->new Object[]{heat,cold.getFill(),cold.getMaxFill(),hot.getFill(),hot.getMaxFill(),worldPosition.getX(),worldPosition.getY(),worldPosition.getZ()};default->throw new ComputerException("No such method: "+method);};}
}
