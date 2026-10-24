package com.hbm.machine.turret.howard;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.energy.*;
import com.hbm.machine.turret.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.machine.turret.rotary.RotaryTurretEntity;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.weapon.sedna.gun.legendary.CasingEffects;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
/** Actual Howard/Methusalem CIWS hitscan probability,100-burst magazine,radar designation and original two-barrel source cosmetic cadence. */
public final class HowardTurretEntity extends RadarCommandTurretBase {
    public final HowardTurretKind kind;public int loaded;private int timer;public float spin,lastSpin;private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();private Direction nodeFacing;
    public HowardTurretEntity(HowardTurretKind kind,BlockPos pos,BlockState state){super(HowardTurretContent.entity(kind),pos,state);this.kind=kind;}
    @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}
    @Override protected String title(){return kind.title;}@Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return kind.damaged?null:new HowardTurretMenu(kind,id,inventory,this,worldPosition);}
    @Override protected List<BulletConfig> ammoConfigs(){return List.of(HowardBallistics.DGK_NORMAL);}
    @Override protected float baseDamage(){return HowardTurretSettings.damage(kind.damaged);}@Override public long getMaxPower(){return 50000;}@Override public long consumption(){return 500;}@Override public double heightOffset(){return 2.25;}@Override public double yawSpeed(){return kind.damaged?3:12;}@Override public double pitchSpeed(){return kind.damaged?2:8;}@Override public double detectorRange(){return kind.damaged?16:250;}@Override public double detectorGrace(){return kind.damaged?5:3;}@Override public double elevation(){return 90;}@Override public double depression(){return 50;}@Override public double barrelLength(){return 3.25;}@Override public boolean thermalVision(){return !kind.damaged;}@Override public boolean active(){return kind.damaged||super.active();}@Override public boolean hasPower(){return kind.damaged||super.hasPower();}
    @Override public boolean acceptableTarget(net.minecraft.world.entity.Entity entity){if(!kind.damaged)return super.acceptableTarget(entity);if(entity instanceof Player p&&p.getAbilities().instabuild)return false;return entity instanceof net.minecraft.world.entity.LivingEntity&&!entity.isSpectator();}
    @Override public Vec3 horizontalOffset(){return switch(getBlockState().getValue(DummyableBlock.LEGACY_META)-10){case 2->new Vec3(1,0,1);case 4->new Vec3(1,0,0);case 5->new Vec3(0,0,1);default->Vec3.ZERO;};}
    public RotaryTurretEntity.Port[] ports(){return RotaryTurretEntity.ports(worldPosition,DummyableBlock.facing(getBlockState()));}
    @Override protected void connect(ServerLevel server){var facing=DummyableBlock.facing(getBlockState());if(facing!=nodeFacing){disconnectPorts();nodeFacing=facing;}var space=HbmEnergy.nodespace(server);for(var port:ports()){if(getBlockState().getBlock() instanceof HowardTurretBlock block)block.setPort(server,port.position(),true);var node=nodes.get(port.position());if(node==null||node.isExpired()){node=new PowerNode(port.position()).setConnections(new NodeConnection(port.position().relative(port.first()),port.first()),new NodeConnection(port.position().relative(port.second()),port.second()));space.createNode(node);nodes.put(port.position(),node);}if(node.hasValidNet())node.addReceiver(this);}}
    public boolean portAt(BlockPos position,Direction side){if(side==null)return false;for(var port:ports())if(port.position().equals(position))return port.first()==side||port.second()==side;return false;}
    @Override public boolean canConnect(Direction side){return portAt(worldPosition,side);}
    @Override public void tickServer(ServerLevel server){if(loaded<=0){var config=firstLoaded();if(config!=null){consumeAmmo(config);level.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),HbmSounds.get("turret.howard_reload"),SoundSource.BLOCKS,4,1);loaded=100;}}super.tickServer(server);}
    @Override public void tickClient(){lastSpin=spin;if(tPos!=null)spin+=45;if(spin>=360){spin-=360;lastSpin-=360;}super.tickClient();}
    @Override protected void updateFiringTick(){timer++;if(kind.damaged){if(tPos!=null&&timer%4==0){level.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),HbmSounds.get("turret.howard_fire"),SoundSource.BLOCKS,4,.7F+level.random.nextFloat()*.3F);casing();if(level.random.nextInt(100)+1<=HowardTurretSettings.CIWS_ACCURACY.get()*.5)damageTarget();smoke(false);}}else if(loaded>0&&tPos!=null){level.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),HbmSounds.get("turret.howard_fire"),SoundSource.BLOCKS,4,.9F+level.random.nextFloat()*.3F);level.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),HbmSounds.get("turret.howard_fire"),SoundSource.BLOCKS,4,1+level.random.nextFloat()*.3F);casing();casing();if(timer%2==0){loaded--;if(level.random.nextInt(100)+1<=HowardTurretSettings.CIWS_ACCURACY.get())damageTarget();smoke(true);}}}
    private void damageTarget(){com.hbm.damage.LegacyEntityDamage.attackEntityFromIgnoreIFrame(target,com.hbm.damage.HbmDamageSources.source(level,com.hbm.damage.HbmDamageSources.shrapnel),baseDamage()+level.random.nextInt(2));}
    private void casing(){cachedCasing=HowardBallistics.CASING_DGK;spawnCasing();}
    private void smoke(boolean both){if(!(level instanceof ServerLevel server))return;var pos=turretPosition();var vec=muzzleOffset();var off=LegacyVector.createVectorHelper(0,.25,0);off.rotateAroundZ((float)-rotationPitch);off.rotateAroundY((float)-(rotationYaw+Math.PI*.5));for(int i=0;i<(both?2:1);i++){if(i==1){off.xCoord*= -1;off.yCoord*= -1;off.zCoord*= -1;}var tag=new CompoundTag();tag.putString("type","smoke");tag.putFloat("size",1.5F);tag.putByte("count",(byte)1);TurretEffects.send(server,worldPosition,pos.x+vec.xCoord+off.xCoord,pos.y+vec.yCoord+off.yCoord,pos.z+vec.zCoord+off.zCoord,tag);}}
    @Override protected Vec3 casingPosition(){var pos=turretPosition();var vec=LegacyVector.createVectorHelper(-.875,.2,-.125);vec.rotateAroundZ((float)-rotationPitch);vec.rotateAroundY((float)-(rotationYaw+Math.PI*.5));return pos.add(vec.xCoord,vec.yCoord,vec.zCoord);}
    @Override protected void spawnCasing(){if(cachedCasing==null||!(level instanceof ServerLevel server))return;var pos=casingPosition();var tag=new CompoundTag();tag.putString("type","casing");tag.putString("name",cachedCasing.getName());tag.putFloat("pitch",(float)-rotationPitch);tag.putFloat("yaw",(float)rotationYaw);tag.putDouble("motion_x",.4);tag.putDouble("motion_y",0);tag.putDouble("motion_z",0);tag.putFloat("variance",.03F);TurretEffects.send(server,worldPosition,pos.x,pos.y,pos.z,tag);cachedCasing=null;}
    @Override protected void saveMachine(CompoundTag tag){super.saveMachine(tag);tag.putInt("loaded",loaded);}@Override protected void loadMachine(CompoundTag tag){super.loadMachine(tag);loaded=tag.getInt("loaded");}
    private void disconnectPorts(){if(level instanceof ServerLevel server)for(var node:nodes.values())if(!node.isExpired())HbmEnergy.nodespace(server).destroyNode(node);nodes.clear();}
    @Override public void setRemoved(){disconnectPorts();super.setRemoved();}@Override public void onChunkUnloaded(){disconnectPorts();super.onChunkUnloaded();}
}
