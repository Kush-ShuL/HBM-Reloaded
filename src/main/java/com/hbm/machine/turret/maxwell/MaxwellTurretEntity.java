package com.hbm.machine.turret.maxwell;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.damage.*;
import com.hbm.energy.*;
import com.hbm.item.medicine.MedicineEffects;
import com.hbm.machine.turret.TurretBaseEntity;
import com.hbm.machine.turret.rotary.RotaryTurretEntity;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.*;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.death.DeathParticlePacket;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
/** Exact Maxwell child of the sole source turret engine: real no-projectile microwave attack, nine summed upgrade slots and private HE. */
public final class MaxwellTurretEntity extends TurretBaseEntity {
 public int beam;public double lastDist;
 private int redLevel,greenLevel,blueLevel,blackLevel,pinkLevel,checkDelay;
 private boolean fiveG,screm,shotPacket;private List<ItemStack> displayed;
 private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();private Direction nodeFacing;
 public MaxwellTurretEntity(BlockPos pos,BlockState state){super(MaxwellTurretContent.ENTITY.get(),pos,state);}
 @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}
 @Override protected String title(){return "container.turretMaxwell";}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new MaxwellTurretMenu(id,inventory,this,worldPosition);}
 @Override public void writeClientSideData(AbstractContainerMenu menu,RegistryFriendlyByteBuf buffer){buffer.writeBlockPos(worldPosition);}
 @Override protected List<BulletConfig> ammoConfigs(){return List.of();}
 @Override protected float baseDamage(){return MaxwellTurretSettings.damage();}
 @Override public long getMaxPower(){return 10000000;}
 @Override public long consumption(){return fiveG?10:10000-blueLevel*300;}
 @Override public double detectorRange(){return 64D+greenLevel*3;}
 @Override public double detectorGrace(){return 5;}
 @Override public double yawSpeed(){return 9;}
 @Override public double pitchSpeed(){return 6;}
 @Override public double elevation(){return 40;}
 @Override public double depression(){return 35;}
 @Override public double acceptableInaccuracy(){return 2;}
 @Override public double barrelLength(){return 2.125;}
 @Override public double heightOffset(){return 2;}
 @Override public Vec3 horizontalOffset(){return switch(getBlockState().getValue(DummyableBlock.LEGACY_META)-10){case 2->new Vec3(1,0,1);case 4->new Vec3(1,0,0);case 5->new Vec3(0,0,1);default->Vec3.ZERO;};}
 public RotaryTurretEntity.Port[] ports(){return RotaryTurretEntity.ports(worldPosition,DummyableBlock.facing(getBlockState()));}
 public boolean portAt(BlockPos position,Direction side){if(side==null)return false;for(var p:ports())if(p.position().equals(position))return p.first()==side||p.second()==side;return false;}
 @Override public boolean canConnect(Direction side){return portAt(worldPosition,side);}
 @Override protected void connect(ServerLevel server){
  var facing=DummyableBlock.facing(getBlockState());if(facing!=nodeFacing){disconnectPorts();nodeFacing=facing;}
  for(var p:ports()){
   if(getBlockState().getBlock() instanceof MaxwellTurretBlock block)block.setPort(server,p.position(),true);
   var node=nodes.get(p.position());if(node==null||node.isExpired()){node=new PowerNode(p.position()).setConnections(new NodeConnection(p.position().relative(p.first()),p.first()),new NodeConnection(p.position().relative(p.second()),p.second()));HbmEnergy.nodespace(server).createNode(node);nodes.put(p.position(),node);}if(node.hasValidNet())node.addReceiver(this);
  }
 }
 @Override public void tickServer(ServerLevel server){
  // Source scans before the entire base update, including range/active HE and aligned firing.
  if(checkDelay<=0){checkDelay=20;redLevel=greenLevel=blueLevel=blackLevel=pinkLevel=0;fiveG=screm=false;
   for(int slot=1;slot<10;slot++){var stack=slots.get(slot);if(stack.isEmpty())continue;var item=stack.getItem();
    for(int tier=1;tier<=3;tier++){
     if(item==HbmContent.item("upgrade_speed_"+tier))redLevel+=tier;
     if(item==HbmContent.item("upgrade_effect_"+tier))greenLevel+=tier;
     if(item==HbmContent.item("upgrade_power_"+tier))blueLevel+=tier;
     if(item==HbmContent.item("upgrade_afterburn_"+tier))pinkLevel+=tier;
     if(item==HbmContent.item("upgrade_overdrive_"+tier))blackLevel+=tier;
    }
    if(item==MaxwellTurretContent.UPGRADE_5G.get())fiveG=true;if(item==MaxwellTurretContent.SCREM.get())screm=true;
   }
  }checkDelay--;super.tickServer(server);
 }
 @Override public void tickClient(){
  if(tPos!=null){var p=turretPosition();lastDist=LegacyVector.createVectorHelper(tPos.x-p.x,tPos.y-p.y,tPos.z-p.z).lengthVector();}
  if(beam>0)beam--;super.tickClient();
 }
 @Override protected void updateFiringTick(){
  long demand=consumption()*10;
  if(target!=null&&getPower()>=demand){
   if(fiveG&&target instanceof Player living)living.addEffect(new MobEffectInstance(MedicineEffects.DEATH,30*60*20,0,true,true));
   else LegacyEntityDamage.attackEntityFromIgnoreIFrame(target,HbmDamageSources.source(level,HbmDamageSources.microwave),(blackLevel*10+redLevel+1F)*baseDamage());
   if(pinkLevel>0)target.igniteForSeconds(pinkLevel*3);
   if(!target.isAlive()&&target instanceof LivingEntity){
    // Original giblets packet lacks gibType/cDiv: exact defaults0/absent, with150m audience; no extra skeleton/ashes side effects.
    DeathParticlePacket.giblets(target,0,0);
    if(screm)level.playSound(null,target.getX(),target.getY(),target.getZ(),HbmSounds.get("block.screm"),SoundSource.BLOCKS,20,1);
    else level.playSound(null,target.getX(),target.getY(),target.getZ(),SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR,SoundSource.BLOCKS,2,.95F+level.random.nextFloat()*.2F);
   }
   power-=demand;shotPacket=true;try{sync(250);}finally{shotPacket=false;}
  }
 }
 @Override public List<ItemStack> displayedAmmo(){
  if(displayed==null){var result=new ArrayList<ItemStack>();for(String family:new String[]{"speed","effect","power","afterburn","overdrive"})for(int tier=1;tier<=3;tier++)result.add(new ItemStack(HbmContent.item("upgrade_"+family+"_"+tier)));result.add(new ItemStack(MaxwellTurretContent.UPGRADE_5G.get()));result.add(new ItemStack(MaxwellTurretContent.SCREM.get()));displayed=List.copyOf(result);}return displayed;
 }
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){if(!shotPacket)return super.getUpdateTag(lookup);var t=new CompoundTag();t.putBoolean("hbm_maxwell_shot",true);return t;}
 @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){if(tag.getBoolean("hbm_maxwell_shot")){beam=5;return;}super.handleUpdateTag(tag,lookup);}
 private void disconnectPorts(){if(level instanceof ServerLevel server)for(var node:nodes.values())if(!node.isExpired())HbmEnergy.nodespace(server).destroyNode(node);nodes.clear();}
 @Override public void setRemoved(){disconnectPorts();super.setRemoved();}
 @Override public void onChunkUnloaded(){disconnectPorts();super.onChunkUnloaded();}
}
