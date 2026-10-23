package com.hbm.machine.reactor.icf.pellet;
import com.hbm.api.machine.IFluidCopiable;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.material.HbmMaterials;
import com.hbm.registry.HbmContent;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Exact eight-slot press: instant one output, two different fuels and optional sixteen-pellet muon charge. */
public final class IcfPelletPressEntity extends ThermalMachine implements StandardFluidTransceiver,IFluidCopiable {
 public final FluidTank[] tanks={new FluidTank(HbmFluids.fromName("DEUTERIUM"),16000),new FluidTank(HbmFluids.fromName("TRITIUM"),16000)};
 public int muon;public static final int MAX_MUON=16;
 private FluidPorts ports;
 public IcfPelletPressEntity(BlockPos pos,BlockState state){super(IcfPelletPressContent.ENTITY.get(),pos,state,8);}
 @Override public boolean isValidBlockState(BlockState state){return state.is(IcfPelletPressContent.BLOCK.get());}
 @Override protected String title(){return "container.machineICFPress";}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new IcfPelletPressMenu(id,inventory,this,worldPosition);}
 public static void tick(Level level,BlockPos pos,BlockState state,IcfPelletPressEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);}
 private void serverTick(ServerLevel level){
  if(ports==null)ports=new FluidPorts(worldPosition);ports.update(level,this);
  for(int i=0;i<2;i++){var identifier=slots.get(i+6);if(identifier.getItem() instanceof FluidIdentifier type)tanks[i].setTankType(type.getType(level,worldPosition,identifier));}
  if(muon<=0&&slots.get(2).is(HbmContent.item("particle_muon"))){var remaining=slots.get(2).getCraftingRemainingItem();var output=slots.get(3);boolean canStore=false;if(remaining.isEmpty())canStore=true;else if(output.isEmpty()){slots.set(3,remaining.copy());canStore=true;}else if(output.is(remaining.getItem())&&output.getDamageValue()==remaining.getDamageValue()&&output.getCount()<output.getMaxStackSize()){output.grow(1);canStore=true;}if(canStore){muon=MAX_MUON;removeItem(2,1);setChanged();}}
  press();sync(15);
 }
 private record Feed(IcfPelletItem.Fuel fuel,boolean fluid){}
 private Feed fuel(FluidTank tank,ItemStack stack){if(tank.getFill()>=1000){var kind=IcfPelletItem.fluidFuel(tank.getTankType());if(kind!=null)return new Feed(kind,true);}if(stack.isEmpty())return null;var materials=HbmMaterials.getMaterialsFromItem(stack);if(materials.size()!=1||materials.getFirst().amount!=HbmMaterials.shape("INGOT").q(1))return null;var kind=IcfPelletItem.materialFuel(materials.getFirst().material);return kind==null?null:new Feed(kind,false);}
 public void press(){if(!slots.get(0).is(HbmContent.item("icf_pellet_empty"))||!slots.get(1).isEmpty())return;var first=fuel(tanks[0],slots.get(4));var second=fuel(tanks[1],slots.get(5));if(first==null||second==null||first.fuel==second.fuel)return;slots.set(1,IcfPelletItem.setup(first.fuel,second.fuel,muon>0));if(muon>0)muon--;removeItem(0,1);if(first.fluid)tanks[0].setFill(tanks[0].getFill()-1000);else removeItem(4,1);if(second.fluid)tanks[1].setFill(tanks[1].getFill()-1000);else removeItem(5,1);setChanged();}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){if(stack.is(HbmContent.item("icf_pellet_empty")))return slot==0;if(stack.is(HbmContent.item("particle_muon")))return slot==2;return slot==4||slot==5;}
 @Override public int[] getSlotsForFace(Direction side){return side==Direction.UP||side==Direction.DOWN?new int[]{0,1,2,3,4}:new int[]{0,1,2,3,5};}
 @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==1||slot==3;}
 @Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return tanks;}@Override public FluidTank[] getSendingTanks(){return new FluidTank[0];}
 @Override public List<TankView> tanks(){return List.of(new TankView(tanks[0].getTankType(),tanks[0].getFill(),16000),new TankView(tanks[1].getTankType(),tanks[1].getFill(),16000));}
 @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long left=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(left!=amount)setChanged();return left;}
 @Override protected void saveMachine(CompoundTag tag){for(int i=0;i<2;i++)tanks[i].writeToNBT(tag,"t"+i);tag.putByte("muon",(byte)muon);}
 @Override protected void loadMachine(CompoundTag tag){for(int i=0;i<2;i++)tanks[i].readFromNBT(tag,"t"+i);muon=tag.getByte("muon");}
 private void disconnect(){if(ports!=null&&level instanceof ServerLevel server)ports.remove(server);ports=null;}
 @Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
}
