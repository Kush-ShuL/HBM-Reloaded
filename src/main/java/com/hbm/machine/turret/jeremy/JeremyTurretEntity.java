package com.hbm.machine.turret.jeremy;
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
/** Source TileEntityTurretJeremy: five actual240mm configs,40aligned-tick cadence,22tick smoky casing and20tick reload. */
public final class JeremyTurretEntity extends TurretBaseEntity {
    public final JeremyTurretKind kind;private int firingTimer,reload;private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();private Direction nodeFacing;
    public JeremyTurretEntity(JeremyTurretKind kind,BlockPos pos,BlockState state){super(JeremyTurretContent.entity(kind),pos,state);this.kind=kind;}
    @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}
    @Override protected String title(){return kind.title;}@Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new JeremyTurretMenu(kind,id,inventory,this,worldPosition);}
    @Override protected List<BulletConfig> ammoConfigs(){return JeremyBallistics.configs();}
    @Override protected float baseDamage(){return JeremyTurretSettings.damage();}@Override public long getMaxPower(){return 10000;}@Override public double detectorGrace(){return 16;}@Override public double depression(){return 45;}@Override public double barrelLength(){return 4.25;}@Override public double detectorRange(){return 80;}
    @Override public Vec3 horizontalOffset(){return switch(getBlockState().getValue(DummyableBlock.LEGACY_META)-10){case 2->new Vec3(1,0,1);case 4->new Vec3(1,0,0);case 5->new Vec3(0,0,1);default->Vec3.ZERO;};}
    public RotaryTurretEntity.Port[] ports(){return RotaryTurretEntity.ports(worldPosition,DummyableBlock.facing(getBlockState()));}
    @Override protected void connect(ServerLevel server){var facing=DummyableBlock.facing(getBlockState());if(facing!=nodeFacing){disconnectPorts();nodeFacing=facing;}var space=HbmEnergy.nodespace(server);for(var port:ports()){if(getBlockState().getBlock() instanceof JeremyTurretBlock block)block.setPort(server,port.position(),true);var node=nodes.get(port.position());if(node==null||node.isExpired()){node=new PowerNode(port.position()).setConnections(new NodeConnection(port.position().relative(port.first()),port.first()),new NodeConnection(port.position().relative(port.second()),port.second()));space.createNode(node);nodes.put(port.position(),node);}if(node.hasValidNet())node.addReceiver(this);}}
    public boolean portAt(BlockPos position,Direction side){if(side==null)return false;for(var port:ports())if(port.position().equals(position))return port.first()==side||port.second()==side;return false;}
    @Override public boolean canConnect(Direction side){return portAt(worldPosition,side);}
    private void reloadTick(){if(reload>0)reload--;if(reload==1)level.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),HbmSounds.get("turret.jeremy_reload"),SoundSource.BLOCKS,2,1);}
    @Override public void tickServer(ServerLevel server){reloadTick();super.tickServer(server);}
    @Override public void tickClient(){reloadTick();super.tickClient();}
    @Override protected void afterSync(){if(casingDelay>0)casingDelay--;else spawnCasing();}
    @Override protected void spawnBullet(BulletConfig config){var pos=turretPosition();var vec=muzzleOffset();var projectile=new EntityBulletBaseMK4(level,config,baseDamage(),config.spread,(float)rotationYaw,(float)rotationPitch);projectile.moveTo(pos.x+vec.xCoord,pos.y+vec.yCoord,pos.z+vec.zCoord,projectile.getYRot(),projectile.getXRot());level.addFreshEntity(projectile);casingDelay=22;}
    @Override protected void updateFiringTick(){firingTimer++;if(firingTimer%40!=0)return;var config=firstLoaded();if(config==null)return;cachedCasing=config.casing;spawnBullet(config);consumeAmmo(config);level.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),HbmSounds.get("turret.jeremy_fire"),SoundSource.BLOCKS,4,1);reload=20;if(level instanceof ServerLevel server){var pos=turretPosition();var vec=muzzleOffset();var tag=new CompoundTag();tag.putString("type","smoke");tag.putFloat("size",0);tag.putByte("count",(byte)5);TurretEffects.send(server,worldPosition,pos.x+vec.xCoord,pos.y+vec.yCoord,pos.z+vec.zCoord,tag);}}
    @Override protected Vec3 casingPosition(){var pos=turretPosition();var vec=LegacyVector.createVectorHelper(-2,0,0);vec.rotateAroundZ((float)-rotationPitch);vec.rotateAroundY((float)-(rotationYaw+Math.PI*.5));return pos.add(vec.xCoord,vec.yCoord,vec.zCoord);}
    @Override protected void spawnCasing(){if(cachedCasing==null)return;var pos=casingPosition();CasingEffects.composeEffect(level,pos.x,pos.y,pos.z,(float)Math.toDegrees(rotationYaw),(float)-Math.toDegrees(rotationPitch),-.2,-.2,0,.01,-5,0,cachedCasing.getName(),true,100,.5,20);cachedCasing=null;}
    private void disconnectPorts(){if(level instanceof ServerLevel server)for(var node:nodes.values())if(!node.isExpired())HbmEnergy.nodespace(server).destroyNode(node);nodes.clear();}
    @Override public void setRemoved(){disconnectPorts();super.setRemoved();}@Override public void onChunkUnloaded(){disconnectPorts();super.onChunkUnloaded();}
}
