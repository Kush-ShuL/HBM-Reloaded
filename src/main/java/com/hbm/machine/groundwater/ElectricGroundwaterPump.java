package com.hbm.machine.groundwater;

import com.hbm.energy.*;
import io.netty.buffer.ByteBuf;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public final class ElectricGroundwaterPump extends GroundwaterPumpEntity implements EnergyReceiver,SidedEnergyConnector {
    private BigInteger power=BigInteger.ZERO;
    private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();
    public ElectricGroundwaterPump(BlockPos pos,BlockState state){super(GroundwaterPumpContent.ELECTRIC_ENTITY.get(),pos,state,GroundwaterPumpKind.ELECTRIC);}
    @Override protected void updatePowerPorts(ServerLevel server){
        for(var connection:connections(worldPosition)){
            var position=connection.origin();var node=nodes.get(position);
            if(node==null||node.isExpired()){
                if(node!=null)HbmEnergy.nodespace(server).destroyNode(node);
                node=new PowerNode(position).setConnections(connection);HbmEnergy.nodespace(server).createNode(node);nodes.put(position,node);
            }
            if(node.hasValidNet())node.addReceiver(this);
        }
    }
    @Override protected void removePowerPorts(ServerLevel server){nodes.values().forEach(n->HbmEnergy.nodespace(server).destroyNode(n));nodes.clear();}
    @Override protected boolean canOperate(){return power.compareTo(BigInteger.valueOf(1000))>=0&&water.getFill()<water.getMaxFill();}
    @Override protected void operate(){power=power.subtract(BigInteger.valueOf(1000));water.setFill(Math.min(water.getFill()+GroundwaterPumpSettings.values().electricSpeed(),water.getMaxFill()));}
    @Override public long getPower(){return power.max(BigInteger.valueOf(Long.MIN_VALUE)).min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}
    @Override public long getMaxPower(){return 10000;}
    @Override public BigInteger getPowerExact(){return power;}
    @Override public BigInteger getMaxPowerExact(){return BigInteger.valueOf(10000);}
    @Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}
    @Override public void setPowerExact(BigInteger value){power=value;}
    @Override public long energyStored(){return getPower();}
    @Override public long energyCapacity(){return getMaxPower();}
    @Override protected void writeExtra(ByteBuf buffer){buffer.writeLong(getPower());}
    @Override protected void readExtra(ByteBuf buffer){power=BigInteger.valueOf(buffer.readLong());}
    @Override protected void writeExactPower(CompoundTag tag){if(power.bitLength()>=64)tag.putString("exactPower",power.toString());}
    @Override protected void readExactPower(CompoundTag tag){if(tag.contains("exactPower",8))power=new BigInteger(tag.getString("exactPower"));}
}
