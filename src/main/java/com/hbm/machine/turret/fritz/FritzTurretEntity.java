package com.hbm.machine.turret.fritz;

import com.hbm.api.machine.IFluidCopiable;
import com.hbm.energy.*;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.turret.TurretBaseEntity;
import com.hbm.machine.turret.rotary.RotaryTurretEntity;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.GunAmmo;
import com.hbm.weapon.sedna.gun.flamer.FlamerBullets;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Source Fritz overrides only its own fuel/ballistics; acquisition, CC12 and ROR use the one real turret engine. */
public final class FritzTurretEntity extends TurretBaseEntity implements StandardFluidReceiver,IFluidCopiable {
    public final FluidTank tank=new FluidTank(HbmFluids.DIESEL,16000);
    private final Map<BlockPos,PowerNode> powerPorts=new LinkedHashMap<>();
    private FluidPorts fluidPorts;
    private Direction nodeFacing;
    private List<ItemStack> displayed;
    public FritzTurretEntity(BlockPos pos,BlockState state){super(FritzTurretContent.ENTITY.get(),pos,state);}
    @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}
    @Override protected String title(){return "container.turretFritz";}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new FritzTurretMenu(id,inventory,this,worldPosition);}
    @Override public void writeClientSideData(AbstractContainerMenu menu,RegistryFriendlyByteBuf buffer){buffer.writeBlockPos(worldPosition);}
    @Override protected List<BulletConfig> ammoConfigs(){return List.of();}
    @Override protected float baseDamage(){return FritzTurretSettings.damage();}
    @Override public long getMaxPower(){return 10000;}
    @Override public double detectorRange(){return 48;}
    @Override public double detectorGrace(){return 2;}
    @Override public double elevation(){return 45;}
    @Override public double barrelLength(){return 2.25;}
    @Override public double acceptableInaccuracy(){return 15;}
    @Override public Vec3 horizontalOffset(){return switch(getBlockState().getValue(DummyableBlock.LEGACY_META)-10){case 2->new Vec3(1,0,1);case 4->new Vec3(1,0,0);case 5->new Vec3(0,0,1);default->Vec3.ZERO;};}
    public RotaryTurretEntity.Port[] ports(){return RotaryTurretEntity.ports(worldPosition,DummyableBlock.facing(getBlockState()));}
    public boolean portAt(BlockPos position,Direction side){if(side==null)return false;for(var port:ports())if(port.position().equals(position))return port.first()==side||port.second()==side;return false;}
    @Override public boolean canConnect(Direction side){return portAt(worldPosition,side);}
    @Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null&&side.getAxis()!=Direction.Axis.Y;}
    @Override protected void connect(ServerLevel server){
        var facing=DummyableBlock.facing(getBlockState());
        if(facing!=nodeFacing){disconnectPorts();nodeFacing=facing;}
        var connections=new ArrayList<NodeConnection>();var positions=new ArrayList<BlockPos>();
        for(var port:ports()){
            if(getBlockState().getBlock() instanceof FritzTurretBlock block)block.setPort(server,port.position(),true);
            positions.add(port.position());
            var first=new NodeConnection(port.position().relative(port.first()),port.first());
            var second=new NodeConnection(port.position().relative(port.second()),port.second());
            connections.add(first);connections.add(second);
            var node=powerPorts.get(port.position());
            if(node==null||node.isExpired()){node=new PowerNode(port.position()).setConnections(first,second);HbmEnergy.nodespace(server).createNode(node);powerPorts.put(port.position(),node);}
            if(node.hasValidNet())node.addReceiver(this);
        }
        if(fluidPorts==null)fluidPorts=new FluidPorts(positions.toArray(BlockPos[]::new),connections.toArray(NodeConnection[]::new));
        fluidPorts.update(server,this);
    }
    @Override protected void updateFiringTick(){
        var type=tank.getTankType();var flammable=type.getTrait(FluidTraits.Flammable.class);
        if(flammable==null||!type.hasTrait("liquid")||tank.getFill()<2)return;
        tank.setFill(tank.getFill()-2);
        var position=turretPosition();var vector=muzzleOffset();
        float damage=Math.min((float)(flammable.getHeatEnergy()/500000F),20F)*baseDamage();
        var projectile=new EntityBulletBaseMK4(level,type==HbmFluids.BALEFIRE?FlamerBullets.flame_nograv_bf:FlamerBullets.flame_nograv,damage,.05F,(float)rotationYaw,(float)rotationPitch);
        projectile.moveTo(position.x+vector.xCoord,position.y+vector.yCoord,position.z+vector.zCoord,projectile.getYRot(),projectile.getXRot());
        level.addFreshEntity(projectile);
        level.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),HbmSounds.get("weapon.flamethrowerShoot"),SoundSource.BLOCKS,2,1+level.random.nextFloat()*.5F);
    }
    @Override public void tickServer(ServerLevel server){
        // Source calls the entire base update (including sync) before loading fluid containers.
        super.tickServer(server);
        var inventory=slots.toArray(ItemStack[]::new);
        tank.setType(9,9,inventory);
        for(int slot=1;slot<9;slot++)tank.loadTank(slot,9,inventory);
        for(int slot=1;slot<10;slot++)if(GunAmmo.standard(63).matchesRecipe(inventory[slot],true)&&tank.getTankType()==HbmFluids.DIESEL&&tank.getFill()+1000<=tank.getMaxFill()){
            tank.setFill(tank.getFill()+1000);inventory[slot].shrink(1);
        }
        for(int slot=0;slot<inventory.length;slot++)slots.set(slot,inventory[slot]);
    }
    @Override public List<ItemStack> displayedAmmo(){
        if(displayed==null){var list=new ArrayList<ItemStack>();list.add(GunAmmo.standard(63).toStack());for(var type:HbmFluids.getInNiceOrder())if(type.hasTrait(FluidTraits.Combustible.class)&&type.hasTrait("liquid"))list.add(FluidIconItem.make(type,0));displayed=List.copyOf(list);}return displayed;
    }
    @Override public int[] getSlotsForFace(Direction side){return new int[]{1,2,3,4,5,6,7,8};}
    @Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tank};}
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{tank};}
    @Override public FluidTank getTankToPaste(){return tank;}
    @Override public List<TankView> tanks(){return List.of(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill()));}
    @Override protected void saveMachine(CompoundTag tag){super.saveMachine(tag);tank.writeToNBT(tag,"diesel");}
    @Override protected void loadMachine(CompoundTag tag){super.loadMachine(tag);tank.readFromNBT(tag,"diesel");}
    private void disconnectPorts(){if(level instanceof ServerLevel server){for(var node:powerPorts.values())if(!node.isExpired())HbmEnergy.nodespace(server).destroyNode(node);if(fluidPorts!=null)fluidPorts.remove(server);}powerPorts.clear();fluidPorts=null;}
    @Override public void setRemoved(){disconnectPorts();super.setRemoved();}
    @Override public void onChunkUnloaded(){disconnectPorts();super.onChunkUnloaded();}
}
