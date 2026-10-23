package com.hbm.machine.reactor.iter.torus;

import api.hbm.redstoneoverradio.*;
import com.hbm.api.computer.*;
import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.fusion.klystron.*;
import com.hbm.fusion.plasma.*;
import com.hbm.item.battery.*;
import com.hbm.machine.reactor.iter.*;
import com.hbm.satellite.SatelliteRayEvents;
import com.hbm.recipe.generic.Blueprints;
import com.hbm.registry.HbmSounds;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Actual ITER source torus: cold solenoids,realKyU ignition,progressivefusion and genuine sharedplasma/neutron distribution. */
public final class TorusEntity extends CooledFusionMachine implements EnergyConductor,KlystronReceiver,ComputerControl,IRORValueProvider,IRORInteractive {
    public static Consumer<TorusEntity> CLIENT_TICK=ignored->{},CLIENT_REMOVED=ignored->{};
    public final FluidTank[] fuelTanks={new FluidTank(HbmFluids.NONE,4000),new FluidTank(HbmFluids.NONE,4000),new FluidTank(HbmFluids.NONE,4000),new FluidTank(HbmFluids.NONE,4000)};
    public final FusionProcessor fusionModule;
    public final boolean[] connections=new boolean[4];
    private final KlystronNode[] klystronNodes=new KlystronNode[4];private final PlasmaNode[] plasmaNodes=new PlasmaNode[4];
    private PowerNode powerNode;private FluidPorts fluidPorts;private int floorChecked,floorValid;
    public boolean didProcess;public long klystronEnergy,plasmaEnergy;public double fuelConsumption;
    public float magnet,prevMagnet,magnetSpeed;public int timeOffset=-1;
    public TorusEntity(BlockPos pos,BlockState state){super(TorusContent.ENTITY.get(),pos,state,3);fusionModule=new FusionProcessor(this,fuelTanks[0],fuelTanks[1],fuelTanks[2],fuelTanks[3],this,this::recipes);}
    public List<FusionRecipe> recipes(){return level==null?List.of():FusionRecipeConfig.recipes(level.getRecipeManager());}
    @Override protected String title(){return "container.fusionTorus";}@Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new TorusMenu(id,inventory,this,worldPosition);}
    public static NodeConnection[] portConnections(BlockPos core){var out=new ArrayList<NodeConnection>();for(int[] p:new int[][]{{0,0},{6,0},{6,2},{6,-2},{-6,0},{-6,2},{-6,-2},{0,6},{2,6},{-2,6},{0,-6},{2,-6},{-2,-6}}){out.add(new NodeConnection(core.offset(p[0],-1,p[1]),Direction.DOWN));out.add(new NodeConnection(core.offset(p[0],5,p[1]),Direction.UP));}return out.toArray(NodeConnection[]::new);}
    public static double speedScaled(double max,double fill){return max==0?0:fill>=max*.5?1:fill/max*2D;}
    public static double outputIntensity(int count){return count==1?1:count==2?.625D:count==3?.5D:.4375D;}
    public static void tick(Level level,BlockPos pos,BlockState state,TorusEntity torus){if(level instanceof ServerLevel server)torus.serverTick(server);else{if(torus.timeOffset==-1)torus.timeOffset=level.random.nextInt(30000);double factor=speedScaled(torus.getMaxPower(),torus.getPower());if(torus.didProcess)torus.magnetSpeed+=.25F;else torus.magnetSpeed-=.25F;torus.magnetSpeed=net.minecraft.util.Mth.clamp(torus.magnetSpeed,0,30F*(float)factor);torus.prevMagnet=torus.magnet;torus.magnet+=torus.magnetSpeed;if(torus.magnet>=360){torus.magnet-=360;torus.prevMagnet-=360;}CLIENT_TICK.accept(torus);}}
    private void serverTick(ServerLevel server){
        var ports=portConnections(worldPosition);var positions=Arrays.stream(ports).map(NodeConnection::origin).toArray(BlockPos[]::new);
        if(powerNode==null||powerNode.isExpired()){powerNode=new PowerNode(positions).setConnections(ports);HbmEnergy.nodespace(server).createNode(powerNode);}if(powerNode.hasValidNet())powerNode.addReceiver(this);if(fluidPorts==null)fluidPorts=new FluidPorts(positions,ports);fluidPorts.update(server,this);checkFoundation(server);
        var kySpace=KlystronNetworks.nodespace(server);var plasmaSpace=PlasmaNetworks.nodespace(server);
        for(int i=0;i<4;i++){var dir=Direction.from3DDataValue(i+2);var pos=worldPosition.relative(dir,7).above(2);if(klystronNodes[i]==null||klystronNodes[i].isExpired()){klystronNodes[i]=kySpace.getNode(pos);if(klystronNodes[i]==null){klystronNodes[i]=new KlystronNode(pos).setConnections(new NodeConnection(pos.relative(dir),dir));kySpace.createNode(klystronNodes[i]);}}if(plasmaNodes[i]==null||plasmaNodes[i].isExpired()){plasmaNodes[i]=plasmaSpace.getNode(pos);if(plasmaNodes[i]==null){plasmaNodes[i]=new PlasmaNode(pos).setConnections(new NodeConnection(pos.relative(dir),dir));plasmaSpace.createNode(plasmaNodes[i]);}}if(klystronNodes[i].hasValidNet())klystronNodes[i].addReceiver(this);if(plasmaNodes[i].hasValidNet())plasmaNodes[i].addProvider(this);}
        updateCryogenics();var batteryStack=slots.get(0);if(batteryStack.getItem() instanceof CreativeBatteryItem)power=BigInteger.valueOf(getMaxPower());else if(batteryStack.getItem() instanceof HbmBattery battery){long amount=Math.min(Math.min(getMaxPower()-getPower(),battery.getDischargeRate(batteryStack)),battery.getCharge(batteryStack));battery.dischargeBattery(batteryStack,amount);power=power.add(BigInteger.valueOf(amount));}
        int receiverCount=0,collectors=0;
        for(int i=0;i<4;i++){connections[i]=false;if(klystronNodes[i].hasValidNet()&&!klystronNodes[i].network().providers.isEmpty())connections[i]=true;if(!connections[i]&&plasmaNodes[i].hasValidNet()&&!plasmaNodes[i].network().receivers.isEmpty())connections[i]=true;if(plasmaNodes[i].hasValidNet()&&!plasmaNodes[i].network().receivers.isEmpty())for(var receiver:plasmaNodes[i].network().receivers){if(!loaded(receiver))continue;if(receiver.receivesFusionPower())receiverCount++;if(receiver instanceof FusionNeutronCollector)collectors++;break;}}
        var recipe=(FusionRecipe)fusionModule.getRecipe();double factor=speedScaled(getMaxPower(),getPower());for(int i=0;i<3;i++)factor=Math.min(factor,recipe!=null&&recipe.inputFluids().size()>i?speedScaled(fuelTanks[i].getMaxFill(),fuelTanks[i].getFill()):1D);boolean ignition=recipe==null||recipe.ignitionTemp()<=klystronEnergy;
        float r=0,g=0,b=0;plasmaEnergy=0;fuelConsumption=0;fusionModule.preUpdate(factor,collectors*.5D);fusionModule.update(1,1,!tilted&&isCool()&&ignition,slots.get(1));didProcess=fusionModule.didProcess;if(fusionModule.markDirty)setChanged();if(didProcess&&recipe!=null){plasmaEnergy=(long)Math.ceil(recipe.outputTemp()*factor);fuelConsumption=factor;r=recipe.r();g=recipe.g();b=recipe.b();if(server.getGameTime()%20==15)SatelliteRayEvents.reportEvent(server,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),SatelliteRayEvents.RayEvent.INFO_PARTICLE,200);}
        double intensity=outputIntensity(receiverCount),flux=recipe==null?0:recipe.neutronFlux()*factor;
        if(plasmaEnergy>0)for(var node:plasmaNodes)if(node.hasValidNet()&&!node.network().receivers.isEmpty())for(var receiver:node.network().receivers)receiver.receiveFusionPower((long)Math.ceil(plasmaEnergy*intensity),flux,r,g,b);
        sync(150);klystronEnergy=0;
    }
    private static boolean loaded(FusionPowerReceiver receiver){return !(receiver instanceof net.minecraft.world.level.block.entity.BlockEntity tile)||!tile.isRemoved()&&tile.hasLevel()&&tile.getLevel().hasChunkAt(tile.getBlockPos());}
    private void checkFoundation(ServerLevel server){if(!com.hbm.config.GeneralModes.machineGravity()){tilted=false;return;}int identity=(worldPosition.getY()+worldPosition.getZ()*27644437)*27644437+worldPosition.getX();if((server.getGameTime()+identity)%20!=0)return;if(floorChecked>=36){boolean previous=tilted;tilted=floorValid<floorChecked*.95;if(tilted&&!previous)server.playSound(null,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,HbmSounds.get("block.metalImpact"),SoundSource.BLOCKS,3,1);setChanged();floorChecked=floorValid=0;}var pos=worldPosition.offset(-5+(floorChecked/6)*2,-1,-5+(floorChecked%6)*2);floorChecked++;var state=server.getBlockState(pos);var material=LegacyMaterials.of(state).name();if(state.isFaceSturdy(server,pos,Direction.UP)&&state.isSolidRender(server,pos)&&!state.isSignalSource()&&!Set.of("sand","cloth","ground").contains(material)&&state.getBlock().getExplosionResistance()>=Blocks.STONE.getExplosionResistance())floorValid++;}
    @Override public boolean acceptsKlystron(){return isLoaded();}@Override public void receiveKlystron(long amount){klystronEnergy+=amount;}
    @Override public PowerNode energyNode(){return powerNode;}@Override public boolean canConnect(Direction side){return side!=null;}@Override public long getMaxPower(){return 10000000;}
    @Override public FluidTank[] getSendingTanks(){return new FluidTank[]{coolantTanks[1],fuelTanks[3]};}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{coolantTanks[0],fuelTanks[0],fuelTanks[1],fuelTanks[2]};}@Override public FluidTank[] getAllTanks(){return new FluidTank[]{coolantTanks[0],coolantTanks[1],fuelTanks[0],fuelTanks[1],fuelTanks[2],fuelTanks[3]};}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0||slot==1&&stack.is(Blueprints.ITEM.get());}@Override public int[] getSlotsForFace(Direction side){return new int[]{2};}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==2;}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=1024;}
    public void select(String name){fusionModule.setRecipe(name,false);setChanged();}
    @Override protected void saveMachine(CompoundTag tag){saveCooling(tag);for(int i=0;i<4;i++)fuelTanks[i].writeToNBT(tag,"ft"+i);fusionModule.writeToNBT(tag);}
    @Override protected void loadMachine(CompoundTag tag){loadCooling(tag);for(int i=0;i<4;i++)fuelTanks[i].readFromNBT(tag,"ft"+i);fusionModule.readFromNBT(tag);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();saveMachine(tag);tag.putBoolean("didProcess",didProcess);tag.putLong("klystronEnergy",klystronEnergy);tag.putLong("plasmaEnergy",plasmaEnergy);tag.putDouble("fuelConsumption",fuelConsumption);int connectionBits=0;for(int i=0;i<4;i++)if(connections[i])connectionBits|=1<<i;tag.putInt("connections",connectionBits);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){loadMachine(tag);didProcess=tag.getBoolean("didProcess");klystronEnergy=tag.getLong("klystronEnergy");plasmaEnergy=tag.getLong("plasmaEnergy");fuelConsumption=tag.getDouble("fuelConsumption");int bits=tag.getInt("connections");for(int i=0;i<4;i++)connections[i]=(bits&(1<<i))!=0;}
    private void disconnect(){if(level instanceof ServerLevel server){if(powerNode!=null&&!powerNode.isExpired())HbmEnergy.nodespace(server).destroyNode(powerNode);if(fluidPorts!=null)fluidPorts.remove(server);for(var node:klystronNodes)KlystronNetworks.nodespace(server).destroyNode(node);for(var node:plasmaNodes)PlasmaNetworks.nodespace(server).destroyNode(node);}powerNode=null;fluidPorts=null;Arrays.fill(klystronNodes,null);Arrays.fill(plasmaNodes,null);if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public String computerType(){return "ntm_fusion_torus";}@Override public String[] computerMethods(){return new String[]{"getEnergyInfo","getFluid","getCoolant","getKlystronEnergy","getPlasmaEnergy","getFuelConsumption","getRecipeProgress","getInfo"};}
    private List<Object> fluidInfo(){var out=new ArrayList<Object>();for(var tank:fuelTanks){out.add(tank.getFill());out.add(tank.getMaxFill());out.add(tank.getTankType().getUnlocalizedName());}return out;}
    @Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "getEnergyInfo"->new Object[]{getPower(),getMaxPower()};case "getFluid"->fluidInfo().toArray();case "getCoolant"->new Object[]{coolantTanks[0].getFill(),coolantTanks[0].getMaxFill(),coolantTanks[1].getFill(),coolantTanks[1].getMaxFill()};case "getKlystronEnergy"->new Object[]{klystronEnergy};case "getPlasmaEnergy"->new Object[]{plasmaEnergy};case "getFuelConsumption"->new Object[]{fuelConsumption};case "getRecipeProgress"->new Object[]{fusionModule.progress,fusionModule.bonus};case "getInfo"->{var out=new ArrayList<Object>();out.add(getPower());out.add(getMaxPower());out.addAll(fluidInfo());for(var tank:coolantTanks){out.add(tank.getFill());out.add(tank.getMaxFill());}out.add(klystronEnergy);out.add(plasmaEnergy);out.add(fuelConsumption);out.add(fusionModule.progress);out.add(fusionModule.bonus);yield out.toArray();}default->throw new ComputerException("No such method: "+method);};}
    @Override public String[] getFunctionInfo(){return new String[]{PREFIX_VALUE+"plasma",PREFIX_VALUE+"consumption",PREFIX_VALUE+"progress",PREFIX_VALUE+"recipe",PREFIX_VALUE+"active",PREFIX_VALUE+"temp",PREFIX_FUNCTION+"setrecipe"+NAME_SEPARATOR+"name"};}@Override public String provideRORValue(String name){return switch(name){case "VAL:plasma"->""+plasmaEnergy;case "VAL:consumption"->""+(int)(fuelConsumption*100);case "VAL:progress"->""+(int)Math.round(fusionModule.progress*100);case "VAL:recipe"->fusionModule.getRecipeName();case "VAL:active"->didProcess?"1":"0";case "VAL:temp"->""+(int)temperature;default->null;};}@Override public String runRORFunction(String name,String[] parameters){if(name.equals(PREFIX_FUNCTION+"setrecipe")&&parameters.length==1)select(parameters[0]);return null;}
}
