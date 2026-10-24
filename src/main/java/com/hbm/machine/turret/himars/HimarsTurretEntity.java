package com.hbm.machine.turret.himars;
import api.hbm.redstoneoverradio.IRORInteractive;
import com.hbm.api.computer.*;
import com.hbm.machine.turret.greg.ArtilleryTurretBase;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.BulletConfig;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
/** TileEntityTurretHIMARS: actual two-mode target queue, 80-tick crane travel, pod loading, fixed45deg pitch and40aligned-tick shots. */
public final class HimarsTurretEntity extends ArtilleryTurretBase {
 public static final short MODE_AUTO=0,MODE_MANUAL=1;
 public short mode=0;public int typeLoaded=-1,ammo;public float crane,lastCrane;private int timer;
 public HimarsTurretEntity(BlockPos pos,BlockState state){super(HimarsTurretContent.ENTITY.get(),pos,state);}
 @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}
 @Override protected String title(){return "container.turretHIMARS";}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new HimarsTurretMenu(id,inventory,this,worldPosition);}
 @Override protected List<BulletConfig> ammoConfigs(){return List.of();}@Override public List<ItemStack> displayedAmmo(){return HimarsAmmoItem.creative();}
 @Override public long getMaxPower(){return 1000000;}@Override public double barrelLength(){return .5;}@Override public double acceptableInaccuracy(){return 5;}@Override public double heightOffset(){return 5;}@Override public double detectorRange(){return 5000;}@Override public double detectorGrace(){return 250;}@Override public double yawSpeed(){return 1;}@Override public double pitchSpeed(){return .5;}@Override protected boolean doLOSCheck(){return false;}
 @Override public Vec3 horizontalOffset(){return switch(getBlockState().getValue(DummyableBlock.LEGACY_META)-10){case 2->new Vec3(1,0,1);case 4->new Vec3(1,0,0);case 5->new Vec3(0,0,1);default->Vec3.ZERO;};}
 @Override public void turnTowards(Vec3 position){var delta=position.subtract(turretPosition());turnTowardsAngle(Math.PI/4,-Math.atan2(delta.x,delta.z));}
 public boolean hasAmmo(){return typeLoaded>=0&&ammo>0;}public int queuedTargets(){return targetQueue.size();}
 private int spareRocket(){for(int slot=1;slot<10;slot++)if(slots.get(slot).getItem() instanceof HimarsAmmoItem)return HimarsAmmoItem.type(slots.get(slot));return -1;}
 private void consumePod(int type){for(int slot=1;slot<10;slot++)if(slots.get(slot).getItem() instanceof HimarsAmmoItem&&HimarsAmmoItem.type(slots.get(slot))==type){removeItem(slot,1);return;}}
 @Override public void tickServer(ServerLevel server){
  connect(server);
  if(mode==MODE_MANUAL){if(!targetQueue.isEmpty())tPos=targetQueue.getFirst();}else targetQueue.clear();
  aligned=false;
  if(target!=null&&!target.isAlive()){target=null;stattrak++;}
  if(target!=null&&mode!=MODE_MANUAL&&!entityInLOS(target))target=null;
  if(target!=null)tPos=entityPosition(target);else if(mode!=MODE_MANUAL)tPos=null;
  if(active()&&hasPower()){
   if(!hasAmmo()||crane>0){turnTowardsAngle(0,rotationYaw);if(aligned){if(hasAmmo())crane-=.0125F;else{crane+=.0125F;if(crane>=1){int available=spareRocket();if(available!=-1){typeLoaded=available;ammo=HimarsRocketTypes.type(available).amount();consumePod(available);}}}}crane=Mth.clamp(crane,0,1);}
   else if(tPos!=null)turnTowards(tPos);
  }else{target=null;tPos=null;}
  if(!active())targetQueue.clear();
  if(target!=null&&!target.isAlive()){target=null;tPos=null;stattrak++;}
  if(active()&&hasPower()){searchTimer--;setPower(getPower()-consumption());if(searchTimer<=0){searchTimer=detectorInterval();if(target==null&&mode!=MODE_MANUAL)seekNewTarget();}}else searchTimer=0;
  if(aligned&&crane<=0)updateFiringTick();chargeBattery();sync(250);
 }
 @Override public void tickClient(){lastCrane=crane;super.tickClient();}
 @Override protected void updateFiringTick(){if(++timer%40!=0)return;if(hasAmmo()&&tPos!=null){spawnShell(typeLoaded);ammo--;level.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),HbmSounds.get("weapon.rocketFlame"),SoundSource.BLOCKS,25,1);}if(mode==MODE_MANUAL&&!targetQueue.isEmpty()){targetQueue.removeFirst();tPos=null;}}
 private void spawnShell(int type){var pos=turretPosition();var offset=muzzleOffset();var rocket=new HimarsRocketEntity(level);rocket.moveTo(pos.x+offset.xCoord,pos.y+offset.yCoord,pos.z+offset.zCoord,0,0);rocket.setThrowableHeading(offset.xCoord,offset.yCoord,offset.zCoord,25,0);if(target!=null)rocket.setTarget(target);else rocket.setTarget(tPos.x,tPos.y,tPos.z);rocket.setType(type);level.addFreshEntity(rocket);}
 @Override public void control(int button){if(button==5){if(++mode>1)mode=0;tPos=null;targetQueue.clear();setChanged();}else super.control(button);}
 @Override protected void saveMachine(CompoundTag tag){super.saveMachine(tag);tag.putShort("mode",mode);tag.putInt("type",typeLoaded);tag.putInt("ammo",ammo);}
 @Override protected void loadMachine(CompoundTag tag){super.loadMachine(tag);mode=tag.getShort("mode");typeLoaded=tag.getShort("type");ammo=tag.getInt("ammo");}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putFloat("hbm_crane",crane);return tag;}
 @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);crane=tag.getFloat("hbm_crane");}
 @Override public String[] computerMethods(){return new String[]{"setActive","isActive","getEnergyInfo","getWhitelisted","addWhitelist","removeWhitelist","setTargeting","getTargeting","hasTarget","getAngle","isAligned","getCurrentTarget","getTargetDistance","addCoords"};}
 @Override public Object[] callComputer(String method,ComputerArguments args){if(method.equals("addCoords")){mode=MODE_MANUAL;targetQueue.add(new Vec3(args.checkDouble(0),args.checkDouble(1),args.checkDouble(2)));setChanged();return new Object[]{};}return super.callComputer(method,args);}
 @Override public String[] getFunctionInfo(){return new String[]{PREFIX_FUNCTION+"setactive"+NAME_SEPARATOR+"active (0 or 1)",PREFIX_FUNCTION+"targetplayers"+NAME_SEPARATOR+"enabled (0 or 1)",PREFIX_FUNCTION+"targetanimals"+NAME_SEPARATOR+"enabled (0 or 1)",PREFIX_FUNCTION+"targetmobs"+NAME_SEPARATOR+"enabled (0 or 1)",PREFIX_FUNCTION+"targetmachines"+NAME_SEPARATOR+"enabled (0 or 1)",PREFIX_FUNCTION+"addwhitelist"+NAME_SEPARATOR+"name",PREFIX_FUNCTION+"removewhitelist"+NAME_SEPARATOR+"name",PREFIX_FUNCTION+"setmanual"+NAME_SEPARATOR+"mode (0 or 1)",PREFIX_FUNCTION+"enqueue"+NAME_SEPARATOR+"x"+PARAM_SEPARATOR+"y"+PARAM_SEPARATOR+"z"};}
 @Override public String runRORFunction(String name,String[] args){super.runRORFunction(name,args);if((PREFIX_FUNCTION+"setmanual").equals(name)&&args.length==1){mode=(short)IRORInteractive.parseInt(args[0],0,1);setChanged();}return null;}
}
