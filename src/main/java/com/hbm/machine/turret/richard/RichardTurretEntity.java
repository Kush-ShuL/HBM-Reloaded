package com.hbm.machine.turret.richard;

import com.hbm.energy.*;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.machine.turret.TurretBaseEntity;
import com.hbm.machine.turret.rotary.RotaryTurretEntity;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.missilelauncher.MissileLauncherBullets;
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

/** Source Richard: the one turret engine, its real five rocket_ml configs, original reload order and target-steered MK4. */
public final class RichardTurretEntity extends TurretBaseEntity {
    public int loaded;
    private int firingTimer, reload;
    private boolean loadedPacket;
    private final Map<BlockPos,PowerNode> nodes = new LinkedHashMap<>();
    private Direction nodeFacing;
    public RichardTurretEntity(BlockPos pos,BlockState state) { super(RichardTurretContent.ENTITY.get(),pos,state); }
    @Override public boolean isValidBlockState(BlockState state) { return getType().isValid(state) && DummyableBlock.isCore(state); }
    @Override protected String title() { return "container.turretRichard"; }
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player) { return new RichardTurretMenu(id,inventory,this,worldPosition); }
    @Override public void writeClientSideData(AbstractContainerMenu menu,RegistryFriendlyByteBuf buffer) { buffer.writeBlockPos(worldPosition); }
    @Override protected List<BulletConfig> ammoConfigs() { return Arrays.asList(MissileLauncherBullets.ROCKETS); }
    @Override protected float baseDamage() { return RichardTurretSettings.damage(); }
    @Override public long getMaxPower() { return 10000; }
    @Override public double detectorRange() { return 64; }
    @Override public double detectorGrace() { return 8; }
    @Override public double depression() { return 25; }
    @Override public double elevation() { return 25; }
    @Override public double barrelLength() { return 1.25; }
    @Override public Vec3 horizontalOffset() { return switch(getBlockState().getValue(DummyableBlock.LEGACY_META)-10) { case 2 -> new Vec3(1,0,1); case 4 -> new Vec3(1,0,0); case 5 -> new Vec3(0,0,1); default -> Vec3.ZERO; }; }
    public RotaryTurretEntity.Port[] ports() { return RotaryTurretEntity.ports(worldPosition,DummyableBlock.facing(getBlockState())); }
    public boolean portAt(BlockPos position,Direction side) { if(side==null)return false;for(var port:ports())if(port.position().equals(position))return port.first()==side||port.second()==side;return false; }
    @Override public boolean canConnect(Direction side) { return portAt(worldPosition,side); }
    @Override protected void connect(ServerLevel server) {
        var facing=DummyableBlock.facing(getBlockState());if(facing!=nodeFacing){disconnectPorts();nodeFacing=facing;}
        for(var port:ports()) {
            if(getBlockState().getBlock() instanceof RichardTurretBlock block)block.setPort(server,port.position(),true);
            var node=nodes.get(port.position());
            if(node==null||node.isExpired()) {
                node=new PowerNode(port.position()).setConnections(new NodeConnection(port.position().relative(port.first()),port.first()),new NodeConnection(port.position().relative(port.second()),port.second()));
                HbmEnergy.nodespace(server).createNode(node);nodes.put(port.position(),node);
            }
            if(node.hasValidNet())node.addReceiver(this);
        }
    }
    @Override public void tickServer(ServerLevel server) {
        // Source first performs the entire base tick/firing/base sync, then advances its reload and sends its second packet.
        super.tickServer(server);
        if(reload>0) { reload--;if(reload==0)loaded=17; }
        if(loaded<=0&&reload<=0&&firstLoaded()!=null)reload=100;
        if(firstLoaded()==null)loaded=0;
        loadedPacket=true;try{sync(250);}finally{loadedPacket=false;}
    }
    @Override protected void updateFiringTick() {
        if(reload>0)return;
        firingTimer++;
        if(firingTimer>0&&firingTimer%10==0) {
            var config=firstLoaded();
            if(config!=null) {
                spawnBullet(config);consumeAmmo(config);
                level.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),HbmSounds.get("turret.richard_fire"),SoundSource.BLOCKS,2,1);
                loaded--;
            } else loaded=0;
        }
    }
    @Override protected void spawnBullet(BulletConfig config) {
        var origin=turretPosition();var offset=muzzleOffset();
        var projectile=new EntityBulletBaseMK4(level,config,baseDamage(),config.spread,(float)rotationYaw,(float)rotationPitch);
        projectile.moveTo(origin.x+offset.xCoord,origin.y+offset.yCoord,origin.z+offset.zCoord,projectile.getYRot(),projectile.getXRot());
        projectile.lockonTarget=target;level.addFreshEntity(projectile);
    }
    @Override protected void saveMachine(CompoundTag tag) { super.saveMachine(tag);tag.putInt("loaded",loaded); }
    @Override protected void loadMachine(CompoundTag tag) { super.loadMachine(tag);loaded=tag.getInt("loaded"); }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) {
        if(!loadedPacket)return super.getUpdateTag(lookup);
        var tag=new CompoundTag();tag.putBoolean("hbm_richard_loaded_packet",true);tag.putInt("loaded",loaded);return tag;
    }
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup) {
        if(tag.getBoolean("hbm_richard_loaded_packet")){loaded=tag.getInt("loaded");return;}
        super.handleUpdateTag(tag,lookup);
    }
    private void disconnectPorts() { if(level instanceof ServerLevel server)for(var node:nodes.values())if(!node.isExpired())HbmEnergy.nodespace(server).destroyNode(node);nodes.clear(); }
    @Override public void setRemoved() { disconnectPorts();super.setRemoved(); }
    @Override public void onChunkUnloaded() { disconnectPorts();super.onChunkUnloaded(); }
}
