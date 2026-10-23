package com.hbm.machine.turret.tauon;

import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.damage.*;
import com.hbm.energy.*;
import com.hbm.item.tool.WeldingParticlePacket;
import com.hbm.machine.turret.TurretBaseEntity;
import com.hbm.machine.turret.rotary.RotaryTurretEntity;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.accelerator.AcceleratorFactory;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

/** Source Tauon: one real uranium ammo config, direct SUBATOMIC damage and exact five-aligned-tick firing. */
public final class TauonTurretEntity extends TurretBaseEntity {
    public int beam;
    public float spin,lastSpin;
    public double lastDist;
    private int firingTimer;
    private boolean shotPacket;
    private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();
    private Direction nodeFacing;
    public TauonTurretEntity(BlockPos pos,BlockState state){super(TauonTurretContent.ENTITY.get(),pos,state);}
    @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}
    @Override protected String title(){return "container.turretTauon";}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new TauonTurretMenu(id,inventory,this,worldPosition);}
    @Override public void writeClientSideData(AbstractContainerMenu menu,RegistryFriendlyByteBuf buffer){buffer.writeBlockPos(worldPosition);}
    @Override protected List<BulletConfig> ammoConfigs(){return List.of(AcceleratorFactory.tau_uranium);}
    @Override protected float baseDamage(){return TauonTurretSettings.damage();}
    @Override public long getMaxPower(){return 100000;}
    @Override public long consumption(){return 1000;}
    @Override public double detectorRange(){return 128;}
    @Override public double detectorGrace(){return 3;}
    @Override public double yawSpeed(){return 9;}
    @Override public double pitchSpeed(){return 6;}
    @Override public double elevation(){return 35;}
    @Override public double depression(){return 35;}
    @Override public double barrelLength(){return 2D-.0625D;}
    @Override public Vec3 horizontalOffset(){return switch(getBlockState().getValue(DummyableBlock.LEGACY_META)-10){case 2->new Vec3(1,0,1);case 4->new Vec3(1,0,0);case 5->new Vec3(0,0,1);default->Vec3.ZERO;};}
    public RotaryTurretEntity.Port[] ports(){return RotaryTurretEntity.ports(worldPosition,DummyableBlock.facing(getBlockState()));}
    public boolean portAt(BlockPos position,Direction side){if(side==null)return false;for(var port:ports())if(port.position().equals(position))return port.first()==side||port.second()==side;return false;}
    @Override public boolean canConnect(Direction side){return portAt(worldPosition,side);}
    @Override protected void connect(ServerLevel server){
        var facing=DummyableBlock.facing(getBlockState());
        if(facing!=nodeFacing){disconnectPorts();nodeFacing=facing;}
        for(var port:ports()){
            if(getBlockState().getBlock() instanceof TauonTurretBlock block)block.setPort(server,port.position(),true);
            var node=nodes.get(port.position());
            if(node==null||node.isExpired()){
                node=new PowerNode(port.position()).setConnections(new NodeConnection(port.position().relative(port.first()),port.first()),new NodeConnection(port.position().relative(port.second()),port.second()));
                HbmEnergy.nodespace(server).createNode(node);nodes.put(port.position(),node);
            }
            if(node.hasValidNet())node.addReceiver(this);
        }
    }
    @Override public void tickClient(){
        // Original child tick precedes the base's angle synchronization/interpolation tick.
        if(tPos!=null){var pos=turretPosition();lastDist=LegacyVector.createVectorHelper(tPos.x-pos.x,tPos.y-pos.y,tPos.z-pos.z).lengthVector();}
        if(beam>0)beam--;
        lastSpin=spin;
        if(tPos!=null)spin+=45;
        if(spin>=360F){spin-=360F;lastSpin-=360F;}
        super.tickClient();
    }
    @Override protected void updateFiringTick(){
        firingTimer++;
        if(firingTimer%5!=0)return;
        var config=firstLoaded();
        if(config==null||target==null)return;
        // Source BulletConfig.getDamage(null,null,SUBATOMIC): no fake shooter or projectile.
        target.hurt(SednaDamageSource.create(level,null,null,DamageResistanceHandler.DamageClass.SUBATOMIC),baseDamage()*(1+level.random.nextFloat()/3F));
        consumeAmmo(config);
        level.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),HbmSounds.get("weapon.tauShoot"),SoundSource.BLOCKS,4,.9F+level.random.nextFloat()*.3F);
        shotPacket=true;try{sync(250);}finally{shotPacket=false;}
        var pos=turretPosition();var offset=muzzleOffset();
        var packet=new WeldingParticlePacket(pos.x+offset.xCoord,pos.y+offset.yCoord,pos.z+offset.zCoord,5,false);
        if(level instanceof ServerLevel server)for(var player:server.players())
            if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<2500)PacketDistributor.sendToPlayer(player,packet);
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){
        if(!shotPacket)return super.getUpdateTag(lookup);
        var tag=new CompoundTag();tag.putBoolean("hbm_tauon_shot",true);return tag;
    }
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){
        if(tag.getBoolean("hbm_tauon_shot")){beam=3;return;}
        super.handleUpdateTag(tag,lookup);
    }
    private void disconnectPorts(){if(level instanceof ServerLevel server)for(var node:nodes.values())if(!node.isExpired())HbmEnergy.nodespace(server).destroyNode(node);nodes.clear();}
    @Override public void setRemoved(){disconnectPorts();super.setRemoved();}
    @Override public void onChunkUnloaded(){disconnectPorts();super.onChunkUnloaded();}
}
