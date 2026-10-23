package com.hbm.machine.reactor.icf;

import api.hbm.redstoneoverradio.IRORValueProvider;
import com.hbm.api.computer.*;
import com.hbm.api.machine.IFluidCopiable;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.item.tool.WeldingParticlePacket;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.machine.reactor.icf.pellet.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmContent;
import com.hbm.satellite.SatelliteRayEvents;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

/** Exact source ICF pellet heating, first-step coolant conversion and stellar-flux production. */
public final class IcfReactorEntity extends ThermalMachine implements StandardFluidTransceiver,IFluidCopiable,ComputerControl,IRORValueProvider {
    public static final long MAX_HEAT=1_000_000_000_000L;
    public long laser,maxLaser,heat,heatup,laserSync,maxLaserSync;
    public int consumption,output;
    public final FluidTank[] tanks={new FluidTank(HbmFluids.fromName("SODIUM"),512000),new FluidTank(HbmFluids.fromName("SODIUM_HOT"),512000),new FluidTank(HbmFluids.fromName("STELLAR_FLUX"),24000)};
    private final Map<BlockPos,FluidPorts> ports=new LinkedHashMap<>();
    public IcfReactorEntity(BlockPos pos,BlockState state){super(IcfReactorContent.REACTOR_ENTITY.get(),pos,state,12);}
    @Override protected String title(){return "container.machineICF";}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new IcfReactorMenu(id,inventory,this,worldPosition);}
    public static NodeConnection[] connections(BlockPos core,Direction dir) {
        var rot=dir.getCounterClockWise();
        return new NodeConnection[]{new NodeConnection(core.above(6),Direction.UP),new NodeConnection(core.below(),Direction.DOWN),
            new NodeConnection(core.relative(dir,3).relative(rot,6).above(3),dir),new NodeConnection(core.relative(dir,3).relative(rot,-6).above(3),dir),
            new NodeConnection(core.relative(dir,-3).relative(rot,6).above(3),dir.getOpposite()),new NodeConnection(core.relative(dir,-3).relative(rot,-6).above(3),dir.getOpposite())};
    }
    public static void tick(Level level,BlockPos pos,BlockState state,IcfReactorEntity reactor){if(level instanceof ServerLevel server)reactor.serverTick(server);}
    private void serverTick(ServerLevel server) {
        if(ports.isEmpty())for(var link:connections(worldPosition,DummyableBlock.facing(getBlockState())))ports.put(link.origin(),new FluidPorts(new BlockPos[]{link.origin()},new NodeConnection[]{link}));
        for(var port:ports.values())port.update(server,this);
        if(slots.get(11).getItem()instanceof FluidIdentifier identifier)tanks[0].setTankType(identifier.getType(server,worldPosition,slots.get(11)));
        if(slots.get(5).is(HbmContent.item("icf_pellet_depleted")))for(int i=6;i<11;i++)if(slots.get(i).isEmpty()){slots.set(i,slots.get(5).copy());slots.set(5,ItemStack.EMPTY);setChanged();break;}
        if(slots.get(5).isEmpty())for(int i=0;i<5;i++)if(slots.get(i).is(IcfPelletPressContent.PELLET.get())){slots.set(5,slots.get(i).copy());slots.set(i,ItemStack.EMPTY);setChanged();break;}
        heatup=0;
        if(slots.get(5).is(IcfPelletPressContent.PELLET.get())&&IcfPelletItem.getFusingDifficulty(slots.get(5))<=laser) {
            heatup=IcfPelletItem.react(slots.get(5),laser);heat+=heatup;
            if(IcfPelletItem.getDepletion(slots.get(5))>=IcfPelletItem.getMaxDepletion(slots.get(5))){slots.set(5,new ItemStack(HbmContent.item("icf_pellet_depleted")));setChanged();}
            if(server.getGameTime()%20==15)SatelliteRayEvents.reportEvent(server,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),SatelliteRayEvents.RayEvent.INFO_PARTICLE,200);
            tanks[2].setFill(tanks[2].getFill()+(int)Math.ceil(heat*10D/MAX_HEAT));
            if(tanks[2].getFill()>tanks[2].getMaxFill())tanks[2].setFill(tanks[2].getMaxFill());
            var point=net.minecraft.world.phys.Vec3.atLowerCornerOf(worldPosition).add(.5,3.5,.5);
            var packet=new WeldingParticlePacket(point.x,point.y,point.z,0,false);
            for(var player:server.players())if(player.distanceToSqr(point)<625)PacketDistributor.sendToPlayer(player,packet);
        }
        if(heatup==0)heat+=laser*.25D;
        consumption=0;output=0;
        var trait=tanks[0].getTankType().getTrait(FluidTraits.Heatable.class);
        if(trait!=null) {
            var step=trait.getFirstStep();tanks[1].setTankType(step.typeProduced());
            int coolingCycles=tanks[0].getFill()/step.amountReq(),heatingCycles=(tanks[1].getMaxFill()-tanks[1].getFill())/step.amountProduced();
            int heatCycles=(int)Math.min(heat/4D/step.heatReq()*trait.getEfficiency(FluidTraits.HeatingType.ICF),heat/step.heatReq());
            int cycles=Math.min(coolingCycles,Math.min(heatingCycles,heatCycles));
            tanks[0].setFill(tanks[0].getFill()-step.amountReq()*cycles);tanks[1].setFill(tanks[1].getFill()+step.amountProduced()*cycles);heat-=step.heatReq()*cycles;
            consumption=step.amountReq()*cycles;output=step.amountProduced()*cycles;
        }
        heat*=.999D;if(heat>MAX_HEAT)heat=MAX_HEAT;
        laserSync=laser;maxLaserSync=maxLaser;sync(150);laser=0;maxLaser=0;
    }
    @Override public int[] getSlotsForFace(Direction side){return new int[]{0,1,2,3,4,6,7,8,9,10};}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot<5&&stack.is(IcfPelletPressContent.PELLET.get());}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot>5;}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=256;}
    @Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1],tanks[2]};}
    @Override public List<TankView> tanks(){return Arrays.stream(tanks).map(tank->new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill())).toList();}
    @Override public List<ProgressView> progressBars(){return List.of(new ProgressView("jade.hbm.icf.heat",heat,MAX_HEAT,0xff00af));}
    @Override protected void saveMachine(CompoundTag tag){tag.putLong("heat",heat);for(int i=0;i<3;i++)tanks[i].writeToNBT(tag,"t"+i);}
    @Override protected void loadMachine(CompoundTag tag){heat=tag.getLong("heat");for(int i=0;i<3;i++)tanks[i].readFromNBT(tag,"t"+i);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putLong("laser",laserSync);tag.putLong("maxLaser",maxLaserSync);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);laser=laserSync=tag.getLong("laser");maxLaser=maxLaserSync=tag.getLong("maxLaser");}
    private void disconnect(){if(level instanceof ServerLevel server)for(var port:ports.values())port.remove(server);ports.clear();}
    @Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public String computerType(){return "ntm_icf_reactor";}
    @Override public String[] computerMethods(){return new String[]{"getHeat","getHeatingRate","getMaxHeat","getPower","getFluid","getPelletStats"};}
    @Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "getHeat"->new Object[]{heat};case "getHeatingRate"->new Object[]{heatup};case "getMaxHeat"->new Object[]{MAX_HEAT};case "getPower"->new Object[]{laser};case "getFluid"->new Object[]{tanks[0].getFill(),tanks[0].getMaxFill(),tanks[0].getTankType().getUnlocalizedName(),tanks[1].getFill(),tanks[1].getMaxFill(),tanks[1].getTankType().getUnlocalizedName(),tanks[2].getFill(),tanks[2].getMaxFill()};case "getPelletStats"->{var stack=slots.get(5);if(stack.isEmpty())throw new ComputerException("No pellet loaded");yield new Object[]{IcfPelletItem.getDepletion(stack),IcfPelletItem.getMaxDepletion(stack),IcfPelletItem.getFusingDifficulty(stack),IcfPelletItem.getType(stack,true).name(),IcfPelletItem.getType(stack,false).name()};}default->throw new ComputerException("No such method: "+method);};}
    @Override public String[] getFunctionInfo(){return new String[]{PREFIX_VALUE+"laser",PREFIX_VALUE+"heat",PREFIX_VALUE+"coldfluid",PREFIX_VALUE+"hotfluid",PREFIX_VALUE+"stellarflux"};}
    @Override public String provideRORValue(String name){return switch(name){case "VAL:laser"->""+laser;case "VAL:heat"->""+heat;case "VAL:coldfluid"->""+tanks[0].getFill();case "VAL:hotfluid"->""+tanks[1].getFill();case "VAL:stellarflux"->""+tanks[2].getFill();default->null;};}
}
