package com.hbm.machine.fel;

import com.hbm.LegacyStartup;import com.hbm.block.digamma.DigammaBlocks;import com.hbm.block.fluid.LegacyMaterials;import com.hbm.energy.*;import com.hbm.exposure.HbmExposure;import com.hbm.item.battery.*;import com.hbm.machine.foundry.crucible.ThermalMachine;import com.hbm.machine.silex.*;import com.hbm.multiblock.DummyableBlock;
import java.math.BigInteger;import java.util.Set;import java.util.function.Consumer;
import net.minecraft.core.*;import net.minecraft.nbt.CompoundTag;import net.minecraft.server.level.ServerLevel;import net.minecraft.sounds.*;import net.minecraft.world.effect.*;import net.minecraft.world.entity.LivingEntity;import net.minecraft.world.entity.item.ItemEntity;import net.minecraft.world.entity.player.*;import net.minecraft.world.inventory.AbstractContainerMenu;import net.minecraft.world.item.ItemStack;import net.minecraft.world.level.Level;import net.minecraft.world.level.block.*;import net.minecraft.world.level.block.state.BlockState;import net.minecraft.world.phys.AABB;

/** Literal source tick ordering: prior-distance injury,3^wavelength consumption, then tracing and SILEX spacing. */
public final class FelBlockEntity extends ThermalMachine implements EnergyReceiver,EnergyConductor {
 public static final long MAX_POWER=20_000_000;public static final int BASE_POWER=1250;
 public static Consumer<FelBlockEntity> CLIENT_TICK=ignored->{},CLIENT_REMOVED=ignored->{};
 public LaserWavelength mode=LaserWavelength.NULL;public boolean isOn,missingValidSilex=true;public int distance;public boolean tilted;
 private BigInteger power=BigInteger.ZERO;private PowerNode powerNode;
 // Official1.7 Material.awt.K flags (setTranslucent); TNT remains an explicit source exception.
 private static final Set<String> TRANSLUCENT=Set.of("leaves","glass","tnt","ice","snow","cactus");
 public FelBlockEntity(BlockPos pos,BlockState state){super(FelContent.ENTITY.get(),pos,state,2);}
 public static NodeConnection portConnection(BlockPos core,Direction direction){return new NodeConnection(core.relative(direction,-5).above(),direction.getOpposite());}
 @Override protected String title(){return "container.machineFEL";}@Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new FelMenu(id,inventory,this,worldPosition);}
 public static void tick(Level level,BlockPos pos,BlockState state,FelBlockEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);else CLIENT_TICK.accept(machine);}
 private void serverTick(ServerLevel server){
  var direction=DummyableBlock.facing(getBlockState());var connection=portConnection(worldPosition,direction);
  if(powerNode==null||powerNode.isExpired()){powerNode=new PowerNode(connection.origin()).setConnections(connection);HbmEnergy.nodespace(server).createNode(powerNode);}if(powerNode.hasValidNet())powerNode.addReceiver(this);
  var batteryStack=slots.getFirst();if(batteryStack.getItem() instanceof CreativeBatteryItem)power=BigInteger.valueOf(MAX_POWER);else if(batteryStack.getItem() instanceof HbmBattery battery){long received=Math.min(Math.min(MAX_POWER-getPower(),battery.getDischargeRate(batteryStack)),battery.getCharge(batteryStack));battery.dischargeBattery(batteryStack,received);power=power.add(BigInteger.valueOf(received));}
  mode=isOn&&slots.get(1).getItem() instanceof LaserCrystalItem crystal?crystal.wavelength:LaserWavelength.NULL;
  int required=mode==LaserWavelength.NULL?0:(int)(BASE_POWER*Math.pow(3,mode.ordinal()));
  if(isOn&&mode!=LaserWavelength.NULL&&getPower()<required)power=BigInteger.ZERO;
  if(isOn&&getPower()>=required&&mode!=LaserWavelength.NULL){
   int previousDistance=distance-1;var end=worldPosition.relative(direction,previousDistance);
   var damageBox=new AABB(Math.min(worldPosition.getX(),end.getX())+.2,worldPosition.getY()+.2,Math.min(worldPosition.getZ(),end.getZ())+.2,Math.max(worldPosition.getX(),end.getX())+.8,worldPosition.getY()+1.8,Math.max(worldPosition.getZ(),end.getZ())+.8);
   for(var entity:server.getEntitiesOfClass(LivingEntity.class,damageBox))switch(mode){
    case VISIBLE:entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,60*60*65536,0));
    case IR:case UV:entity.igniteForSeconds(10);break;
    case GAMMA:HbmExposure.contaminate(entity,HbmExposure.Contamination.CREATIVE,25);break;
    case DRX:HbmExposure.applyDigammaData(entity,.1F);break;default:break;
   }
   power=power.subtract(BigInteger.valueOf(required));boolean spacing=false;
   for(int i=3;i<24;i++){
    var target=worldPosition.relative(direction,i).above();var state=server.getBlockState(target);var material=LegacyMaterials.of(state);boolean silex=state.getBlock() instanceof SilexBlock;boolean opaque=silex||material.blocksMovement()&&!TRANSLUCENT.contains(material.name());
    if(!opaque&&!state.is(Blocks.TNT)){distance=24;spacing=false;continue;}
    if(silex){
     var core=target.relative(direction).below();
     if(server.getBlockEntity(core) instanceof SilexBlockEntity machine){var facing=DummyableBlock.facing(machine.getBlockState());if((facing==direction||facing==direction.getOpposite())&&i>=5&&!spacing){if(machine.mode!=mode){machine.illuminate(mode);missingValidSilex=false;spacing=true;continue;}}
      else{SilexBlock.sourceBreakFromBeam(server,core);server.addFreshEntity(new ItemEntity(server,target.getX()+.5,target.getY()+.5,target.getZ()+.5,new ItemStack(SilexContent.ITEM.get())));}
     }
    }else{
     distance=i;if(material.isLiquid()){fizz(server,target);server.removeBlock(target,false);break;}
     if(state.getBlock().getExplosionResistance()<75&&server.random.nextInt(5)==0){fizz(server,target);var replacement=mode!=LaserWavelength.DRX?Blocks.FIRE:LegacyStartup.polaroidId==11?DigammaBlocks.MATTER.get():DigammaBlocks.FLAME.get();server.setBlock(target,replacement.defaultBlockState(),3);if(mode==LaserWavelength.DRX)server.setBlock(target.below(),DigammaBlocks.ASH.get().defaultBlockState(),3);}break;
    }
   }
  }
  sync(250);
 }
 private static void fizz(ServerLevel level,BlockPos pos){level.playSound(null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,SoundEvents.LAVA_EXTINGUISH,SoundSource.BLOCKS,1,1);}
 /** Source visuals deliberately use2^ordinal, unlike server consumption3^ordinal. */
 public boolean visuallyPowered(){return getPower()>BASE_POWER*Math.pow(2,mode.ordinal())&&isOn&&mode!=LaserWavelength.NULL;}
 public void toggle(){isOn=!isOn;setChanged();}
 @Override public long getPower(){return power.longValue();}@Override public long getMaxPower(){return MAX_POWER;}@Override public void setPower(long value){power=BigInteger.valueOf(value);setChanged();}@Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return BigInteger.valueOf(MAX_POWER);}@Override public void setPowerExact(BigInteger value){power=value;setChanged();}
 @Override public PowerNode energyNode(){return powerNode;}@Override public boolean canConnect(Direction side){return side!=null;}@Override public boolean isLoaded(){return level!=null&&!isRemoved()&&level.hasChunkAt(worldPosition);}@Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return MAX_POWER;}
 @Override public int[] getSlotsForFace(Direction side){return new int[0];}@Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}
 @Override protected void saveMachine(CompoundTag tag){tag.putString("power",power.toString());tag.putString("mode",mode.name());tag.putBoolean("isOn",isOn);tag.putBoolean("valid",missingValidSilex);tag.putInt("distance",distance);tag.putBoolean("tilted",tilted);}
 @Override protected void loadMachine(CompoundTag tag){power=tag.contains("power",8)?new BigInteger(tag.getString("power")):BigInteger.valueOf(tag.getLong("power"));try{mode=LaserWavelength.valueOf(tag.getString("mode"));}catch(IllegalArgumentException malformed){mode=LaserWavelength.NULL;}isOn=tag.getBoolean("isOn");missingValidSilex=tag.getBoolean("valid");distance=tag.getInt("distance");tilted=tag.getBoolean("tilted");}
 private void disconnect(){if(level instanceof ServerLevel server&&powerNode!=null)HbmEnergy.nodespace(server).destroyNode(powerNode);powerNode=null;if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
}
