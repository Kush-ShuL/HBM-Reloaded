package com.hbm.machine.pneumaticstorage;

import api.hbm.ntl.StackCache;
import com.hbm.pneumatic.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

/** Actual per-machine accessor cache over the unique shared pneumatic graph. */
public abstract class PneumaticMachineBase extends PneumaticInventory {
    protected PneumaticNode node;
    public StackCache cache;
    protected PneumaticMachineBase(PneumaticKind kind,BlockPos pos,BlockState state){super(kind,pos,state);}
    @Override protected void updateServer(ServerLevel server){
        if(node==null||!node.isValid()){if(cache!=null)cache.dissolveCache();node=PneumaticNetworks.connect(this);}else node=PneumaticNetworks.connect(this);
        if(cache==null||cache.hasExpired)cache=new StackCache(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ());
        if(node!=null&&node.isValid()&&node.network()!=null)node.network().addStackCache(cache);
    }
    protected void disconnect(){if(cache!=null)cache.dissolveCache();PneumaticNetworks.disconnect(this);node=null;}
    @Override public void setRemoved(){disconnect();super.setRemoved();}
    @Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
}
