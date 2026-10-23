package com.hbm.machine.lambda;

import com.hbm.energy.*;
import com.hbm.entity.rocket.EntityRocketLambda;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.battery.*;
import com.hbm.item.fluid.FluidItemContent;
import com.hbm.machine.foundry.crucible.ThermalMachine;
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

/** Original five-component sequencer and seven-slot satellite carrier; source consumes 10kHE on every powered tick. */
public final class LambdaEntity extends ThermalMachine implements EnergyReceiver,SidedEnergyConnector,StandardFluidReceiver {
    public static final long MAX_POWER=1_000_000,CONSUMPTION=10_000;
    public static final int DOORS=0,ERECTOR=1,ROTOR=2,CLAMPS=3,PISTONS=4,COUNTDOWN_DURATION=200;
    public final FluidTank[] tanks={new FluidTank(HbmFluids.GASOLINE_LEADED,64_000),new FluidTank(HbmFluids.PEROXIDE,64_000)};
    public final float[] positions=new float[5],prevPositions=new float[5],speed=new float[5],target=new float[5],syncPositions=new float[5];
    public long power;public boolean erected,erecting,autolaunch,tilted;public int animationProgress,animationDelay,countdown;
    private boolean clientRocketLoaded;private int turnProgress;
    private final Map<BlockPos,PowerNode> powerPorts=new LinkedHashMap<>();private FluidPorts fluidPorts;private List<BlockPos> boundPorts=List.of();
    public static Consumer<LambdaEntity> CLIENT_TICK=machine->{},CLIENT_REMOVE=machine->{};
    public LambdaEntity(BlockPos pos,BlockState state){super(LambdaContent.ENTITY.get(),pos,state,7);}
    @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}
    @Override protected String title(){return "container.launchpadLambda";}
    @Override public double getUseRange(){return 24;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new LambdaMenu(id,inventory,this,worldPosition);}
    @Override public void writeClientSideData(AbstractContainerMenu menu,net.minecraft.network.RegistryFriendlyByteBuf buffer){buffer.writeBlockPos(worldPosition);}
    public static void tick(Level level,BlockPos pos,BlockState state,LambdaEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);else machine.clientTick();}
    private void serverTick(ServerLevel server){
        updatePorts(server);chargeBattery();var items=slots.toArray(ItemStack[]::new);tanks[0].loadTank(2,3,items);tanks[1].loadTank(4,5,items);for(int i=0;i<items.length;i++)slots.set(i,items[i]);
        if(!hasRocketLoaded()){erected=false;erecting=false;countdown=0;}
        if(power>=CONSUMPTION){updateStates();move();power-=CONSUMPTION;if(autolaunch&&erected&&canLaunch()&&countdown<=0)countdown=COUNTDOWN_DURATION;}
        sync(300);
    }
    private void clientTick(){for(int i=0;i<positions.length;i++){prevPositions[i]=positions[i];if(turnProgress>0){positions[i]+=(syncPositions[i]-positions[i])/(float)turnProgress;--turnProgress;}else positions[i]=syncPositions[i];}CLIENT_TICK.accept(this);}
    private void chargeBattery(){if(power<0){power=0;return;}if(power>MAX_POWER){power=MAX_POWER;return;}var stack=slots.get(6);if(stack.getItem() instanceof CreativeBatteryItem){power=MAX_POWER;return;}if(stack.getItem() instanceof HbmBattery battery){long amount=Math.min(Math.min(MAX_POWER-power,battery.getDischargeRate(stack)),battery.getCharge(stack));power+=amount;battery.dischargeBattery(stack,amount);}}
    private void updatePorts(ServerLevel server){
        var facing=DummyableBlock.facing(getBlockState());var available=new ArrayList<BlockPos>();for(var p:LambdaLayout.ports(worldPosition,facing))if(server.hasChunkAt(p)&&server.getBlockState(p).is(LambdaContent.BLOCK.get())&&DummyableBlock.isPort(server.getBlockState(p)))available.add(p);
        if(!available.equals(boundPorts)||powerPorts.values().stream().anyMatch(PowerNode::isExpired)){disconnect();boundPorts=List.copyOf(available);for(var p:available){var node=new PowerNode(p).setConnections(new NodeConnection(p.relative(facing),facing));HbmEnergy.nodespace(server).createNode(node);powerPorts.put(p,node);}if(!available.isEmpty())fluidPorts=new FluidPorts(available.toArray(BlockPos[]::new),available.stream().map(p->new NodeConnection(p.relative(facing),facing)).toArray(NodeConnection[]::new));}
        for(var node:powerPorts.values())if(node.hasValidNet())node.addReceiver(this);if(fluidPorts!=null)fluidPorts.update(server,this);
    }
    private void disconnect(){if(level instanceof ServerLevel server){for(var node:powerPorts.values())HbmEnergy.nodespace(server).destroyNode(node);if(fluidPorts!=null)fluidPorts.remove(server);}powerPorts.clear();fluidPorts=null;boundPorts=List.of();}
    @Override public void setRemoved(){disconnect();CLIENT_REMOVE.accept(this);super.setRemoved();}
    @Override public void onChunkUnloaded(){disconnect();CLIENT_REMOVE.accept(this);super.onChunkUnloaded();}
    public void updateStates(){
        if(finishedAllMoving()&&erecting&&!erected){
            if(erectorDown()&&doorsClosed()){setTarget(DOORS,3,3,60);setTarget(PISTONS,0,1,1);setTarget(CLAMPS,0,1,1);setTarget(ROTOR,180,180,1);}
            else if(erectorDown()&&doorsOpen()){animationDelay=20;setTarget(ERECTOR,27,27,100);}
            else if(erectorUp()&&rotorDown()){animationDelay=20;setTarget(ROTOR,0,180,60);setTarget(DOORS,0,3,60);}
            else if(erectorUp()&&rotorUp()&&doorsClosed()){animationDelay=20;setTarget(ERECTOR,25,2,60);}
            else if(erectorCenter()&&rotorUp()&&doorsClosed()){erected=true;setChanged();}else{erecting=false;setChanged();}
        }
        if(erected&&countdown<=20&&countdown>0){erecting=false;setChanged();}
        if(finishedAllMoving()&&!erecting){setTarget(DOORS,0,3,60);if(clampsOn())setTarget(PISTONS,.75F,.75F,20);else if(clampsOff()&&clampsDown()){animationDelay=10;setTarget(CLAMPS,90,90,40);}else if(clampsUp()&&!erectorDown()){animationDelay=20;setTarget(ERECTOR,0,25,100);}else if(erectorDown()&&doorsClosed()&&hasRocketLoaded()){erecting=true;setChanged();}}
        if(erected&&countdown>0){countdown--;if(countdown<=0){setChanged();level.playSound(null,worldPosition,HbmSounds.get("entity.soyuzTakeoff"),SoundSource.BLOCKS,100,1.1F);liftOff();}}
    }
    public void liftOff(){if(!(level instanceof ServerLevel server))return;var rocket=new EntityRocketLambda(server);rocket.moveTo(worldPosition.getX()+.5,worldPosition.getY()+2,worldPosition.getZ()+.5,0,0);server.addFreshEntity(rocket);tanks[0].setFill(tanks[0].getFill()-24_000);tanks[1].setFill(tanks[1].getFill()-24_000);rocket.setSat(slots.get(1));slots.set(0,ItemStack.EMPTY);slots.set(1,ItemStack.EMPTY);erected=false;setChanged();}
    public void setTarget(int index,float value,float span,int duration){if(span<=0)span=1;target[index]=value;speed[index]=span/duration;}
    public void move(){if(animationDelay>0)animationDelay--;for(int i=0;i<positions.length;i++){prevPositions[i]=positions[i];if(animationDelay>0)continue;if(Math.abs(positions[i]-target[i])<=speed[i])positions[i]=target[i];else if(positions[i]<target[i])positions[i]+=speed[i];else positions[i]-=speed[i];}}
    public boolean doorsClosed(){return positions[DOORS]==0;}public boolean doorsOpen(){return positions[DOORS]==3;}public boolean erectorDown(){return positions[ERECTOR]==0;}public boolean erectorCenter(){return positions[ERECTOR]==25;}public boolean erectorUp(){return positions[ERECTOR]==27;}public boolean rotorUp(){return positions[ROTOR]==0;}public boolean rotorDown(){return positions[ROTOR]==180;}public boolean clampsOn(){return positions[PISTONS]==0;}public boolean clampsOff(){return positions[PISTONS]==.75F;}public boolean clampsUp(){return positions[CLAMPS]==90;}public boolean clampsDown(){return positions[CLAMPS]==0;}
    public boolean finishedMoving(int index){return positions[index]==target[index];}public boolean wasMoving(int index){return positions[index]!=prevPositions[index];}public boolean finishedAllMoving(){for(int i=0;i<positions.length;i++)if(!finishedMoving(i))return false;return true;}public float getInterpPos(int index,float partial){return prevPositions[index]+(positions[index]-prevPositions[index])*partial;}
    public boolean hasRocketLoaded(){return level!=null&&level.isClientSide?clientRocketLoaded:slots.get(0).is(HbmContent.item("missile_lambda"));}
    public static boolean needsOrbiter(ItemStack stack){return !stack.isEmpty()&&(stack.is(HbmContent.item("sat_gerald"))||stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue())==SatelliteKind.MINER_LUNAR.ordinal());}
    public boolean hasJetFuel(){return tanks[0].getFill()>=24_000;}public boolean hasOxidizer(){return tanks[1].getFill()>=24_000;}public boolean hasAllFuel(){return hasJetFuel()&&hasOxidizer();}
    public boolean canLaunch(){return hasRocketLoaded()&&hasAllFuel()&&power>=CONSUMPTION&&!slots.get(1).isEmpty()&&canPlaceItem(1,slots.get(1))&&!needsOrbiter(slots.get(1));}
    private boolean fluidContainer(ItemStack stack,FluidTank tank){return stack.is(FluidItemContent.INFINITE.get())||FluidContainerRegistry.getFluidContent(stack,tank.getTankType())>0||stack.getItem() instanceof FillableItem fillable&&fillable.providesFluid(tank.getTankType(),stack);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return switch(slot){case 0->stack.is(HbmContent.item("missile_lambda"));case 1->stack.getItem() instanceof SatelliteFrequency&&!needsOrbiter(stack);case 2->fluidContainer(stack,tanks[0]);case 4->fluidContainer(stack,tanks[1]);case 3,5->false;case 6->stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem;default->true;};}
    @Override public int[] getSlotsForFace(Direction side){return new int[]{0,1};}
    @Override protected void saveMachine(CompoundTag tag){tag.putLong("power",power);tanks[0].writeToNBT(tag,"tank0");tanks[1].writeToNBT(tag,"tank1");for(int i=0;i<5;i++){tag.putFloat("p"+i,positions[i]);tag.putFloat("s"+i,speed[i]);tag.putFloat("t"+i,target[i]);}tag.putBoolean("tilted",tilted);tag.putBoolean("erected",erected);tag.putBoolean("erecting",erecting);tag.putInt("animationProgress",animationProgress);tag.putInt("animationDelay",animationDelay);tag.putBoolean("autolaunch",autolaunch);tag.putInt("countdown",countdown);}
    @Override protected void loadMachine(CompoundTag tag){if(!tag.contains("power"))return;power=tag.getLong("power");tanks[0].readFromNBT(tag,"tank0");tanks[1].readFromNBT(tag,"tank1");for(int i=0;i<5;i++){positions[i]=tag.getFloat("p"+i);speed[i]=tag.getFloat("s"+i);target[i]=tag.getFloat("t"+i);}tilted=tag.getBoolean("tilted");erected=tag.getBoolean("erected");erecting=tag.getBoolean("erecting");animationProgress=tag.getInt("animationProgress");animationDelay=tag.getInt("animationDelay");autolaunch=tag.getBoolean("autolaunch");countdown=tag.getInt("countdown");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putBoolean("rocketLoaded",hasRocketLoaded());return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){setMuffled(tag.getBoolean("muffled"));power=tag.getLong("power");tanks[0].readFromNBT(tag,"tank0");tanks[1].readFromNBT(tag,"tank1");erecting=tag.getBoolean("erecting");tilted=tag.getBoolean("tilted");erected=tag.getBoolean("erected");autolaunch=tag.getBoolean("autolaunch");countdown=tag.getInt("countdown");clientRocketLoaded=tag.getBoolean("rocketLoaded");for(int i=0;i<5;i++){target[i]=tag.getFloat("t"+i);float value=tag.getFloat("p"+i);if(syncPositions[i]!=value){syncPositions[i]=value;turnProgress=3;}}}
    @Override public long getPower(){return power;}@Override public long getMaxPower(){return MAX_POWER;}@Override public void setPower(long value){power=value;setChanged();}@Override public boolean canConnect(Direction side){return side!=null;}@Override public boolean isLoaded(){return level!=null&&!isRemoved();}
    @Override public long energyStored(){return power;}@Override public long energyCapacity(){return MAX_POWER;}
    @Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return tanks;}@Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rest=StandardFluidReceiver.super.transferFluid(type,pressure,amount);if(rest!=amount)setChanged();return rest;}
    @Override public java.util.List<TankView> tanks(){return Arrays.stream(tanks).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}
    @Override public java.util.List<ProgressView> progressBars(){return erected&&countdown>0?List.of(new ProgressView("gui.hbm.lambda.countdown",COUNTDOWN_DURATION-countdown,COUNTDOWN_DURATION,0xFF0000)):List.of();}
}
