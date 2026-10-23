package com.hbm.machine.oilflare;

import api.hbm.redstoneoverradio.*;
import com.hbm.api.machine.*;
import com.hbm.config.GeneralModes;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.battery.*;
import com.hbm.machine.crystallizer.EffectUpgradeItem;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.particle.LegacyFluidEffects;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Original GasFlare6-slot state machine, source vent/burn order and independent HE port topology. */
public final class FlareEntity extends ThermalMachine implements EnergyProvider,SidedEnergyConnector,StandardFluidReceiver,IFluidCopiable,IRORInteractive,IRORValueProvider {
    public static final BigInteger CAPACITY=BigInteger.valueOf(100_000);
    public static Consumer<FlareEntity> clientTick=flare->{};
    public final FluidTank tank=new FluidTank(HbmFluids.fromName("GAS"),64000);
    public BigInteger power=BigInteger.ZERO,output=BigInteger.ZERO;
    public boolean isOn,doesBurn,tilted;
    public int fluidUsed,speedLevel,effectLevel;
    private int tiltBlocksChecked,tiltBlocksValid;
    private final Map<BlockPos,PowerNode> powerPorts=new LinkedHashMap<>();
    private FluidPorts fluidPorts;
    public FlareEntity(BlockPos pos,BlockState state){super(FlareContent.ENTITY.get(),pos,state,6);}
    @Override protected String title(){return "container.gasFlare";}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new FlareMenu(id,inventory,this,worldPosition);}
    public static BlockPos[] portPositions(BlockPos core){return new BlockPos[]{core.north(),core.east(),core.south(),core.west()};}
    public static Direction portSide(BlockPos core,BlockPos port){return port.getX()<core.getX()?Direction.WEST:port.getX()>core.getX()?Direction.EAST:port.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH;}
    private void updatePorts(ServerLevel server){var active=new ArrayList<BlockPos>();for(var pos:portPositions(worldPosition)){if(!server.hasChunkAt(pos)||!server.getBlockState(pos).is(getBlockState().getBlock())||!DummyableBlock.isPort(server.getBlockState(pos)))continue;active.add(pos);var node=powerPorts.get(pos);if(node==null||node.isExpired()){if(node!=null)HbmEnergy.nodespace(server).destroyNode(node);var side=portSide(worldPosition,pos);node=new PowerNode(pos).setConnections(new NodeConnection(pos.relative(side),side));HbmEnergy.nodespace(server).createNode(node);powerPorts.put(pos,node);}if(node.hasValidNet())node.addProvider(this);}for(var pos:List.copyOf(powerPorts.keySet()))if(!active.contains(pos))HbmEnergy.nodespace(server).destroyNode(powerPorts.remove(pos));if(fluidPorts==null){var positions=portPositions(worldPosition);var connections=Arrays.stream(positions).map(pos->{var side=portSide(worldPosition,pos);return new NodeConnection(pos.relative(side),side);}).toArray(NodeConnection[]::new);fluidPorts=new FluidPorts(positions,connections);}fluidPorts.update(server,this);}
    public static boolean gaseous(HbmFluidType type){return type.hasTrait("gaseous")||type.hasTrait("gaseous_art");}
    public static BigInteger production(HbmFluidType type,int amount,int effect){var trait=type.getTrait(FluidTraits.Flammable.class);if(trait==null)return BigInteger.ZERO;var base=BigInteger.valueOf(trait.getHeatEnergy()).multiply(BigInteger.valueOf(amount)).divide(BigInteger.valueOf(1000)).divide(BigInteger.valueOf(gaseous(type)?5:10));return base.add(base.multiply(BigInteger.valueOf(effect)).divide(BigInteger.valueOf(3)));}
    private void upgrades(){speedLevel=effectLevel=0;for(int i=4;i<=5;i++){var item=slots.get(i).getItem();if(item instanceof FurnaceUpgradeItem upgrade&&upgrade.type()==FurnaceUpgradeItem.Type.SPEED)speedLevel+=upgrade.tier();else if(item instanceof EffectUpgradeItem upgrade)effectLevel+=upgrade.tier();}speedLevel=Math.min(3,speedLevel);effectLevel=Math.min(3,effectLevel);}
    public static boolean isUpgradeItem(ItemStack stack){var id=BuiltInRegistries.ITEM.getKey(stack.getItem());return id.getNamespace().equals("hbm")&&Set.of("upgrade_speed_1","upgrade_speed_2","upgrade_speed_3","upgrade_effect_1","upgrade_effect_2","upgrade_effect_3","upgrade_power_1","upgrade_power_2","upgrade_power_3","upgrade_fortune_1","upgrade_fortune_2","upgrade_fortune_3","upgrade_afterburn_1","upgrade_afterburn_2","upgrade_afterburn_3","upgrade_overdrive_1","upgrade_overdrive_2","upgrade_overdrive_3","upgrade_radius","upgrade_health","upgrade_smelter","upgrade_shredder","upgrade_centrifuge","upgrade_crystallizer","upgrade_nullifier","upgrade_screm","upgrade_gc_speed","upgrade_5g").contains(id.getPath());}
    public static void tick(Level level,BlockPos pos,BlockState state,FlareEntity flare){if(level instanceof ServerLevel server)flare.serverTick(server);else clientTick.accept(flare);}
    private void serverTick(ServerLevel server){updatePorts(server);checkFoundation(server);fluidUsed=0;output=BigInteger.ZERO;var items=slots.toArray(ItemStack[]::new);tank.setType(3,items);tank.loadTank(1,2,items);slots.set(1,items[1]);slots.set(2,items[2]);if(isOn&&tank.getFill()>0&&!tilted){upgrades();var type=tank.getTankType();if(!doesBurn||!type.hasTrait(FluidTraits.Flammable.class)){if(gaseous(type)){int eject=Math.min(50*(1+speedLevel),tank.getFill());fluidUsed=eject;tank.setFill(tank.getFill()-eject);// FluidType.onFluidRelease is an empty original hook; trait release remains the separate 5-tick dispatch below.
                    if(server.getGameTime()%7==0)sound(server,SoundEvents.LAVA_EXTINGUISH,1.5F,.5F);if(server.getGameTime()%5==0&&eject>0)type.release(server,worldPosition,tank,eject*5,FluidTraits.Release.SPILL);}}
            else{int eject=Math.min(10*(1+speedLevel),tank.getFill());fluidUsed=eject;tank.setFill(tank.getFill()-eject);output=production(type,eject,effectLevel);power=power.add(output).min(CAPACITY);LegacyFluidEffects.gasFlame(server,worldPosition.getX()+.5,worldPosition.getY()+11.75,worldPosition.getZ()+.5,server.random.nextGaussian()*.15,.2,server.random.nextGaussian()*.15);for(var entity:server.getEntities((net.minecraft.world.entity.Entity)null,new AABB(worldPosition.getX()-1,worldPosition.getY()+12,worldPosition.getZ()-2,worldPosition.getX()+2,worldPosition.getY()+17,worldPosition.getZ()+2))){entity.igniteForSeconds(5);entity.hurt(server.damageSources().onFire(),5F);}if(server.getGameTime()%3==0)sound(server,com.hbm.registry.HbmSounds.get("weapon.flamethrowerShoot"),1.5F,.75F);if(server.getGameTime()%5==0&&eject>0)type.release(server,worldPosition,tank,eject*5,FluidTraits.Release.BURN);}}
        chargeBattery();sync(50);}
    private void sound(ServerLevel level,SoundEvent sound,float volume,float pitch){level.playSound(null,worldPosition.getX(),worldPosition.getY()+11,worldPosition.getZ(),sound,SoundSource.BLOCKS,isMuffled()?volume*.1F:volume,pitch);}
    private void chargeBattery(){if(power.signum()<0){power=BigInteger.ZERO;return;}if(power.compareTo(CAPACITY)>0){power=CAPACITY;return;}var stack=slots.getFirst();if(stack.getItem() instanceof CreativeBatteryItem){power=BigInteger.ZERO;return;}if(stack.getItem() instanceof HbmBattery battery){long available=Math.min(battery.getChargeRate(stack),battery.getMaxCharge(stack)-battery.getCharge(stack));long amount=power.min(BigInteger.valueOf(available)).longValueExact();battery.chargeBattery(stack,amount);power=power.subtract(BigInteger.valueOf(amount));}}
    /** Source CONFIG gravity, four standardFloor3x3 corners, one probe every20ticks and95% support threshold. */
    private void checkFoundation(ServerLevel server){if(!GeneralModes.machineGravity()){tilted=false;return;}int identity=(worldPosition.getY()+worldPosition.getZ()*27644437)*27644437+worldPosition.getX();if((server.getGameTime()+identity)%20!=0)return;if(tiltBlocksChecked>=4){boolean next=tiltBlocksValid<tiltBlocksChecked*.95;if(next&&!tilted)server.playSound(null,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,com.hbm.registry.HbmSounds.get("block.metalImpact"),SoundSource.BLOCKS,3,1);tilted=next;setChanged();tiltBlocksChecked=tiltBlocksValid=0;}var pos=worldPosition.offset(-1+(tiltBlocksChecked/2)*2,-1,-1+(tiltBlocksChecked%2)*2);tiltBlocksChecked++;var state=server.getBlockState(pos);var id=BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();if(state.isFaceSturdy(server,pos,Direction.UP)&&!com.hbm.block.fluid.LegacyMaterials.of(state).name().equals("sand")&&!Set.of("hbm:dirt_dead","hbm:dirt_oily","hbm:stone_cracked").contains(id))tiltBlocksValid++;}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public boolean canConnect(Direction side){return side!=null&&side.getAxis().isHorizontal();}
    @Override public boolean canConnect(HbmFluidType type,Direction side){return canConnect(side);}
    @Override public ConnectionPriority getFluidPriority(){return ConnectionPriority.LOW;}
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{tank};}
    @Override public FluidTank[] getReceivingTanks(){return getAllTanks();}
    @Override public BigInteger getPowerExact(){return power;}
    @Override public BigInteger getMaxPowerExact(){return CAPACITY;}
    @Override public BigInteger getProviderSpeedExact(){return CAPACITY;}
    @Override public long getPower(){return power.min(BigInteger.valueOf(Long.MAX_VALUE)).max(BigInteger.valueOf(Long.MIN_VALUE)).longValue();}
    @Override public long getMaxPower(){return 100_000;}
    @Override public void setPowerExact(BigInteger value){power=value;setChanged();}
    @Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}
    @Override public void usePowerExact(BigInteger amount){if(amount.signum()<0||amount.compareTo(power)>0)throw new IllegalArgumentException("Unavailable HE requested");power=power.subtract(amount);setChanged();}
    @Override public long energyStored(){return getPower();}
    @Override public long energyCapacity(){return getMaxPower();}
    @Override public List<TankView> tanks(){return List.of(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill()));}
    @Override public List<ProgressView> progressBars(){return List.of(new ProgressView("gui.hbm.flare.flow",fluidUsed,50*(1+speedLevel),0x62A9DC),new ProgressView("gui.hbm.flare.output",output.doubleValue(),Math.max(1,production(tank.getTankType(),10*(1+speedLevel),effectLevel).doubleValue()),0xe9ba4b));}
    @Override protected void saveMachine(CompoundTag tag){tag.putString("powerExact",power.toString());tag.putLong("powerTime",getPower());tank.writeToNBT(tag,"gas");tag.putBoolean("isOn",isOn);tag.putBoolean("doesBurn",doesBurn);tag.putBoolean("tilted",tilted);}
    @Override protected void loadMachine(CompoundTag tag){power=tag.contains("powerExact",Tag.TAG_STRING)?new BigInteger(tag.getString("powerExact")):BigInteger.valueOf(tag.getLong("powerTime"));tank.readFromNBT(tag,"gas");isOn=tag.getBoolean("isOn");doesBurn=tag.getBoolean("doesBurn");tilted=tag.getBoolean("tilted");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putInt("fluidUsed",fluidUsed);tag.putString("output",output.toString());tag.putInt("speedLevel",speedLevel);tag.putInt("effectLevel",effectLevel);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);fluidUsed=tag.getInt("fluidUsed");output=tag.contains("output")?new BigInteger(tag.getString("output")):BigInteger.ZERO;speedLevel=tag.getInt("speedLevel");effectLevel=tag.getInt("effectLevel");}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}
    @Override public int[] getSlotsForFace(Direction side){return new int[0];}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}
    @Override public int[] getFluidIDToCopy(){return new int[]{tank.getTankType().getID()};}
    @Override public FluidTank getTankToPaste(){return tank;}
    @Override public CompoundTag getSettings(Level level,BlockPos pos){var tag=new CompoundTag();tag.putIntArray("fluidID",new int[]{tank.getTankType().getID()});tag.putBoolean("isOn",isOn);tag.putBoolean("doesBurn",doesBurn);return tag;}
    @Override public void pasteSettings(CompoundTag tag,int index,Level level,Player player,BlockPos pos){int[] types=tag.getIntArray("fluidID");if(index<0||index>=types.length)return;tank.setTankType(HbmFluids.fromID(types[index]));if(tag.contains("isOn"))isOn=tag.getBoolean("isOn");if(tag.contains("doesBurn"))doesBurn=tag.getBoolean("doesBurn");setChanged();}
    @Override public String[] getFunctionInfo(){return new String[]{PREFIX_VALUE+"flowstate",PREFIX_VALUE+"ignitionstate",PREFIX_VALUE+"output",PREFIX_VALUE+"power",PREFIX_VALUE+"fluid",PREFIX_FUNCTION+"setflowstate"+NAME_SEPARATOR+"state",PREFIX_FUNCTION+"setignitionstate"+NAME_SEPARATOR+"state"};}
    @Override public String provideRORValue(String name){return switch(name){case "VAL:flowstate"->isOn?"1":"0";case "VAL:ignitionstate"->doesBurn?"1":"0";case "VAL:output"->output.toString();case "VAL:power"->power.toString();case "VAL:fluid"->Integer.toString(tank.getFill());default->null;};}
    @Override public String runRORFunction(String name,String[] params){if(params.length==1){if((PREFIX_FUNCTION+"setflowstate").equals(name)){isOn=params[0].equals("1");setChanged();}if((PREFIX_FUNCTION+"setignitionstate").equals(name)){doesBurn=params[0].equals("1");setChanged();}}return null;}
    private void disconnect(){if(level instanceof ServerLevel server){powerPorts.values().forEach(node->HbmEnergy.nodespace(server).destroyNode(node));if(fluidPorts!=null)fluidPorts.remove(server);}powerPorts.clear();fluidPorts=null;}
    @Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public void setRemoved(){disconnect();super.setRemoved();}
}
