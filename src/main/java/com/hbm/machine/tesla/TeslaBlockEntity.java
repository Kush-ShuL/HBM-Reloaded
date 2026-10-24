package com.hbm.machine.tesla;

import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import java.math.BigInteger;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** TileEntityTesla's original always-on native HE receiver and transient arc targets. */
public final class TeslaBlockEntity extends BlockEntity implements EnergyReceiver,EnergyConductor,MachineView, com.hbm.multiblock.MuffledMachine {
    public static final long CAPACITY=100000,COST=5000;
    public static final int RANGE=10;
    public static final double OFFSET=1.75;
    private static final BigInteger MAX=BigInteger.valueOf(CAPACITY),PULSE=BigInteger.valueOf(COST);
    private static final ResourceLocation METEOR_BATTERY=ResourceLocation.fromNamespaceAndPath("hbm","meteor_battery");
    private BigInteger power=BigInteger.ZERO;
    private PowerNode node;
    private boolean loaded=true;
    public boolean muffled,tilted;
    private List<Vec3> targets=List.of();
    public TeslaBlockEntity(BlockPos pos,BlockState state){super(TeslaContent.ENTITY.get(),pos,state);}
    public List<Vec3> targets(){return targets;}
    public Vec3 origin(){return new Vec3(worldPosition.getX()+.5,worldPosition.getY()+OFFSET,worldPosition.getZ()+.5);}
    public static void tick(Level level,BlockPos pos,BlockState state,TeslaBlockEntity coil){if(level instanceof ServerLevel server)coil.serverTick(server);}
    private void serverTick(ServerLevel server){
        if(node==null||node.isExpired()){
            node=HbmEnergy.nodespace(server).getNode(worldPosition);
            if(node==null||node.isExpired()){node=new PowerNode(worldPosition);HbmEnergy.nodespace(server).createNode(node);}
        }
        if(node.hasValidNet())node.addReceiver(this);
        targets=List.of();
        if(BuiltInRegistries.BLOCK.getKey(server.getBlockState(worldPosition.below()).getBlock()).equals(METEOR_BATTERY))power=MAX;
        if(power.compareTo(PULSE)>=0){power=power.subtract(PULSE);targets=TeslaDischarge.zap(server,origin(),RANGE,null);}
        // networkPackNT(100), including the empty list that ends a previous beam.
        var packet=getUpdatePacket();
        try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<10000)player.connection.send(packet);dispatchScope.commit();}
    }
    @Override public long getPower(){return power.longValueExact();}
    @Override public long getMaxPower(){return CAPACITY;}
    @Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}
    @Override public BigInteger getPowerExact(){return power;}
    @Override public BigInteger getMaxPowerExact(){return MAX;}
    @Override public void setPowerExact(BigInteger value){power=value.max(BigInteger.ZERO).min(MAX);setChanged();}
    @Override public long energyStored(){return getPower();}
    @Override public long energyCapacity(){return CAPACITY;}
    @Override public PowerNode energyNode(){return node;}
    @Override public boolean isLoaded(){return loaded&&!isRemoved()&&level!=null;}
    private void removeNode(){if(node!=null&&!node.isExpired()&&level instanceof ServerLevel server)HbmEnergy.nodespace(server).destroyNode(node);node=null;}
    @Override public void onLoad(){super.onLoad();loaded=true;}
    @Override public void onChunkUnloaded(){loaded=false;removeNode();super.onChunkUnloaded();}
    @Override public void setRemoved(){loaded=false;removeNode();super.setRemoved();}
    // Original Tesla does not override its base NBT to persist power or targets.
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){
        var tag=new CompoundTag();tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);tag.putLong("power",getPower());
        var list=new ListTag();for(Vec3 target:targets){var point=new CompoundTag();point.putDouble("x",target.x);point.putDouble("y",target.y);point.putDouble("z",target.z);list.add(point);}tag.put("targets",list);return tag;
    }
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");power=BigInteger.valueOf(tag.getLong("power"));var points=new java.util.ArrayList<Vec3>();for(Tag element:tag.getList("targets",Tag.TAG_COMPOUND)){var point=(CompoundTag)element;points.add(new Vec3(point.getDouble("x"),point.getDouble("y"),point.getDouble("z")));}targets=List.copyOf(points);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
