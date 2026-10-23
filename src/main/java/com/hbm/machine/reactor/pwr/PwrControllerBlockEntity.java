package com.hbm.machine.reactor.pwr;

import com.hbm.api.computer.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.machine.reactor.ReactorInventory;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** X5846 free-layout PWR, source setup/fission/cooling arithmetic and persisted topology. */
public final class PwrControllerBlockEntity extends ReactorInventory implements StandardFluidTransceiver, com.hbm.multiblock.MuffledMachine {
    public final FluidTank[] tanks={new FluidTank(HbmFluids.COOLANT,128000),new FluidTank(HbmFluids.COOLANT_HOT,128000)};
    public static final long coreHeatCapacityBase=10000000,hullHeatCapacityBase=10000000;
    public long coreHeat,coreHeatCapacity=coreHeatCapacityBase,hullHeat;public double flux,rodLevel=100,rodTarget=100,progress,processTime;
    public int typeLoaded,amountLoaded,rodCount,connections,connectionsControlled,heatexCount,heatsinkCount,channelCount,sourceCount,unloadDelay;
    public boolean assembled,muffled;public BlockPos[] ports=new BlockPos[0];public final List<BlockPos> rods=new ArrayList<>();
    private FluidPorts fluidPorts;private List<BlockPos> loadedPorts=List.of();private CompoundTag lastSent;
    public static Consumer<PwrControllerBlockEntity> CLIENT_TICK=machine->{},CLIENT_REMOVED=machine->{};
    public PwrControllerBlockEntity(BlockPos pos,BlockState state){super(PwrContent.ENTITY.get(),pos,state,3);}
    @Override public boolean isValidBlockState(BlockState state){return state.is(PwrContent.CONTROLLER.get());}
    @Override protected String nameKey(){return "container.pwrController";}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new PwrMenu(id,inventory,this,worldPosition);}
    public static void tick(Level level,BlockPos pos,BlockState state,PwrControllerBlockEntity controller){if(level instanceof ServerLevel server)controller.serverTick(server);else CLIENT_TICK.accept(controller);}
    private boolean chunkExists(int x,int z){return level.hasChunk(x,z);}
public void setup(HashMap<BlockPos, Block> partMap, HashMap<BlockPos, Block> rodMap) {

		rodCount = 0;
		connections = 0;
		connectionsControlled = 0;
		heatexCount = 0;
		channelCount = 0;
		heatsinkCount = 0;
		sourceCount = 0;
		rods.clear();
		List<BlockPos> portList = new ArrayList();

		int connectionsDouble = 0;
		int connectionsControlledDouble = 0;

		for(Entry<BlockPos, Block> entry : partMap.entrySet()) {
			Block block = entry.getValue();

			if(block == PwrContent.block("pwr_fuel")) rodCount++;
			if(block == PwrContent.block("pwr_heatex")) heatexCount++;
			if(block == PwrContent.block("pwr_channel")) channelCount++;
			if(block == PwrContent.block("pwr_heatsink")) heatsinkCount++;
			if(block == PwrContent.block("pwr_neutron_source")) sourceCount++;
			if(block == PwrContent.block("pwr_port")) portList.add(entry.getKey().immutable());
		}

		for(Entry<BlockPos, Block> entry : rodMap.entrySet()) {
			BlockPos fuelPos = entry.getKey();

			rods.add(fuelPos);

			for(Direction dir : Direction.values()) {

				boolean controlled = false;

				for(int i = 1; i < 16; i++) {
					BlockPos checkPos = fuelPos.relative(dir, i);
					Block atPos = partMap.get(checkPos);
					if(atPos == null || atPos == PwrContent.block("pwr_casing")) break;
					if(atPos == PwrContent.block("pwr_control")) controlled = true;
					if(atPos == PwrContent.block("pwr_fuel")) {
						if(controlled) {
							connectionsControlledDouble++;
						} else {
							connectionsDouble++;
						}
						break;
					}
					if(atPos == PwrContent.block("pwr_reflector")) {
						if(controlled) {
							connectionsControlledDouble += 2;
						} else {
							connectionsDouble += 2;
						}
						break;
					}
				}
			}
		}
		
		this.ports = portList.toArray(new BlockPos[0]);

		connections = connectionsDouble / 2;
		connectionsControlled = connectionsControlledDouble / 2;
		heatsinkCount = Math.min(heatsinkCount, 80);

		//switching this to int64 because after 2127 heatsinks the capacity exceeds the int32 which is well within the 4000+ threshold we are working with. oops!
		this.coreHeatCapacity = this.coreHeatCapacityBase + this.heatsinkCount * (this.coreHeatCapacityBase / 20);
	}
    private void serverTick(ServerLevel server){
if(this.amountLoaded <= 0) this.tanks[0].setType(2, slots);
			setupTanks();

			if(unloadDelay > 0) unloadDelay--;

			int chunkX = worldPosition.getX() >> 4;
			int chunkZ = worldPosition.getZ() >> 4;

			//since fluid sources are often not within 1 chunk, we just do 2 chunks distance and call it a day
			if(!chunkExists(chunkX, chunkZ) ||
					!chunkExists(chunkX + 2, chunkZ + 2) ||
					!chunkExists(chunkX + 2, chunkZ - 2) ||
					!chunkExists(chunkX - 2, chunkZ + 2) ||
					!chunkExists(chunkX - 2, chunkZ - 2)) {
				this.unloadDelay = 60;
			}

			if(this.assembled) {
				updatePorts(server);
				

				//only perform fission if the area has been loaded for 40 ticks or more
				if(this.unloadDelay <= 0) {

					if((typeLoaded == -1 || amountLoaded <= 0) && !slots[0].isEmpty() && slots[0].getItem() == PwrContent.FUEL.get()) {
						typeLoaded = PwrFuelItem.metadata(slots[0]);
						amountLoaded++;
						removeItem(0, 1);
						setChanged();
					} else if(!slots[0].isEmpty() && slots[0].getItem() == PwrContent.FUEL.get() && PwrFuelItem.metadata(slots[0]) == typeLoaded && amountLoaded < rodCount){
						amountLoaded++;
						removeItem(0, 1);
						setChanged();
					}
					double diff = this.rodLevel - this.rodTarget;
					if(diff < 1 && diff > -1) this.rodLevel = this.rodTarget;
					if(this.rodTarget > this.rodLevel) this.rodLevel++;
					if(this.rodTarget < this.rodLevel) this.rodLevel--;

					double multiplier = 1D;

					if(tanks[0].getTankType().hasTrait(FluidTraits.PwrModerator.class)) {
						multiplier = tanks[0].getTankType().getTrait(FluidTraits.PwrModerator.class).getMultiplier();
					}

					int newFlux = this.sourceCount * 20;

					if(typeLoaded != -1 && amountLoaded > 0) {

						PwrFuelType fuel = PwrFuelType.from(typeLoaded);
						double usedRods = getTotalProcessMultiplier();
						double fluxPerRod = this.flux / this.rodCount;
						double outputPerRod = fuel.effonix(fluxPerRod);
						double totalOutput = outputPerRod * amountLoaded * usedRods;
						double totalHeatOutput = totalOutput * fuel.heatEmission;

						if(tanks[0].getFill() > 0) {
							totalHeatOutput *= multiplier;
						}

						this.coreHeat += totalHeatOutput;
						newFlux += totalOutput;

						this.processTime = (int) fuel.yield;
						this.progress += totalOutput;

						if(this.progress >= this.processTime) {
							this.progress -= this.processTime;

							if(slots[1].isEmpty()) {
								slots[1] = PwrContent.spent(typeLoaded);
							} else if(slots[1].getItem() == PwrContent.spent(typeLoaded).getItem() && PwrFuelItem.metadata(slots[1]) == typeLoaded && slots[1].getCount() < slots[1].getMaxStackSize()) {
								slots[1].grow(1);
							}

							this.amountLoaded--;
							setChanged();
						}
						
						if(server.getGameTime() % 100 == 0)
							com.hbm.satellite.SatelliteRayEvents.reportEvent(server,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),com.hbm.satellite.SatelliteRayEvents.RayEvent.INFO_NUCLEAR,200);
					}

					if(this.amountLoaded <= 0) {
						this.typeLoaded = -1;
					}

					if(amountLoaded > rodCount) amountLoaded = rodCount;

					/* CORE COOLING */
					double coreCoolingApproachNum = getXOverE((double) this.heatexCount * 5 / (double) getRodCountForCoolant(), 2) / 2D;
					long averageCoreHeat = (this.coreHeat + this.hullHeat) / 2;
					this.coreHeat -= (coreHeat - averageCoreHeat) * coreCoolingApproachNum;
					this.hullHeat -= (hullHeat - averageCoreHeat) * coreCoolingApproachNum;

					updateCoolant();

					this.coreHeat *= 0.999D;
					this.hullHeat *= 0.999D;

					this.flux = newFlux;

					if(tanks[0].getFill() > 0) {
						this.flux *= multiplier;
					}

					if(this.coreHeat > this.coreHeatCapacity) {
						meltDown(server);
					}
				} else {
					this.hullHeat = 0;
					this.coreHeat = 0;
				}
			} else {
				disconnect();
			}

			
        setChanged();var tag=getUpdateTag(server.registryAccess());if(!tag.equals(lastSent)||server.getGameTime()%20==0){lastSent=tag;var packet=ClientboundBlockEntityDataPacket.create(this,(ignored,lookup)->tag);for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<22500)player.connection.send(packet);}
    }
protected void updateCoolant() {

		FluidTraits.Heatable trait = tanks[0].getTankType().getTrait(FluidTraits.Heatable.class);
		if(trait == null || trait.getEfficiency(FluidTraits.HeatingType.PWR) <= 0) return;

		double coolingEff = (double) this.channelCount / (double) getRodCountForCoolant() * 0.1D; //10% cooling if numbers match
		if(coolingEff > 1D) coolingEff = 1D;

		//no use in trying to convert everythin to long since the internal tanks would never even support operation like that, just cap the heat cycle count to prevent overflows in the math
		int heatToUse = (int) Math.min(Math.min(this.hullHeat, (long) (this.hullHeat * coolingEff * trait.getEfficiency(FluidTraits.HeatingType.PWR))), 2_000_000_000);
		FluidTraits.HeatingStep step = trait.getFirstStep();
		int coolCycles = tanks[0].getFill() / step.amountReq();
		int hotCycles = (tanks[1].getMaxFill() - tanks[1].getFill()) / step.amountProduced();
		int heatCycles = heatToUse / step.heatReq();
		int cycles = Math.min(coolCycles, Math.min(hotCycles, heatCycles));

		this.hullHeat -= step.heatReq() * cycles;
		this.tanks[0].setFill(tanks[0].getFill() - step.amountReq() * cycles);
		this.tanks[1].setFill(tanks[1].getFill() + step.amountProduced() * cycles);
	}
protected int getRodCountForCoolant() {
		return this.rodCount + (int) Math.ceil(this.heatsinkCount / 4D);
	}
protected void setupTanks() {

		FluidTraits.Heatable trait = tanks[0].getTankType().getTrait(FluidTraits.Heatable.class);

		if(trait == null || trait.getEfficiency(FluidTraits.HeatingType.PWR) <= 0) {
			tanks[0].setTankType(HbmFluids.NONE);
			tanks[1].setTankType(HbmFluids.NONE);
			return;
		}

		tanks[1].setTankType(trait.getFirstStep().typeProduced());
	}
public double getTotalProcessMultiplier() {
		double totalConnections = this.connections + this.connectionsControlled * (1D - (this.rodLevel / 100D));
		double connectionsEff = connectinFunc(totalConnections);
		return connectionsEff;
	}
public double connectinFunc(double connections) {
		return connections / 10D * (1D - getXOverE(connections, 300D)) + connections / 150D * getXOverE(connections, 300D);
	}
public double getXOverE(double x, double d) {
		return 1 - Math.pow(Math.E, -x / d);
	}

    private void updatePorts(ServerLevel server){var active=new ArrayList<BlockPos>();for(var port:ports)if(server.hasChunkAt(port)&&server.getBlockEntity(port) instanceof PwrAssemblyBlockEntity part&&part.isPort()&&worldPosition.equals(part.controllerPos))active.add(port);if(!loadedPorts.equals(active)){disconnect();loadedPorts=List.copyOf(active);var connections=new ArrayList<com.hbm.energy.NodeConnection>();for(var port:active)for(var side:Direction.values())connections.add(new com.hbm.energy.NodeConnection(port.relative(side),side));if(!active.isEmpty())fluidPorts=new FluidPorts(active.toArray(BlockPos[]::new),connections.toArray(com.hbm.energy.NodeConnection[]::new));}if(fluidPorts!=null)fluidPorts.update(server,this);}
    public void disconnect(){if(level instanceof ServerLevel server&&fluidPorts!=null)fluidPorts.remove(server);fluidPorts=null;loadedPorts=List.of();}
    @Override public void onChunkUnloaded(){disconnect();if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.onChunkUnloaded();}
    @Override public void setRemoved(){disconnect();if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.setRemoved();}
    private void meltDown(ServerLevel server){server.destroyBlock(worldPosition,false);double x=0,y=0,z=0;var corium=PwrContent.block("corium_block").defaultBlockState().setValue(com.hbm.block.fluid.LegacyFluidBlock.LEVEL,5);for(var pos:rods){if(server.getBlockEntity(pos) instanceof PwrAssemblyBlockEntity part)part.restore();server.setBlock(pos,corium,3);x+=pos.getX()+.5;y+=pos.getY()+.5;z+=pos.getZ()+.5;}x/=rods.size();y/=rods.size();z/=rods.size();server.explode(null,x,y,z,15F,true,Level.ExplosionInteraction.BLOCK);}
    public void setLevel(double value){rodTarget=Mth.clamp(value,0,100);setChanged();}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0&&stack.is(PwrContent.FUEL.get());}
    @Override public int[] getSlotsForFace(Direction side){return new int[]{0,1};}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==1;}
    @Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1]};}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rejected=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(rejected!=amount)setChanged();return rejected;}
    @Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);if(amount>0)setChanged();}
    @Override public List<TankView> tanks(){return Arrays.stream(tanks).map(tank->new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill())).toList();}
    @Override public List<ProgressView> progressBars(){return amountLoaded>0&&processTime>0?List.of(new ProgressView("jade.hbm.pwr.depletion",progress,processTime,0xE69B51)):List.of();}
    @Override public String computerType(){return "ntm_pwr_control";}
    @Override public String[] computerMethods(){return new String[]{"getHeat","getFlux","getLevel","getCoolantInfo","getFuelInfo","getInfo","setLevel"};}
    @Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "getHeat"->new Object[]{coreHeat,hullHeat,coreHeatCapacity,hullHeatCapacityBase};case "getFlux"->new Object[]{flux};case "getLevel"->new Object[]{rodTarget,rodLevel};case "getCoolantInfo"->new Object[]{tanks[0].getFill(),tanks[0].getMaxFill(),tanks[1].getFill(),tanks[1].getMaxFill()};case "getFuelInfo"->new Object[]{amountLoaded,progress,processTime};case "getInfo"->new Object[]{coreHeat,hullHeat,coreHeatCapacity,hullHeatCapacityBase,flux,rodTarget,rodLevel,amountLoaded,progress,processTime,tanks[0].getFill(),tanks[0].getMaxFill(),tanks[1].getFill(),tanks[1].getMaxFill()};case "setLevel"->{setLevel(args.checkDouble(0));yield new Object[]{true};}default->throw new ComputerException("No such method");};}
    public static final String[] ROR={"VAL:rods","VAL:coreheat","VAL:hullheat","VAL:coldbuf","VAL:hotbuf","VAL:flux","VAL:depletion","FUN:setrods!percent","FUN:jettison"};
    public String[] getFunctionInfo(){return ROR.clone();}
    public String provideRORValue(String name){return switch(name){case "VAL:rods"->""+(int)(100-rodLevel);case "VAL:coreheat"->""+coreHeat;case "VAL:hullheat"->""+hullHeat;case "VAL:coldbuf"->""+tanks[0].getFill();case "VAL:hotbuf"->""+tanks[1].getFill();case "VAL:flux"->""+(int)flux;case "VAL:depletion"->""+(int)(progress*100/processTime);default->null;};}
    public String runRORFunction(String name,String[] params){if(name.equals("FUN:setrods")&&params.length>0){int percent;try{percent=Integer.parseInt(params[0]);}catch(Exception error){try{percent=(int)Math.round(Double.parseDouble(params[0]));}catch(Exception invalid){throw new IllegalArgumentException("Exception: Parameter in Invalid Format");}}if(percent<0||percent>100)throw new IllegalArgumentException("Exception: Parameter in Invalid Format");rodTarget=100-percent;setChanged();}if(name.equals("FUN:jettison")){typeLoaded=-1;amountLoaded=0;progress=0;setChanged();}return null;}
    private void writeLive(CompoundTag tag){tanks[0].writeToNBT(tag,"t0");tanks[1].writeToNBT(tag,"t1");tag.putBoolean("assembled",assembled);tag.putBoolean("muffled",muffled);tag.putLong("coreHeatL",coreHeat);tag.putLong("hullHeatL",hullHeat);tag.putDouble("flux",flux);tag.putDouble("rodLevel",rodLevel);tag.putDouble("rodTarget",rodTarget);tag.putInt("typeLoaded",typeLoaded);tag.putInt("amountLoaded",amountLoaded);tag.putDouble("progress",progress);tag.putDouble("processTime",processTime);tag.putLong("coreHeatCapacityL",coreHeatCapacity);tag.putInt("rodCount",rodCount);}
    private void readLive(CompoundTag tag){tanks[0].readFromNBT(tag,"t0");tanks[1].readFromNBT(tag,"t1");assembled=tag.getBoolean("assembled");muffled=tag.getBoolean("muffled");coreHeat=Math.max(tag.getInt("coreHeat"),tag.getLong("coreHeatL"));hullHeat=Math.max(tag.getInt("hullHeat"),tag.getLong("hullHeatL"));flux=tag.getDouble("flux");rodLevel=tag.getDouble("rodLevel");rodTarget=tag.getDouble("rodTarget");typeLoaded=tag.getInt("typeLoaded");amountLoaded=tag.getInt("amountLoaded");progress=tag.getDouble("progress");processTime=tag.getDouble("processTime");coreHeatCapacity=Math.max(coreHeatCapacityBase,Math.max(tag.getInt("coreHeatCapacity"),tag.getLong("coreHeatCapacityL")));rodCount=tag.getInt("rodCount");}
    @Override protected void writeState(CompoundTag tag){writeLive(tag);tag.putInt("connections",connections);tag.putInt("connectionsControlled",connectionsControlled);tag.putInt("heatexCount",heatexCount);tag.putInt("channelCount",channelCount);tag.putInt("sourceCount",sourceCount);tag.putInt("heatsinkCount",heatsinkCount);tag.putInt("portCount",ports.length);for(int i=0;i<ports.length;i++)tag.putIntArray("p"+i,new int[]{ports[i].getX(),ports[i].getY(),ports[i].getZ()});tag.putInt("rodCount",rods.size());for(int i=0;i<rods.size();i++){var pos=rods.get(i);tag.putIntArray("r"+i,new int[]{pos.getX(),pos.getY(),pos.getZ()});}}
    @Override protected void readState(CompoundTag tag){readLive(tag);connections=tag.getInt("connections");connectionsControlled=tag.getInt("connectionsControlled");heatexCount=tag.getInt("heatexCount");channelCount=tag.getInt("channelCount");sourceCount=tag.getInt("sourceCount");heatsinkCount=tag.getInt("heatsinkCount");ports=new BlockPos[tag.getInt("portCount")];for(int i=0;i<ports.length;i++){var a=tag.getIntArray("p"+i);ports[i]=new BlockPos(a[0],a[1],a[2]);}rods.clear();for(int i=0;i<rodCount;i++)if(tag.contains("r"+i)){var a=tag.getIntArray("r"+i);rods.add(new BlockPos(a[0],a[1],a[2]));}}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();writeLive(tag);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){readLive(tag);}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
