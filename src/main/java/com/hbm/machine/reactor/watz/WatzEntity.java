package com.hbm.machine.reactor.watz;

import api.hbm.redstoneoverradio.IRORValueProvider;
import com.hbm.api.computer.*;
import com.hbm.api.machine.IFluidCopiable;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.machine.reactor.watz.fuel.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Source TileEntityWatz: one real reaction per chain, top→bottom pellets, bottom→top thermal and private-fluid redistribution. */
public final class WatzEntity extends ThermalMachine implements StandardFluidTransceiver,IFluidCopiable,ComputerControl,IRORValueProvider, com.hbm.multiblock.MuffledMachine {
    public final FluidTank[] tanks={new FluidTank(HbmFluids.fromName("COOLANT"),64000),new FluidTank(HbmFluids.fromName("COOLANT_HOT"),64000),new FluidTank(HbmFluids.fromName("WATZ"),64000)};
    public FluidTank[] sharedTanks;
    public final NonNullList<ItemStack> locks=NonNullList.withSize(24,ItemStack.EMPTY);
    public int heat;public double fluxLastBase,fluxLastReaction,fluxDisplay;public boolean isOn,isLocked,muffled,tilted;
    private FluidPorts ports;private CompoundTag wireSnapshot;
    public WatzEntity(BlockPos pos,BlockState state){super(WatzContent.ENTITY.get(),pos,state,24);resetSharedTanks();}
    @Override protected String title(){return "container.watz";}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new WatzMenu(id,inventory,this,worldPosition);}
    @Override public int getMaxStackSize(){return 1;}
    private void resetSharedTanks(){sharedTanks=new FluidTank[3];for(int i=0;i<3;i++){sharedTanks[i]=new FluidTank(tanks[i].getTankType(),64000);sharedTanks[i].setFill(tanks[i].getFill());}}
    public static NodeConnection[] portConnections(BlockPos core){var result=new ArrayList<NodeConnection>();for(int[] p:new int[][]{{2,0},{-2,0},{0,2},{0,-2},{0,0}})result.add(new NodeConnection(core.offset(p[0],-1,p[1]),Direction.DOWN));for(int[] p:new int[][]{{2,0},{-2,0},{0,2},{0,-2},{0,0}})result.add(new NodeConnection(core.offset(p[0],3,p[1]),Direction.UP));return result.toArray(NodeConnection[]::new);}
    public static void tick(Level level,BlockPos pos,BlockState state,WatzEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);}
    private void serverTick(ServerLevel server){
        if(ports==null){var connections=portConnections(worldPosition);ports=new FluidPorts(Arrays.stream(connections).map(NodeConnection::origin).toArray(BlockPos[]::new),connections);}ports.update(server,this);resetSharedTanks();
        if(entityAt(worldPosition.above(3))!=null)return;
        boolean turnedOn=server.getBlockState(worldPosition.above(3)).is(WatzContent.PUMP.get())&&server.getSignal(worldPosition.above(5),Direction.DOWN)>0;
        var segments=new ArrayList<WatzEntity>();segments.add(this);
        for(int y=worldPosition.getY()-3;y>=server.getMinBuildHeight();y-=3){var segment=entityAt(new BlockPos(worldPosition.getX(),y,worldPosition.getZ()));if(segment==null)break;segments.add(segment);}
        var shared=new FluidTank[3];for(int i=0;i<3;i++)shared[i]=new FluidTank(tanks[i].getTankType(),0);
        for(var segment:segments){segment.setupCoolant();for(int i=0;i<3;i++){shared[i].changeTankSize(shared[i].getMaxFill()+segment.tanks[i].getMaxFill());shared[i].setFill(shared[i].getFill()+segment.tanks[i].getFill());}}
        for(int i=segments.size()-1;i>=0;i--)segments.get(i).updateCoolant(shared);
        updateReaction(null,shared,turnedOn);for(int i=1;i<segments.size();i++)segments.get(i).updateReaction(segments.get(i-1),shared,turnedOn);
        for(var segment:segments){segment.sharedTanks=shared;segment.isOn=turnedOn;segment.wireSnapshot=segment.liveUpdateTag();segment.sync(25);segment.heat*=.99;}
        for(int i=segments.size()-1;i>=0;i--){var segment=segments.get(i);for(int j=0;j<3;j++){int amount=Math.min(segment.tanks[j].getMaxFill(),shared[j].getFill());shared[j].setFill(shared[j].getFill()-amount);segment.tanks[j].setFill(amount);}segment.setChanged();}
        if(shared[2].getFill()>0)WatzFailure.explode(this,server);
    }
    private WatzEntity entityAt(BlockPos pos){return level!=null&&level.hasChunkAt(pos)&&level.getBlockEntity(pos) instanceof WatzEntity other?other:null;}
    private void setupCoolant(){tanks[0].setTankType(HbmFluids.fromName("COOLANT"));tanks[1].setTankType(tanks[0].getTankType().getTrait(FluidTraits.Heatable.class).getFirstStep().typeProduced());}
    private void updateCoolant(FluidTank[] shared){double heatToUse=heat*.2D;var step=shared[0].getTankType().getTrait(FluidTraits.Heatable.class).getFirstStep();int heatCycles=(int)(heatToUse/step.heatReq()),coolCycles=shared[0].getFill()/step.amountReq(),hotCycles=(shared[1].getMaxFill()-shared[1].getFill())/step.amountProduced();int cycles=Math.min(heatCycles,Math.min(hotCycles,coolCycles));heat-=cycles*step.heatReq();shared[0].setFill(shared[0].getFill()-cycles*step.amountReq());shared[1].setFill(shared[1].getFill()+cycles*step.amountProduced());}
    private void updateReaction(WatzEntity above,FluidTank[] shared,boolean turnedOn){
        if(turnedOn){var pellets=new ArrayList<ItemStack>();for(int i=0;i<24;i++){var stack=slots.get(i);if(WatzPelletItem.fresh(stack))pellets.add(stack);}
            double baseFlux=0;for(var stack:pellets)baseFlux+=WatzPelletItem.type(stack).passive;
            double inputFlux=baseFlux+fluxLastReaction,addedFlux=0,addedHeat=0;
            for(var stack:pellets){var type=WatzPelletItem.type(stack);if(type.burnFunc!=null){double div=type.heatDiv!=null?type.heatDiv.effonix(heat):1D;double burn=type.burnFunc.effonix(inputFlux)/div;WatzPelletItem.setYield(stack,WatzPelletItem.getYield(stack)-burn);addedFlux+=burn;addedHeat+=type.heatEmission*burn;shared[2].setFill(shared[2].getFill()+(int)Math.round(type.mudContent*burn));}}
            for(var stack:pellets){var type=WatzPelletItem.type(stack);if(type.absorbFunc!=null){double absorb=type.absorbFunc.effonix(baseFlux+fluxLastReaction);addedHeat+=absorb;WatzPelletItem.setYield(stack,WatzPelletItem.getYield(stack)-absorb);shared[2].setFill(shared[2].getFill()+(int)Math.round(type.mudContent*absorb));}}
            heat+=addedHeat;fluxLastBase=baseFlux;fluxLastReaction=addedFlux;
        }else{fluxLastBase=0;fluxLastReaction=0;}
        for(int i=0;i<24;i++){var stack=slots.get(i);if(WatzPelletItem.fresh(stack)&&WatzPelletItem.getEnrichment(stack)<=0){var out=WatzFuelContent.stack(true,WatzPelletItem.type(stack).ordinal());slots.set(i,out);continue;}}
        if(above!=null)for(int i=0;i<24;i++){var bottom=slots.get(i);var top=above.slots.get(i);if(bottom.isEmpty()&&!top.isEmpty()){slots.set(i,top.copy());above.removeItem(i,top.getCount());}if(WatzPelletItem.fresh(bottom)&&WatzPelletItem.depleted(top)){var buffer=top.copy();above.slots.set(i,bottom.copy());slots.set(i,buffer);}}
    }
    public void toggleLock(){if(isLocked)Collections.fill(locks,ItemStack.EMPTY);else for(int i=0;i<24;i++)locks.set(i,slots.get(i));isLocked=!isLocked;setChanged();}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return WatzPelletItem.fresh(stack)&&(!isLocked||!locks.get(slot).isEmpty()&&locks.get(slot).getItem()==stack.getItem());}
    @Override public int[] getSlotsForFace(Direction side){return java.util.stream.IntStream.range(0,24).toArray();}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return !WatzPelletItem.fresh(stack);}
    @Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1],tanks[2]};}@Override public FluidTank getTankToPaste(){return null;}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long left=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(left!=amount)setChanged();return left;}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);setChanged();}
    @Override public List<TankView> tanks(){return Arrays.stream(tanks).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}
    @Override protected void saveMachine(CompoundTag tag){for(int i=0;i<3;i++)tanks[i].writeToNBT(tag,"t"+i);tag.putInt("heat",heat);tag.putDouble("lastFluxB",fluxLastBase);tag.putDouble("lastFluxR",fluxLastReaction);tag.putBoolean("isLocked",isLocked);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    @Override protected void loadMachine(CompoundTag tag){for(int i=0;i<3;i++)tanks[i].readFromNBT(tag,"t"+i);heat=tag.getInt("heat");fluxLastBase=tag.getDouble("lastFluxB");fluxLastReaction=tag.getDouble("lastFluxR");isLocked=tag.getBoolean("isLocked");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");wireSnapshot=null;resetSharedTanks();}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var list=new ListTag();for(int i=0;i<24;i++)if(!locks.get(i).isEmpty()){var entry=(CompoundTag)locks.get(i).save(lookup);entry.putByte("slot",(byte)i);list.add(entry);}tag.put("locks",list);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);Collections.fill(locks,ItemStack.EMPTY);var list=tag.getList("locks",Tag.TAG_COMPOUND);for(int i=0;i<list.size();i++){var entry=list.getCompound(i);int index=entry.getByte("slot");if(index>=0&&index<24)locks.set(index,ItemStack.parseOptional(lookup,entry));}}
    private CompoundTag liveUpdateTag(){var tag=new CompoundTag();tag.putInt("heat",heat);tag.putBoolean("isOn",isOn);tag.putBoolean("isLocked",isLocked);tag.putDouble("fluxDisplay",fluxLastBase+fluxLastReaction);for(int i=0;i<3;i++)sharedTanks[i].writeToNBT(tag,"t"+i);return tag;}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return wireSnapshot==null?liveUpdateTag():wireSnapshot.copy();}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){heat=tag.getInt("heat");isOn=tag.getBoolean("isOn");isLocked=tag.getBoolean("isLocked");fluxDisplay=tag.getDouble("fluxDisplay");for(int i=0;i<3;i++){String key="t"+i;tanks[i].setTankType(HbmFluids.fromID(tag.getInt(key+"_type")));tanks[i].changeTankSize(tag.getInt(key+"_max"));tanks[i].withPressure(tag.getShort(key+"_p"));tanks[i].setFill(tag.getInt(key));}}
    private void disconnect(){if(ports!=null&&level instanceof ServerLevel server)ports.remove(server);ports=null;}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public String computerType(){return "watz_reactor";}@Override public String[] computerMethods(){return new String[]{"getComponentName","getHeat","getFlux","getCoolantInfo","getWasteInfo","isOn","getInfo"};}
    @Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "getComponentName"->new Object[]{computerType()};case "getHeat"->new Object[]{heat};case "getFlux"->new Object[]{fluxLastBase+fluxLastReaction};case "getCoolantInfo"->new Object[]{tanks[0].getFill(),tanks[0].getMaxFill(),tanks[1].getFill(),tanks[1].getMaxFill()};case "getWasteInfo"->new Object[]{tanks[2].getFill(),tanks[2].getMaxFill()};case "isOn"->new Object[]{isOn};case "getInfo"->new Object[]{tanks[0].getFill(),tanks[0].getMaxFill(),tanks[1].getFill(),tanks[1].getMaxFill(),tanks[2].getFill(),tanks[2].getMaxFill(),heat,fluxLastBase+fluxLastReaction,isOn};default->throw new ComputerException("No such method: "+method);};}
    @Override public String[] getFunctionInfo(){return new String[]{PREFIX_VALUE+"heat",PREFIX_VALUE+"flux",PREFIX_VALUE+"mud",PREFIX_VALUE+"coolant_hot",PREFIX_VALUE+"coolant_cold"};}
    @Override public String provideRORValue(String name){return switch(name){case "VAL:heat"->""+heat;case "VAL:flux"->""+(int)(fluxLastBase+fluxLastReaction);case "VAL:mud"->""+tanks[2].getFill();case "VAL:coolant_hot"->""+tanks[1].getFill();case "VAL:coolant_cold"->""+tanks[0].getFill();default->null;};}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
