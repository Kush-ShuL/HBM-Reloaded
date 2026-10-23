package com.hbm.fluid.interop;

import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.multiblock.DummyableBlock;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Physical ports resolve to one core; existing native graph connections define the permitted faces. */
record NativeFluidEndpoint(FluidUser owner,Direction side,BlockEntity access,HbmFluidType admittedType) {
    boolean valid(HbmFluidType type){
        if(!owner.isLoaded()||(admittedType!=null&&admittedType!=type))return false;
        if(access==null)return true; // Existing network membership already admits its remote source port.
        if(access.isRemoved()||side==null||!owner.canConnect(type,side))return false;
        if(access.getBlockState().getBlock() instanceof DummyableBlock){
            if(!(access.getLevel() instanceof ServerLevel level))return false;
            var node=FluidNetworks.nodeAt(level,type,access.getBlockPos());
            return node!=null&&!node.isExpired()&&node.connections().contains(new NodeConnection(access.getBlockPos().relative(side),side));
        }
        return true;
    }
    static NativeFluidEndpoint network(FluidUser owner,HbmFluidType type){return new NativeFluidEndpoint(owner,null,null,type);}
    static NativeFluidEndpoint at(BlockEntity queried,Direction side){
        if(side==null||queried.isRemoved()||queried.getLevel()==null)return null;
        var level=queried.getLevel();var pos=queried.getBlockPos();var state=queried.getBlockState();
        if(state.getBlock() instanceof DummyableBlock block){
            if(!DummyableBlock.isPort(state))return null;var corePos=block.findCore(level,pos);if(corePos==null)return null;
            return level.getBlockEntity(corePos) instanceof FluidUser owner?new NativeFluidEndpoint(owner,side,queried,null):null;
        }
        return queried instanceof FluidUser owner?new NativeFluidEndpoint(owner,side,queried,null):null;
    }
}
