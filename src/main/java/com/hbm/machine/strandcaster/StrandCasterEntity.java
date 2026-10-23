package com.hbm.machine.strandcaster;
import com.hbm.api.machine.*;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.autogen.ScrapsItem;
import com.hbm.machine.anvil.MoldItem;
import com.hbm.machine.foundry.FoundryAcceptor;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.material.*;
import com.hbm.multiblock.DummyableBlock;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
/** X5846 caster: quanta-based molten input, nine-mold buffer and source idle flush. No HE consumer. */
public final class StrandCasterEntity extends ThermalMachine implements FoundryAcceptor,StandardFluidTransceiver,ICopiable {
 public NtmMaterial type;public int amount;public long lastProgressTick;
 public final FluidTank water=new FluidTank(HbmFluids.fromName("WATER"),64000),steam=new FluidTank(HbmFluids.fromName("SPENTSTEAM"),64000);
 private final Map<BlockPos,FluidPorts> ports=new LinkedHashMap<>();
 public StrandCasterEntity(BlockPos pos,BlockState state){super(StrandCasterContent.ENTITY.get(),pos,state,7);}
 @Override protected String title(){return "container.machineStrandCaster";}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new StrandCasterMenu(id,inventory,this,worldPosition);}
 public MoldItem.Mold mold(){return slots.getFirst().getItem() instanceof MoldItem item?item.mold(slots.getFirst()):null;}
 public int capacity(){var mold=mold();return mold==null?50000:mold.requiredUnits()*10;}
 public int waterRequired(){var mold=mold();return mold==null?50:mold.requiredUnits()*5;}
 public int limit(){var mold=mold();return mold==null?capacity():mold.requiredUnits()*9;}
 public static BlockPos[] portPositions(BlockPos pos,Direction dir){var rot=dir.getClockWise();return new BlockPos[]{pos.relative(rot).relative(dir,-1),pos.relative(dir,-1),pos.relative(rot).relative(dir,-5),pos.relative(dir,-5)};}
 public static Direction portSide(BlockPos core,Direction facing,BlockPos port){var positions=portPositions(core,facing);return port.equals(positions[0])||port.equals(positions[2])?facing.getClockWise():facing.getCounterClockWise();}
 public static BlockPos[] pourPositions(BlockPos core,Direction dir){var rot=dir.getClockWise();return new BlockPos[]{core.above(2).relative(rot).relative(dir,-1),core.above(2).relative(dir,-1),core.above(2).relative(rot),core.above(2)};}
 public static void tick(Level level,BlockPos pos,BlockState state,StrandCasterEntity self){if(level instanceof ServerLevel server)self.serverTick(server);}
 private void updatePorts(ServerLevel server){var facing=DummyableBlock.facing(getBlockState());var loaded=new HashSet<BlockPos>();for(var pos:portPositions(worldPosition,facing))if(server.hasChunkAt(pos)&&server.getBlockState(pos).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(pos))){loaded.add(pos);ports.computeIfAbsent(pos,key->{var side=portSide(worldPosition,facing,key);return new FluidPorts(new BlockPos[]{key},new NodeConnection[]{new NodeConnection(key.relative(side),side)});}).update(server,this);}for(var old:List.copyOf(ports.keySet()))if(!loaded.contains(old)){ports.remove(old).remove(server);}}
 private void serverTick(ServerLevel server){updatePorts(server);if(amount>capacity()){server.addFreshEntity(new ItemEntity(server,worldPosition.getX()+.5,worldPosition.getY()+2,worldPosition.getZ()+.5,ScrapsItem.create(new MaterialStack(type,Math.max(amount-capacity(),0)))));amount=capacity();}if(amount==0)type=null;int molds=maxProcessable();if(molds>0&&(molds>=9||server.getDayTime()>=lastProgressTick+200)){var mold=mold();amount-=molds*mold.requiredUnits();var output=mold.output(type);int remaining=output.getCount()*molds;for(int i=1;i<7&&remaining>0;i++){if(slots.get(i).isEmpty())slots.set(i,output.copyWithCount(0));if(sameOutput(slots.get(i),output)||slots.get(i).isEmpty()){int move=Math.min(remaining,output.getMaxStackSize()-slots.get(i).getCount());if(move>0){if(slots.get(i).isEmpty())slots.set(i,output.copyWithCount(move));else slots.get(i).grow(move);remaining-=move;}}}water.setFill(water.getFill()-waterRequired()*molds);steam.setFill(steam.getFill()+waterRequired()*molds);lastProgressTick=server.getDayTime();setChanged();}sync(150);}
 private static boolean sameOutput(ItemStack a,ItemStack b){return a.getItem()==b.getItem()&&a.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),a.getDamageValue()).equals(b.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),b.getDamageValue()));}
 public int maxProcessable(){var mold=mold();if(type==null||mold==null)return 0;var out=mold.output(type);if(out.isEmpty()||mold.requiredUnits()<=0)return 0;int free=0;for(int i=1;i<7;i++)if(slots.get(i).isEmpty())free+=out.getMaxStackSize();else if(sameOutput(slots.get(i),out))free+=out.getMaxStackSize()-slots.get(i).getCount();return Math.min(Math.min(amount/mold.requiredUnits(),free/out.getCount()),Math.min(water.getFill()/waterRequired(),(steam.getMaxFill()-steam.getFill())/waterRequired()));}
 public boolean standardCheck(MaterialStack stack){return(type==null||type==stack.material)&&amount<limit()&&mold()!=null;}
 public MaterialStack standardAdd(MaterialStack stack){type=stack.material;if(stack.amount+amount<=limit()){amount+=stack.amount;setChanged();return null;}int needed=limit()-amount;amount=limit();stack.amount-=needed;lastProgressTick=level.getDayTime();setChanged();return stack;}
 public boolean pourAt(BlockPos pos,Direction side,MaterialStack stack){return side==Direction.UP&&Arrays.asList(pourPositions(worldPosition,DummyableBlock.facing(getBlockState()))).contains(pos)&&standardCheck(stack);}
 @Override public boolean canAcceptPartialPour(Vec3 hit,Direction side,MaterialStack stack){return false;}
 @Override public MaterialStack pour(Vec3 hit,Direction side,MaterialStack stack){return standardAdd(stack);}
 @Override public boolean canAcceptPartialFlow(Direction side,MaterialStack stack){return false;}@Override public MaterialStack flow(Direction side,MaterialStack stack){return null;}
 @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}@Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null;}
 @Override public FluidTank[] getAllTanks(){return new FluidTank[]{water,steam};}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{water};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{steam};}
 @Override public List<TankView> tanks(){return List.of(new TankView(water.getTankType(),water.getFill(),water.getMaxFill()),new TankView(steam.getTankType(),steam.getFill(),steam.getMaxFill()));}@Override public List<ProgressView> progressBars(){return List.of(new ProgressView(type==null?"gui.hbm.foundry.material":type.descriptionId(),amount,capacity(),type==null?0xff8e42:type.moltenColor()));}
 @Override protected void saveMachine(CompoundTag tag){tag.putInt("type",type==null?-1:type.id());tag.putInt("amount",amount);water.writeToNBT(tag,"w");steam.writeToNBT(tag,"s");tag.putLong("t",lastProgressTick);}
 @Override protected void loadMachine(CompoundTag tag){type=HbmMaterials.byId(tag.getInt("type"));amount=tag.getInt("amount");water.readFromNBT(tag,"w");steam.readFromNBT(tag,"s");lastProgressTick=tag.getLong("t");}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);net.minecraft.world.ContainerHelper.saveAllItems(tag,slots,lookup);return tag;}
 @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);net.minecraft.world.ContainerHelper.loadAllItems(tag,slots,lookup);}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0&&stack.getItem() instanceof MoldItem;}@Override public int[] getSlotsForFace(Direction side){return new int[]{1,2,3,4,5,6};}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return !canPlaceItem(slot,stack);}
 @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=128;}
 public ItemStack removeMetal(){var result=amount>0?ScrapsItem.create(new MaterialStack(type,amount)):ItemStack.EMPTY;type=null;amount=0;setChanged();return result;}
 private void disconnect(){if(level instanceof ServerLevel server)ports.values().forEach(port->port.remove(server));ports.clear();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}@Override public void setRemoved(){disconnect();super.setRemoved();}
 @Override public CompoundTag getSettings(Level level,BlockPos pos){var tag=new CompoundTag();if(type!=null)tag.putIntArray("matFilter",new int[]{type.id()});return tag;}@Override public void pasteSettings(CompoundTag tag,int index,Level level,Player player,BlockPos pos){}@Override public String[] infoForDisplay(Level level,BlockPos pos){return null;}
}
