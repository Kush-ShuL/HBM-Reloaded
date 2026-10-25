package com.hbm.pneumatic;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
public final class PneumaticNode {
 BlockEntity owner;final Set<BlockPos> remote=new LinkedHashSet<>();int mask;PneumaticNetwork network;boolean expired,unloaded;
 PneumaticNode(BlockEntity owner,int mask){this.owner=owner;this.mask=mask;}
 public boolean isValid(){return !expired;}public PneumaticNetwork network(){return network!=null&&network.isValid()?network:null;}public BlockPos position(){return owner.getBlockPos();}
}
