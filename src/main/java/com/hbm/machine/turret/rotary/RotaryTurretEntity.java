package com.hbm.machine.turret.rotary;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.energy.*;
import com.hbm.machine.turret.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.rifle.RifleBallistics;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
/** Source Chekhov/Friendly share the real turret engine; only their actual ammo/delay/damage/ejector differ. */
public final class RotaryTurretEntity extends TurretBaseEntity {
    public final RotaryTurretKind kind;public float spin,lastSpin;private float acceleration;private boolean manual;private int firingTimer;private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();private Direction nodeFacing;
    public record Port(BlockPos position,Direction first,Direction second){}
    public RotaryTurretEntity(RotaryTurretKind kind,BlockPos pos,BlockState state){super(RotaryTurretContent.entity(kind),pos,state);this.kind=kind;}
    @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}
    @Override protected String title(){return kind.title;}@Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new RotaryTurretMenu(kind,id,inventory,this,worldPosition);}
    @Override protected List<BulletConfig> ammoConfigs(){return kind==RotaryTurretKind.CHEKHOV?List.of(RifleBallistics.bmg50_sp,RifleBallistics.bmg50_fmj,RifleBallistics.bmg50_jhp,RifleBallistics.bmg50_ap,RifleBallistics.bmg50_du):List.of(RifleBallistics.r556_sp,RifleBallistics.r556_fmj,RifleBallistics.r556_jhp,RifleBallistics.r556_ap);}
    @Override protected float baseDamage(){return RotaryTurretSettings.damage(kind);}@Override public long getMaxPower(){return 10000;}@Override public double elevation(){return 45;}@Override public double barrelLength(){return 3.5;}@Override public double acceptableInaccuracy(){return 15;}
    @Override public Vec3 horizontalOffset(){return switch(getBlockState().getValue(DummyableBlock.LEGACY_META)-10){case 2->new Vec3(1,0,1);case 4->new Vec3(1,0,0);case 5->new Vec3(0,0,1);default->Vec3.ZERO;};}
    public Port[] ports(){return ports(worldPosition,DummyableBlock.facing(getBlockState()));}
    public static Port[] ports(BlockPos p,Direction d){var r=d.getClockWise();return new Port[]{new Port(p,d,r.getOpposite()),new Port(p.relative(r),d,r),new Port(p.relative(d,-1),d.getOpposite(),r.getOpposite()),new Port(p.relative(d,-1).relative(r),d.getOpposite(),r)};}
    @Override protected void connect(ServerLevel server){var facing=DummyableBlock.facing(getBlockState());if(facing!=nodeFacing){disconnectPorts();nodeFacing=facing;}var space=HbmEnergy.nodespace(server);for(var port:ports()){if(getBlockState().getBlock() instanceof RotaryTurretBlock block)block.setPort(server,port.position,true);var node=nodes.get(port.position);if(node==null||node.isExpired()){node=new PowerNode(port.position).setConnections(new NodeConnection(port.position.relative(port.first),port.first),new NodeConnection(port.position.relative(port.second),port.second));space.createNode(node);nodes.put(port.position,node);}if(node.hasValidNet())node.addReceiver(this);}}
    public boolean portAt(BlockPos position,Direction side){if(side==null)return false;for(var port:ports())if(port.position.equals(position))return port.first==side||port.second==side;return false;}
    @Override public boolean canConnect(Direction side){return portAt(worldPosition,side);}
    @Override public void tickServer(ServerLevel server){super.tickServer(server);if(tPos==null&&!manual){firingTimer--;if(firingTimer>20)firingTimer=20;if(firingTimer<0)firingTimer=0;}}
    @Override public void tickClient(){super.tickClient();if(tPos!=null||manual)acceleration=Math.min(45,acceleration+2);else acceleration=Math.max(0,acceleration-2);manual=false;lastSpin=spin;spin+=acceleration;if(spin>=360){spin-=360;lastSpin-=360;}}
    public void manualSetup(){manual=true;}
    @Override protected void updateFiringTick(){firingTimer++;if(firingTimer<=20||firingTimer%kind.delay!=0)return;var config=firstLoaded();if(config==null)return;cachedCasing=config.casing;spawnBullet(config);consumeAmmo(config);level.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),HbmSounds.get("turret.chekhov_fire"),SoundSource.BLOCKS,2,1);if(level instanceof ServerLevel server){var pos=turretPosition();var vector=muzzleOffset();var tag=new CompoundTag();tag.putString("type","smoke");tag.putFloat("size",1.5F);tag.putByte("count",(byte)1);TurretEffects.send(server,worldPosition,pos.x+vector.xCoord,pos.y+vector.yCoord,pos.z+vector.zCoord,tag);}}
    @Override protected Vec3 casingPosition(){var pos=turretPosition();var vector=LegacyVector.createVectorHelper(-1.125,.125,.25);vector.rotateAroundZ((float)-rotationPitch);vector.rotateAroundY((float)-(rotationYaw+Math.PI*.5));return pos.add(vector.xCoord,vector.yCoord,vector.zCoord);}
    @Override protected void spawnCasing(){if(cachedCasing==null||!(level instanceof ServerLevel server))return;var pos=casingPosition();var tag=new CompoundTag();tag.putString("type","casing");tag.putString("name",cachedCasing.getName());tag.putFloat("pitch",(float)-rotationPitch);tag.putFloat("yaw",(float)rotationYaw);tag.putDouble("motion_x",kind==RotaryTurretKind.CHEKHOV?-.8:-.3);tag.putDouble("motion_y",kind==RotaryTurretKind.CHEKHOV?.8:.6);tag.putDouble("motion_z",0);tag.putFloat("variance",kind==RotaryTurretKind.CHEKHOV?.1F:.05F);TurretEffects.send(server,worldPosition,pos.x,pos.y,pos.z,tag);cachedCasing=null;}
    private void disconnectPorts(){if(level instanceof ServerLevel server)for(var node:nodes.values())if(!node.isExpired())HbmEnergy.nodespace(server).destroyNode(node);nodes.clear();}
    @Override public void setRemoved(){disconnectPorts();super.setRemoved();}@Override public void onChunkUnloaded(){disconnectPorts();super.onChunkUnloaded();}
}
