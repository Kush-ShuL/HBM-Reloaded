package com.hbm.machine.launchpadsoyuz;

import api.hbm.item.IDesignatorItem;
import com.hbm.energy.*;
import com.hbm.entity.rocket.EntityRocketSoyuz;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.battery.*;
import com.hbm.item.fluid.FluidItemContent;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.machine.storage.CrateStackData;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.*;
import com.hbm.satellite.*;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Independent actual TileEntityLaunchpadSoyuz sequencer, twenty XR segments,27slots and twelve real native Combo ports. */
public final class LaunchpadSoyuzEntity extends ThermalMachine implements EnergyReceiver,SidedEnergyConnector,StandardFluidReceiver {
    public static final long MAX_POWER=1_000_000,CONSUMPTION=10_000;
    public static final int INDEX_STRUT1=0,INDEX_STRUT2=1,INDEX_STRUT3=2,INDEX_STRUT4=3,INDEX_STRUT5=4,INDEX_CARRIAGE=5,INDEX_ROTOR=6,INDEX_TILT=7;
    public static final int FUEL_DURATION=300,COUNTDOWN_DURATION=600;
    public enum SoyuzStatus{ABSENT,LOADING,FUELING,READY,LAUNCHING}
    public enum ComponentStatus{DEPLOY,RETRACT}
    public final FluidTank[] tanks={new FluidTank(HbmFluids.KEROSENE_REFORM,128_000),new FluidTank(HbmFluids.OXYGEN,128_000)};
    public final float[] positions=new float[8],prevPositions=new float[8],speed=new float[8],target=new float[8],syncPositions=new float[8];
    public long power;public boolean cargoMode,tilted;public int loadedType=-1,fuelCountdown,countdown;
    public SoyuzStatus soyuzStatus=SoyuzStatus.ABSENT;
    public ComponentStatus strutStatus=ComponentStatus.RETRACT,carriageStatus=ComponentStatus.RETRACT,rotorStatus=ComponentStatus.RETRACT;
    private int turnProgress;
    private final Map<BlockPos,PowerNode> powerPorts=new LinkedHashMap<>();private FluidPorts fluidPorts;
    private List<LaunchpadSoyuzLayout.Port> boundPorts=List.of();
    public static Consumer<LaunchpadSoyuzEntity> CLIENT_TICK=machine->{},CLIENT_REMOVE=machine->{};
    public LaunchpadSoyuzEntity(BlockPos pos,BlockState state){super(LaunchpadSoyuzContent.ENTITY.get(),pos,state,27);}
    @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}
    @Override protected String title(){return "container.launchpadSoyuz";}
    @Override public double getUseRange(){return 50;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new LaunchpadSoyuzMenu(id,inventory,this,worldPosition);}
    @Override public void writeClientSideData(AbstractContainerMenu menu,net.minecraft.network.RegistryFriendlyByteBuf buffer){buffer.writeBlockPos(worldPosition);}
    public static void tick(Level level,BlockPos pos,BlockState state,LaunchpadSoyuzEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);else machine.clientTick();}
    private void serverTick(ServerLevel server){
        updatePorts(server);chargeBattery();var items=slots.toArray(ItemStack[]::new);tanks[0].loadTank(4,5,items);tanks[1].loadTank(6,7,items);for(int i=4;i<=7;i++)slots.set(i,items[i]);
        if(!hasRocketLoaded()){
            boolean moving=false;for(int i=0;i<positions.length;i++)if(!finishedMoving(i)){moving=true;break;}
            if(!moving)soyuzStatus=SoyuzStatus.ABSENT;loadedType=-1;
        }else loadedType=metadata(slots.get(0));
        if(power>=CONSUMPTION){updateStates();move();power-=CONSUMPTION;}
        for(int i=0;i<=INDEX_STRUT5;i++)if(finishedMoving(i)&&wasMoving(i)){
            var facing=DummyableBlock.facing(getBlockState());var right=facing.getClockWise();
            playSourceSound(worldPosition.getX()+.5-facing.getStepX()*4+right.getStepX()*2,worldPosition.getY()+25,worldPosition.getZ()+.5-facing.getStepZ()*4+right.getStepZ()*2,"hbm:door.sliding_seal_stop",35,.75F);
        }
        sync(300);
    }
    private void clientTick(){
        for(int i=0;i<positions.length;i++){
            prevPositions[i]=positions[i];
            // Exact source shared counter decreases inside the eight-component loop.
            if(turnProgress>0){positions[i]+=(syncPositions[i]-positions[i])/(float)turnProgress;--turnProgress;}
            else positions[i]=syncPositions[i];
        }
        CLIENT_TICK.accept(this);
    }
    private void chargeBattery(){
        if(power<0){power=0;return;}if(power>MAX_POWER){power=MAX_POWER;return;}
        var stack=slots.get(8);if(stack.getItem() instanceof CreativeBatteryItem){power=MAX_POWER;return;}
        if(stack.getItem() instanceof HbmBattery battery){long amount=Math.min(Math.min(MAX_POWER-power,battery.getDischargeRate(stack)),battery.getCharge(stack));power+=amount;battery.dischargeBattery(stack,amount);}
    }
    private void updatePorts(ServerLevel server){
        var available=LaunchpadSoyuzLayout.ports(worldPosition,DummyableBlock.facing(getBlockState())).stream()
            .filter(port->server.hasChunkAt(port.position())&&server.getBlockState(port.position()).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(port.position()))).toList();
        if(!available.equals(boundPorts)||powerPorts.values().stream().anyMatch(PowerNode::isExpired)){
            disconnect();boundPorts=List.copyOf(available);
            for(var port:available){var node=new PowerNode(port.position()).setConnections(new NodeConnection(port.position().relative(port.side()),port.side()));HbmEnergy.nodespace(server).createNode(node);powerPorts.put(port.position(),node);}
            if(!available.isEmpty())fluidPorts=new FluidPorts(available.stream().map(LaunchpadSoyuzLayout.Port::position).toArray(BlockPos[]::new),available.stream().map(port->new NodeConnection(port.position().relative(port.side()),port.side())).toArray(NodeConnection[]::new));
        }
        for(var node:powerPorts.values())if(node.hasValidNet())node.addReceiver(this);if(fluidPorts!=null)fluidPorts.update(server,this);
    }
    private void disconnect(){if(level instanceof ServerLevel server){for(var node:powerPorts.values())HbmEnergy.nodespace(server).destroyNode(node);if(fluidPorts!=null)fluidPorts.remove(server);}powerPorts.clear();fluidPorts=null;boundPorts=List.of();}
    @Override public void setRemoved(){disconnect();CLIENT_REMOVE.accept(this);super.setRemoved();}
    @Override public void onChunkUnloaded(){disconnect();CLIENT_REMOVE.accept(this);super.onChunkUnloaded();}
    private void playSourceSound(double x,double y,double z,String name,float volume,float pitch){if(level instanceof ServerLevel server)server.playSound(null,x,y,z,HbmSounds.get(name),SoundSource.BLOCKS,volume,pitch);}

    // BEGIN ORIGINAL UPDATE_STATES (actual source branches, RNG order and returns)
	public void updateStates() {
		
		if(this.soyuzStatus == SoyuzStatus.ABSENT) {
			
			/// RETURN BEHAVIOR ///
			
			// retract all struts
			if(this.strutStatus == ComponentStatus.DEPLOY) {
				this.strutStatus = ComponentStatus.RETRACT;
				
				for(int i = 0; i <= INDEX_STRUT5; i++) {
					setTarget(i, false, 60 + level.random.nextInt(21)); // 3-4 seconds
				}
			}
			
			// first send away the carriage
			if(this.carriageStatus == ComponentStatus.DEPLOY) {
				this.carriageStatus = ComponentStatus.RETRACT;
				setTarget(INDEX_CARRIAGE, false, 100); // 5 seconds
			}
			
			// once carriage has stopped, tilt, then retract rotor
			if(this.carriageStatus == ComponentStatus.RETRACT && this.finishedMoving(INDEX_CARRIAGE)) {
				// if the carriage has hit the buffer stops, tilt
				if(wasMoving(INDEX_CARRIAGE)) setTarget(INDEX_TILT, true, 3);
				
				// after tilt has returned
				if(this.target[INDEX_TILT] == 0 && this.rotorStatus == ComponentStatus.DEPLOY) {
					this.rotorStatus = ComponentStatus.RETRACT;
					setTarget(INDEX_ROTOR, false, 100); // 5 seconds
				}
			}

			// return tilt
			if(this.target[INDEX_TILT] > 0 && this.finishedMoving(INDEX_TILT)) {
				setTarget(INDEX_TILT, false, 3);
			}
			
			/// DEPLOY BEHAVIOR ///
			
			// start if rocket is loaded and all relevant components are fully retracted
			if(this.hasRocketLoaded() &&
					this.carriageStatus == ComponentStatus.RETRACT && this.finishedMoving(INDEX_CARRIAGE) &&
					this.rotorStatus == ComponentStatus.RETRACT && this.finishedMoving(INDEX_ROTOR)) {
				
				// deploy carriage
				this.carriageStatus = ComponentStatus.DEPLOY;
				setTarget(INDEX_CARRIAGE, true, 200); // 10 seconds

				this.soyuzStatus = SoyuzStatus.LOADING;
				return; // always return on status change
			}
		}
		
		if(this.soyuzStatus == SoyuzStatus.LOADING) {
			
			// once carriage has returned, start rotor and lift the rocket onto the launch pad
			if(this.rotorStatus == ComponentStatus.RETRACT && this.finishedMoving(INDEX_CARRIAGE)) {
				this.rotorStatus = ComponentStatus.DEPLOY;
				setTarget(INDEX_ROTOR, true, 200); // 10 seconds
			}
			
			if(this.carriageStatus == ComponentStatus.DEPLOY && this.finishedMoving(INDEX_CARRIAGE) &&
					this.rotorStatus == ComponentStatus.DEPLOY && this.finishedMoving(INDEX_ROTOR)) {

				// once carriage and rotor are deployed, extend struts
				if(this.strutStatus == ComponentStatus.RETRACT) {
					this.strutStatus = ComponentStatus.DEPLOY;
					
					for(int i = 0; i <= INDEX_STRUT5; i++) {
						setTarget(i, true, 60 + level.random.nextInt(21)); // 3-4 seconds
					}
				} else {

					// if struts are fully deployed, the rocket is in place, start fueling
					boolean strutsDeployed = true;
					for(int i = 0; i <= INDEX_STRUT5; i++) {
						if(!this.finishedMoving(i)) strutsDeployed = false;
					}
					
					if(strutsDeployed) {
						this.fuelCountdown = FUEL_DURATION;
						this.soyuzStatus = SoyuzStatus.FUELING;
						return; // always return on status change
					}
				}
			}
		}
		
		if(this.soyuzStatus == SoyuzStatus.FUELING) {
			
			if(this.hasAllFuel()) {
				if(this.fuelCountdown > 0) {
					this.fuelCountdown--;
				} else {
					this.soyuzStatus = SoyuzStatus.READY;
					return; // always return on status change
				}
			}
		}
		
		if(this.soyuzStatus == SoyuzStatus.READY) {
			
			// should the fuel somehow not be present during this phase, reset phase back to fueling
			if(!this.hasAllFuel()) {
				this.fuelCountdown = FUEL_DURATION;
				this.soyuzStatus = SoyuzStatus.FUELING;
				return; // always return on status change
			}
		}
		
		if(this.soyuzStatus == SoyuzStatus.LAUNCHING) {
			
			// return carriage
			if(this.carriageStatus == ComponentStatus.DEPLOY) {
				this.carriageStatus = ComponentStatus.RETRACT;
				this.setTarget(INDEX_CARRIAGE, false, 100); // 5 seconds
				
			// return rotor
			}
			
			if(this.carriageStatus == ComponentStatus.RETRACT && this.finishedMoving(INDEX_CARRIAGE)) {
				if(wasMoving(INDEX_CARRIAGE)) setTarget(INDEX_TILT, true, 3);
				
				if(this.target[INDEX_TILT] == 0 && this.rotorStatus == ComponentStatus.DEPLOY) {
					this.rotorStatus = ComponentStatus.RETRACT;
					setTarget(INDEX_ROTOR, false, 100); // 5 seconds
				}
			}

			if(this.target[INDEX_TILT] > 0 && this.finishedMoving(INDEX_TILT)) {
				setTarget(INDEX_TILT, false, 3);
			}
			
			if(this.countdown > 0) {
				this.countdown--;
				
				if(countdown % 100 == 0 && countdown > 0) playSourceSound(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), "hbm:alarm.hatch", 100F, 1.1F);

				if(countdown == 20) playSourceSound(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), "hbm:entity.soyuzTakeoff", 100F, 1.1F);
				
			} else {
				
				if(canLaunch()) {
					this.soyuzStatus = SoyuzStatus.ABSENT;
					this.liftOff();
				} else {
					this.soyuzStatus = SoyuzStatus.READY;
				}
				
				for(int i = 0; i <= INDEX_STRUT5; i++) {
					setTarget(i, false, 18 + level.random.nextInt(8)); //1 second
				}
			}
		}
	}
	
    // END ORIGINAL UPDATE_STATES

    public boolean hasRocketLoaded(){return slots.get(0).is(HbmContent.item("missile_soyuz"));}
    public boolean finishedMoving(int index){return positions[index]==target[index];}
    public boolean wasMoving(int index){return positions[index]!=prevPositions[index];}
    public boolean hasAllFuel(){return hasJetFuel()&&hasOxidizer();}
    public boolean hasJetFuel(){return tanks[0].getFill()>=100_000;}
    public boolean hasOxidizer(){return tanks[1].getFill()>=100_000;}
    public void setTarget(int index,boolean deploy,int duration){target[index]=deploy?1F:0F;speed[index]=1F/duration;}
    public void move(){for(int i=0;i<positions.length;i++){prevPositions[i]=positions[i];if(Math.abs(positions[i]-target[i])<=speed[i])positions[i]=target[i];else if(positions[i]<target[i])positions[i]+=speed[i];else positions[i]-=speed[i];}}
    public float getInterpPos(int index,float partial){return prevPositions[index]+(positions[index]-prevPositions[index])*partial;}
    public boolean canLaunch(){
        if(loadedType<0||!hasAllFuel()||power<CONSUMPTION)return false;
        if(cargoMode){if(slots.get(1).isEmpty())return false;for(int i=9;i<27;i++)if(!slots.get(i).isEmpty())return true;return false;}
        return orbital()!=1&&!slots.get(2).isEmpty();
    }
    public Vec3 enginePosition(){var dir=DummyableBlock.facing(getBlockState());var rot=dir.getClockWise();return new Vec3(worldPosition.getX()+.5-dir.getStepX()*4-rot.getStepX()*4,worldPosition.getY()+4,worldPosition.getZ()+.5-dir.getStepZ()*4-rot.getStepZ()*4);}
    public void liftOff(){
        if(!(level instanceof ServerLevel server))return;var center=enginePosition();var rocket=new EntityRocketSoyuz(level);rocket.setSkin(loadedType);rocket.mode=cargoMode?1:0;rocket.moveTo(center.x,center.y,center.z,0,0);server.addFreshEntity(rocket);
        tanks[0].setFill(tanks[0].getFill()-100_000);tanks[1].setFill(tanks[1].getFill()-100_000);
        if(!cargoMode){rocket.setSat(slots.get(2));if(orbital()==2)slots.set(3,ItemStack.EMPTY);slots.set(2,ItemStack.EMPTY);}
        else{var payload=new ArrayList<ItemStack>();for(int i=9;i<27;i++){payload.add(slots.get(i));slots.set(i,ItemStack.EMPTY);}var data=CrateStackData.data(slots.get(1));rocket.targetX=data.getInt("xCoord");rocket.targetZ=data.getInt("zCoord");rocket.setPayload(payload);}
        slots.set(0,ItemStack.EMPTY);setChanged();
    }
    public int orbital(){if(cargoMode)return 0;if(needsOrbiter(slots.get(2)))return slots.get(3).is(HbmContent.item("missile_soyuz_lander"))?2:1;return 0;}
    private static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
    public static boolean needsOrbiter(ItemStack stack){return !stack.isEmpty()&&(stack.is(HbmContent.item("sat_gerald"))||metadata(stack)==SatelliteKind.MINER_LUNAR.ordinal());}
    private boolean fluidContainer(ItemStack stack,FluidTank tank){return stack.is(FluidItemContent.INFINITE.get())||FluidContainerRegistry.getFluidContent(stack,tank.getTankType())>0||stack.getItem() instanceof FillableItem fillable&&fillable.providesFluid(tank.getTankType(),stack);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){
        if(slot==0)return stack.is(HbmContent.item("missile_soyuz"));if(slot==1)return stack.getItem() instanceof IDesignatorItem;
        if(slot==2)return stack.getItem() instanceof SatelliteFrequency&&!cargoMode;if(slot==3)return stack.is(HbmContent.item("missile_soyuz_lander"))&&!cargoMode;
        if(slot==4)return fluidContainer(stack,tanks[0]);if(slot==5)return false;if(slot==6)return fluidContainer(stack,tanks[1]);if(slot==7)return false;
        if(slot==8)return stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem;
        if(slot>8){if(!cargoMode)return false;for(int i=0;i<=3;i++)if(canPlaceItem(i,stack))return false;return true;}return true;
    }
    @Override public int[] getSlotsForFace(Direction side){return new int[]{0,2,3,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26};}
    public void receiveControl(Player player,CompoundTag data){if(!stillValid(player))return;if(data.contains("cargo")){cargoMode=data.getBoolean("cargo");setChanged();}if(data.contains("launch")&&soyuzStatus==SoyuzStatus.READY&&canLaunch()){soyuzStatus=SoyuzStatus.LAUNCHING;countdown=COUNTDOWN_DURATION;setChanged();}}
    @Override protected void saveMachine(CompoundTag tag){
        tag.putLong("power",power);tanks[0].writeToNBT(tag,"tank0");tanks[1].writeToNBT(tag,"tank1");
        for(int i=0;i<8;i++){tag.putFloat("p"+i,positions[i]);tag.putFloat("s"+i,speed[i]);tag.putFloat("t"+i,target[i]);}
        tag.putInt("soyuzStatus",soyuzStatus.ordinal());tag.putInt("strutStatus",strutStatus.ordinal());tag.putInt("carriageStatus",carriageStatus.ordinal());tag.putInt("rotorStatus",rotorStatus.ordinal());tag.putBoolean("cargoMode",cargoMode);tag.putInt("fuelCountdown",fuelCountdown);tag.putInt("countdown",countdown);tag.putBoolean("tilted",tilted);
    }
    private static <T>T sourceEnum(T[] values,int ordinal){return values[Math.abs(ordinal%values.length)];}
    @Override protected void loadMachine(CompoundTag tag){
        if(!tag.contains("power"))return;power=tag.getLong("power");tanks[0].readFromNBT(tag,"tank0");tanks[1].readFromNBT(tag,"tank1");
        for(int i=0;i<8;i++){positions[i]=tag.getFloat("p"+i);speed[i]=tag.getFloat("s"+i);target[i]=tag.getFloat("t"+i);}
        soyuzStatus=sourceEnum(SoyuzStatus.values(),tag.getInt("soyuzStatus"));strutStatus=sourceEnum(ComponentStatus.values(),tag.getInt("strutStatus"));carriageStatus=sourceEnum(ComponentStatus.values(),tag.getInt("carriageStatus"));rotorStatus=sourceEnum(ComponentStatus.values(),tag.getInt("rotorStatus"));cargoMode=tag.getBoolean("cargoMode");fuelCountdown=tag.getInt("fuelCountdown");countdown=tag.getInt("countdown");tilted=tag.getBoolean("tilted");
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){
        var tag=new CompoundTag();tanks[0].writeToNBT(tag,"tank0");tanks[1].writeToNBT(tag,"tank1");tag.putLong("power",power);tag.putInt("loadedType",loadedType);tag.putBoolean("cargoMode",cargoMode);tag.putInt("countdown",countdown);tag.putByte("soyuzStatus",(byte)soyuzStatus.ordinal());for(int i=0;i<8;i++)tag.putFloat("p"+i,positions[i]);tag.putBoolean("muffled",isMuffled());tag.putBoolean("tilted",tilted);return tag;
    }
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){
        tanks[0].readFromNBT(tag,"tank0");tanks[1].readFromNBT(tag,"tank1");power=tag.getLong("power");loadedType=tag.getInt("loadedType");cargoMode=tag.getBoolean("cargoMode");countdown=tag.getInt("countdown");soyuzStatus=sourceEnum(SoyuzStatus.values(),tag.getByte("soyuzStatus"));setMuffled(tag.getBoolean("muffled"));tilted=tag.getBoolean("tilted");
        for(int i=0;i<8;i++){float next=tag.getFloat("p"+i);if(syncPositions[i]!=next){syncPositions[i]=next;turnProgress=3;}}
    }
    @Override public long getPower(){return power;}@Override public long getMaxPower(){return MAX_POWER;}@Override public void setPower(long value){power=value;setChanged();}
    @Override public boolean canConnect(Direction side){return side!=null;}@Override public boolean isLoaded(){return level!=null&&!isRemoved();}
    @Override public long energyStored(){return power;}@Override public long energyCapacity(){return MAX_POWER;}
    @Override public FluidTank[] getReceivingTanks(){return tanks;}@Override public FluidTank[] getAllTanks(){return tanks;}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long remainder=StandardFluidReceiver.super.transferFluid(type,pressure,amount);if(remainder!=amount)setChanged();return remainder;}
    @Override public List<TankView> tanks(){return Arrays.stream(tanks).map(tank->new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill())).toList();}
}
