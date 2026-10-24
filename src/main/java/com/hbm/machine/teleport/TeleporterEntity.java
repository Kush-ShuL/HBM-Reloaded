package com.hbm.machine.teleport;

import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import com.hbm.multiblock.MuffledMachine;
import java.math.BigInteger;
import net.minecraft.core.*;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.*;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

/** One native HE node around the source cube, per-entity cost and original unconditional failed-attempt charge. */
public final class TeleporterEntity extends BlockEntity implements EnergyReceiver,SidedEnergyConnector,MachineView,MuffledMachine {
    public static final BigInteger CAPACITY=BigInteger.valueOf(1_500_000),CONSUMPTION=BigInteger.valueOf(1_000_000);
    public BigInteger power=BigInteger.ZERO;
    public int targetX=-1,targetY=-1,targetZ=-1,targetDim;
    /** Only modern dimensions without a bound numeric source ID need this additional namespaced destination. */
    public String targetDimension="";
    public boolean muffled,tilted;
    private PowerNode node;
    private CompoundTag lastPacked;
    public TeleporterEntity(BlockPos pos,BlockState state){super(TeleportContent.ENTITY.get(),pos,state);}
    public static void tick(Level level,BlockPos pos,BlockState state,TeleporterEntity machine){
        if(level instanceof ServerLevel server)machine.serverTick(server);
        else if(machine.targetY!=-1&&machine.power.compareTo(CONSUMPTION)>=0){
            double x=pos.getX()+.5+level.random.nextGaussian()*.25;
            double y=pos.getY()+1+level.random.nextDouble()*2;
            double z=pos.getZ()+.5+level.random.nextGaussian()*.25;
            level.addParticle(new DustParticleOptions(new Vector3f(.4F,.8F,1F),1),x,y,z,0,0,0);
        }
    }
    private void serverTick(ServerLevel world){
        if(node==null||node.isExpired()){node=new PowerNode(worldPosition);HbmEnergy.nodespace(world).createNode(node);}
        if(node.hasValidNet())node.addReceiver(this);
        if(targetY!=-1){
            var box=new AABB(worldPosition.getX()+.25,worldPosition.getY(),worldPosition.getZ()+.25,worldPosition.getX()+.75,worldPosition.getY()+2,worldPosition.getZ()+.75);
            for(Entity entity:world.getEntitiesOfClass(Entity.class,box,entity->true))teleport(entity);
        }
        // LoadedBase.networkPackNT(15): changed wire values, or the original one-second reload recovery.
        var tag=getUpdateTag(world.registryAccess());
        if(!tag.equals(lastPacked)||world.getGameTime()%20==0){
            lastPacked=tag;var packet=getUpdatePacket();
            try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(world.getServer(),packet)){for(var player:world.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<15D*15D)player.connection.send(packet);dispatchScope.commit();}
        }
    }
    public ResourceKey<Level> destination(){return TeleportDimensions.resolve(targetDim,targetDimension);}
    public String dimensionLabel(){return targetDimension.isEmpty()?Integer.toString(targetDim):targetDimension;}
    public void teleport(Entity entity){
        if(!(level instanceof ServerLevel world)||power.compareTo(CONSUMPTION)<0)return;
        sound(world,worldPosition.getX()+.5,worldPosition.getY()+1.5,worldPosition.getZ()+.5);
        var destination=destination();
        boolean cross=destination==null||!destination.equals(entity.level().dimension());
        double y=targetY+1.5+LegacyTeleport.offset(entity,cross);
        LegacyTeleport.move(entity,targetX+.5,y,targetZ+.5,destination);
        // Original invokes the second sound through this same source world, even after a dimension change/failed lookup.
        sound(world,entity.getX(),entity.getY(),entity.getZ());
        power=power.subtract(CONSUMPTION);setChanged();
    }
    private static void sound(Level world,double x,double y,double z){world.playSound(null,x,y,z,SoundEvents.ENDERMAN_TELEPORT,SoundSource.BLOCKS,1F,1F);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);state(tag);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    private void state(CompoundTag tag){tag.putString("power",power.toString());tag.putInt("x1",targetX);tag.putInt("y1",targetY);tag.putInt("z1",targetZ);tag.putInt("dim",targetDim);if(!targetDimension.isEmpty())tag.putString(TeleportDimensions.KEY,targetDimension);}
    private void readState(CompoundTag tag){power=tag.contains("power",Tag.TAG_STRING)?new BigInteger(tag.getString("power")):BigInteger.valueOf(tag.getLong("power"));targetX=tag.getInt("x1");targetY=tag.getInt("y1");targetZ=tag.getInt("z1");targetDim=tag.getInt("dim");targetDimension=tag.getString(TeleportDimensions.KEY);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);readState(tag);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();state(tag);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){readState(tag);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    private void disconnect(){if(node!=null&&level instanceof ServerLevel world)HbmEnergy.nodespace(world).destroyNode(node);node=null;}
    @Override public void setRemoved(){disconnect();super.setRemoved();}
    @Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public boolean isLoaded(){return level!=null&&!isRemoved()&&level.hasChunkAt(worldPosition);}
    @Override public boolean canConnect(Direction side){return side!=null;}
    @Override public long getPower(){return power.longValueExact();}
    @Override public long getMaxPower(){return 1_500_000;}
    @Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}
    @Override public BigInteger getPowerExact(){return power;}
    @Override public BigInteger getMaxPowerExact(){return CAPACITY;}
    @Override public void setPowerExact(BigInteger value){power=value;setChanged();}
    @Override public BigInteger transferPowerExact(BigInteger amount){if(amount.signum()<0)throw new IllegalArgumentException("Negative HE transfer");var room=CAPACITY.subtract(power);if(room.signum()<0)throw new IllegalStateException("Invalid source teleporter HE");var accepted=amount.min(room);power=power.add(accepted);if(accepted.signum()!=0)setChanged();return amount.subtract(accepted);}
    @Override public long transferPower(long amount){return transferPowerExact(BigInteger.valueOf(amount)).longValueExact();}
    @Override public long energyStored(){return getPower();}
    @Override public long energyCapacity(){return getMaxPower();}
    @Override public boolean isMuffled(){return muffled;}
    @Override public void setMuffled(boolean value){muffled=value;setChanged();}
}
