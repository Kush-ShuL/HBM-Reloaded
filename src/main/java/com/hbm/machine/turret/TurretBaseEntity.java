package com.hbm.machine.turret;

import api.hbm.entity.IRadarDetectableNT;
import api.hbm.redstoneoverradio.IRORInteractive;
import com.hbm.api.computer.*;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.energy.*;
import com.hbm.entity.missile.EntityMissileBaseNT;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.item.battery.*;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.satellite.cargo.TurretRadarObserver;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.util.FakePlayer;

/** Actual TileEntityTurretBaseNT acquisition, angle/LOS, ammunition, native HE, inventory and computer/ROR paths. */
public abstract class TurretBaseEntity extends ThermalMachine implements EnergyReceiver,SidedEnergyConnector,ComputerControl,IRORInteractive,TurretRadarObserver {
    public double rotationYaw,rotationPitch,lastRotationYaw,lastRotationPitch,syncRotationYaw,syncRotationPitch;
    public boolean isOn,aligned,targetPlayers,targetAnimals,targetMobs=true,targetMachines=true;
    public int searchTimer,stattrak,casingDelay;public Entity target;public Vec3 tPos;protected long power;protected SpentCasing cachedCasing;
    private PowerNode powerNode;
    protected TurretBaseEntity(BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state,11);}
    // BlockEntity validates during its own constructor, before subclass fields are initialized.
    @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state);}
    protected abstract List<BulletConfig> ammoConfigs();protected abstract void updateFiringTick();protected abstract float baseDamage();
    public double yawSpeed(){return 4.5;}public double pitchSpeed(){return 3;}public double depression(){return 30;}public double elevation(){return 30;}public int detectorInterval(){return 10;}public double detectorRange(){return 32;}public double detectorGrace(){return 3;}public double acceptableInaccuracy(){return 5;}public double heightOffset(){return 1.5;}public double barrelLength(){return 1;}public boolean thermalVision(){return true;}public Vec3 horizontalOffset(){return new Vec3(0,0,0);}public long consumption(){return 100;}
    public boolean hasPower(){return getPower()>=consumption();}public boolean active(){return isOn;}
    public Vec3 turretPosition(){var offset=horizontalOffset();return new Vec3(worldPosition.getX()+offset.x,worldPosition.getY()+heightOffset(),worldPosition.getZ()+offset.z);}
    public Vec3 entityPosition(Entity entity){return new Vec3(entity.getX(),entity.getY()+entity.getBbHeight()*.5,entity.getZ());}
    private static double legacyLength(double x,double y,double z){return (double)(float)Math.sqrt(x*x+y*y+z*z);}
    protected LegacyVector muzzleOffset(){var vector=LegacyVector.createVectorHelper(barrelLength(),0,0);vector.rotateAroundZ((float)-rotationPitch);vector.rotateAroundY((float)-(rotationYaw+Math.PI*.5));return vector;}
    protected void connect(ServerLevel server){if(powerNode==null||powerNode.isExpired()){powerNode=new PowerNode(worldPosition).setConnections(new NodeConnection(worldPosition.below(),Direction.DOWN));HbmEnergy.nodespace(server).createNode(powerNode);}if(powerNode.hasValidNet())powerNode.addReceiver(this);}
    public void tickServer(ServerLevel server){
        connect(server);aligned=false;
        if(target!=null&&!target.isAlive()){target=null;stattrak++;}
        if(target!=null&&!entityInLOS(target))target=null;tPos=target==null?null:entityPosition(target);
        if(active()&&hasPower()){if(tPos!=null)turnTowards(tPos);}else{target=null;tPos=null;}
        if(target!=null&&!target.isAlive()){target=null;tPos=null;stattrak++;}
        if(active()&&hasPower()){searchTimer--;setPower(getPower()-consumption());if(searchTimer<=0){searchTimer=detectorInterval();if(target==null)seekNewTarget();}}else searchTimer=0;
        if(aligned)updateFiringTick();chargeBattery();sync(250);afterSync();
    }
    protected void afterSync(){}
    public void tickClient(){lastRotationPitch=rotationPitch;lastRotationYaw=rotationYaw;rotationPitch=syncRotationPitch;rotationYaw=syncRotationYaw;if(Math.abs(lastRotationYaw-rotationYaw)>Math.PI){if(lastRotationYaw<rotationYaw)lastRotationYaw+=Math.PI*2;else lastRotationYaw-=Math.PI*2;}}
    protected void chargeBattery(){if(power<0){power=0;return;}if(power>getMaxPower()){power=getMaxPower();return;}var stack=slots.get(10);if(stack.getItem() instanceof CreativeBatteryItem){power=getMaxPower();return;}if(stack.getItem() instanceof HbmBattery battery){long amount=Math.min(Math.min(getMaxPower()-power,battery.getDischargeRate(stack)),battery.getCharge(stack));battery.dischargeBattery(stack,amount);power+=amount;}}
    protected void seekNewTarget(){var origin=turretPosition();double range=detectorRange(),closest=range;Entity next=null;for(var entity:level.getEntities((Entity)null,new AABB(origin,origin).inflate(range))){var pos=entityPosition(entity);double distance=legacyLength(pos.x-origin.x,pos.y-origin.y,pos.z-origin.z);if(distance>range||!acceptableTarget(entity)||!entityInLOS(entity))continue;if(distance<closest){closest=distance;next=entity;}}target=next;if(target!=null)tPos=entityPosition(target);}
    public void turnTowards(Vec3 position){var delta=position.subtract(turretPosition());turnTowardsAngle(Math.asin(delta.y/legacyLength(delta.x,delta.y,delta.z)),-Math.atan2(delta.x,delta.z));}
    public void turnTowardsAngle(double targetPitch,double targetYaw){double yawStep=Math.toRadians(yawSpeed()),pitchStep=Math.toRadians(pitchSpeed()),pi2=Math.PI*2;if(Math.abs(rotationPitch-targetPitch)<pitchStep||Math.abs(rotationPitch-targetPitch)>pi2-pitchStep)rotationPitch=targetPitch;else rotationPitch+=targetPitch>rotationPitch?pitchStep:-pitchStep;double deltaYaw=(targetYaw-rotationYaw)%pi2;int dir=deltaYaw< -Math.PI?1:deltaYaw<0?-1:deltaYaw>Math.PI?-1:deltaYaw>0?1:0;if(Math.abs(rotationYaw-targetYaw)<yawStep||Math.abs(rotationYaw-targetYaw)>pi2-yawStep)rotationYaw=targetYaw;else rotationYaw+=yawStep*dir;double deltaPitch=targetPitch-rotationPitch;deltaYaw=targetYaw-rotationYaw;double error=Math.sqrt(deltaYaw*deltaYaw+deltaPitch*deltaPitch);rotationYaw%=pi2;rotationPitch%=pi2;if(error<=Math.toRadians(acceptableInaccuracy()))aligned=true;}
    public boolean entityInLOS(Entity entity){if(!entity.isAlive()||entity.isRemoved())return false;if(!thermalVision()&&entity instanceof LivingEntity living&&living.hasEffect(MobEffects.INVISIBILITY))return false;var pos=turretPosition();var point=entityPosition(entity);var delta=point.subtract(pos);double length=legacyLength(delta.x,delta.y,delta.z);if(length<detectorGrace()||length>detectorRange()*1.1)return false;var unit=LegacyVector.createVectorHelper(delta.x,delta.y,delta.z).normalize();double pitch=Math.toDegrees(Math.asin(unit.yCoord/unit.lengthVector()));if(pitch< -depression()||pitch>elevation())return false;return level.clip(new ClipContext(point,pos,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,net.minecraft.world.phys.shapes.CollisionContext.empty())).getType()==HitResult.Type.MISS;}
    public boolean acceptableTarget(Entity entity){if(!entity.isAlive()||entity.isRemoved()||entity.isSpectator())return false;if(TurretTargeting.contains(TurretTargeting.blacklist,entity))return false;for(var entry:TurretTargeting.conditions.entrySet())if(entry.getKey().isInstance(entity)&&entry.getValue()!=null){int result=entry.getValue().apply(entity,this);if(result==-1)return false;if(result==1)return true;}var whitelist=whitelist();if(whitelist!=null){if(entity instanceof Player player&&whitelist.contains(player.getDisplayName().getString()))return false;if(entity instanceof Mob&&whitelist.contains(entity.hasCustomName()?entity.getCustomName().getString():""))return false;}
        if(targetAnimals&&(entity instanceof Animal||entity instanceof WaterAnimal||entity instanceof AmbientCreature||entity instanceof AbstractGolem||entity instanceof Npc||TurretTargeting.contains(TurretTargeting.friendly,entity)))return true;
        if(targetMobs){if(entity instanceof EnderDragon)return false;if(entity instanceof EnderDragonPart||entity instanceof Enemy||TurretTargeting.contains(TurretTargeting.hostile,entity))return true;}
        if(targetMachines){if(entity instanceof IRadarDetectableNT radar&&!radar.canBeSeenBy(this))return false;if(entity instanceof EntityMissileBaseNT)return entity.getDeltaMovement().y<0;if(entity instanceof AbstractMinecart||entity instanceof com.hbm.entity.aircraft.bomber.EntityBomber||TurretTargeting.contains(TurretTargeting.machines,entity))return true;}
        if(targetPlayers){if(entity instanceof FakePlayer)return false;if(entity instanceof Player||TurretTargeting.contains(TurretTargeting.players,entity))return true;}return false;
    }
    public List<ItemStack> displayedAmmo(){return ammoConfigs().stream().filter(config->config!=null&&config.ammo!=null).map(config->config.ammo.toStack()).toList();}
    public BulletConfig firstLoaded(){for(int slot=1;slot<10;slot++){var stack=slots.get(slot);if(stack.isEmpty())continue;for(var config:ammoConfigs())if(config!=null&&config.ammo!=null&&config.ammo.matchesRecipe(stack,true))return config;}return null;}
    protected void consumeAmmo(BulletConfig config){for(int slot=1;slot<10;slot++)if(config.ammo.matchesRecipe(slots.get(slot),true)){removeItem(slot,1);return;}setChanged();}
    protected void spawnBullet(BulletConfig config){var origin=turretPosition();var offset=muzzleOffset();var projectile=new EntityBulletBaseMK4(level,config,baseDamage(),config.spread,(float)rotationYaw,(float)rotationPitch);projectile.moveTo(origin.x+offset.xCoord,origin.y+offset.yCoord,origin.z+offset.zCoord,projectile.getYRot(),projectile.getXRot());level.addFreshEntity(projectile);spawnCasing();}
    protected Vec3 casingPosition(){return turretPosition();}
    protected void spawnCasing(){if(cachedCasing==null||!(level instanceof ServerLevel server))return;var pos=casingPosition();var tag=new CompoundTag();tag.putString("type","casing");tag.putString("name",cachedCasing.getName());tag.putFloat("pitch",(float)-rotationPitch);tag.putFloat("yaw",(float)rotationYaw);TurretEffects.send(server,worldPosition,pos.x,pos.y,pos.z,tag);cachedCasing=null;}
    public List<String> whitelist(){return slots.getFirst().getItem() instanceof TurretChipItem?TurretChipItem.names(slots.getFirst()):null;}
    public void addName(String name){if(slots.getFirst().getItem() instanceof TurretChipItem){TurretChipItem.add(slots.getFirst(),name);setChanged();}}
    public void removeName(int index){if(slots.getFirst().getItem() instanceof TurretChipItem){TurretChipItem.remove(slots.getFirst(),index);setChanged();}}
    public void control(int button){switch(button){case 0->isOn=!isOn;case 1->targetPlayers=!targetPlayers;case 2->targetAnimals=!targetAnimals;case 3->targetMobs=!targetMobs;case 4->targetMachines=!targetMachines;default->{return;}}setChanged();}
    @Override public int[] getSlotsForFace(Direction side){return new int[]{1,2,3,4,5,6,7,8,9};}@Override public boolean canPlaceItem(int slot,ItemStack stack){return true;}
    @Override public long getPower(){return power;}@Override public void setPower(long value){power=value;setChanged();}@Override public long energyStored(){return power;}@Override public long energyCapacity(){return getMaxPower();}@Override public boolean isLoaded(){return level!=null&&!isRemoved()&&level.hasChunkAt(worldPosition);}@Override public boolean canConnect(Direction side){return side==Direction.DOWN;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new TurretMenu(id,inventory,this,worldPosition);}
    @Override public void startOpen(Player player){if(level!=null&&!level.isClientSide)level.playSound(null,worldPosition,HbmSounds.get("block.openC"),SoundSource.BLOCKS,1,1);}@Override public void stopOpen(Player player){if(level!=null&&!level.isClientSide)level.playSound(null,worldPosition,HbmSounds.get("block.closeC"),SoundSource.BLOCKS,1,1);}
    @Override protected void saveMachine(CompoundTag tag){tag.putLong("power",power);tag.putBoolean("isOn",isOn);tag.putBoolean("targetPlayers",targetPlayers);tag.putBoolean("targetAnimals",targetAnimals);tag.putBoolean("targetMobs",targetMobs);tag.putBoolean("targetMachines",targetMachines);tag.putInt("stattrak",stattrak);}
    @Override protected void loadMachine(CompoundTag tag){power=tag.getLong("power");isOn=tag.getBoolean("isOn");targetPlayers=tag.getBoolean("targetPlayers");targetAnimals=tag.getBoolean("targetAnimals");targetMobs=tag.getBoolean("targetMobs");targetMachines=tag.getBoolean("targetMachines");stattrak=tag.getInt("stattrak");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putDouble("hbm_pitch",rotationPitch);tag.putDouble("hbm_yaw",rotationYaw);if(tPos!=null){tag.putDouble("hbm_target_x",tPos.x);tag.putDouble("hbm_target_y",tPos.y);tag.putDouble("hbm_target_z",tPos.z);}return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);syncRotationPitch=tag.getDouble("hbm_pitch");syncRotationYaw=tag.getDouble("hbm_yaw");tPos=tag.contains("hbm_target_x")?new Vec3(tag.getDouble("hbm_target_x"),tag.getDouble("hbm_target_y"),tag.getDouble("hbm_target_z")):null;}
    private void disconnect(){if(powerNode!=null&&level instanceof ServerLevel server&&!powerNode.isExpired())HbmEnergy.nodespace(server).destroyNode(powerNode);powerNode=null;}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public String computerType(){return "ntm_turret";}@Override public boolean canComputerConnect(Direction side){return side==Direction.DOWN;}
    @Override public String[] computerMethods(){return new String[]{"setActive","isActive","getEnergyInfo","getWhitelisted","addWhitelist","removeWhitelist","setTargeting","getTargeting","hasTarget","getAngle","isAligned","getPos"};}
    @Override public Object[] callComputer(String method,ComputerArguments args){switch(method){case "setActive"-> {isOn=args.checkBoolean(0);setChanged();return new Object[]{};}case "isActive"->{return new Object[]{isOn};}case "getEnergyInfo"->{return new Object[]{getPower(),getMaxPower()};}case "getWhitelisted"->{var names=whitelist();return names==null?new Object[]{}:new Object[]{names.isEmpty()?null:names.toArray(String[]::new)};}case "addWhitelist"->{String name=args.checkString(0);var names=whitelist();if(names!=null&&names.contains(name))return new Object[]{false};addName(name);return new Object[]{true};}case "removeWhitelist"->{String name=args.checkString(0);var names=whitelist();if(names==null||!names.contains(name))return new Object[]{false};removeName(names.indexOf(name));return new Object[]{true};}case "setTargeting"->{targetPlayers=args.checkBoolean(0);targetAnimals=args.checkBoolean(1);targetMobs=args.checkBoolean(2);targetMachines=args.checkBoolean(3);setChanged();return new Object[]{};}case "getTargeting"->{return new Object[]{targetPlayers,targetAnimals,targetMobs,targetMachines};}case "hasTarget"->{return new Object[]{target!=null};}case "getAngle"->{return new Object[]{Math.toDegrees(rotationPitch),Math.toDegrees(rotationYaw)};}case "isAligned"->{return new Object[]{aligned};}case "getPos"->{return new Object[]{worldPosition.getX(),worldPosition.getY(),worldPosition.getZ()};}default->throw new ComputerException("Unknown method: "+method);}}
    @Override public String[] getFunctionInfo(){return new String[]{PREFIX_FUNCTION+"setactive"+NAME_SEPARATOR+"active (0 or 1)",PREFIX_FUNCTION+"targetplayers"+NAME_SEPARATOR+"enabled (0 or 1)",PREFIX_FUNCTION+"targetanimals"+NAME_SEPARATOR+"enabled (0 or 1)",PREFIX_FUNCTION+"targetmobs"+NAME_SEPARATOR+"enabled (0 or 1)",PREFIX_FUNCTION+"targetmachines"+NAME_SEPARATOR+"enabled (0 or 1)",PREFIX_FUNCTION+"addwhitelist"+NAME_SEPARATOR+"name",PREFIX_FUNCTION+"removewhitelist"+NAME_SEPARATOR+"name"};}
    @Override public String runRORFunction(String name,String[] args){if(args.length==0)return null;for(var entry:new String[]{"setactive","targetplayers","targetanimals","targetmobs","targetmachines"})if((PREFIX_FUNCTION+entry).equals(name)){try{boolean value=Integer.parseInt(args[0])==1;switch(entry){case "setactive"->isOn=value;case "targetplayers"->targetPlayers=value;case "targetanimals"->targetAnimals=value;case "targetmobs"->targetMobs=value;case "targetmachines"->targetMachines=value;}setChanged();}catch(NumberFormatException ignored){}return null;}var names=whitelist();if(names!=null){if((PREFIX_FUNCTION+"addwhitelist").equals(name)&&!names.contains(args[0]))addName(args[0]);if((PREFIX_FUNCTION+"removewhitelist").equals(name)&&names.contains(args[0]))removeName(names.indexOf(args[0]));}return null;}
}
