package com.hbm.api.machine;

import com.hbm.fluid.FluidTank;
import com.hbm.fluid.HbmFluids;
import com.hbm.fluid.network.FluidUser;
import com.hbm.fluid.network.StandardFluidTransceiver;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Source tank-order copying, NoID filtering and indexed paste; never transfers fluid contents. */
public interface IFluidCopiable extends ICopiable {
    default int[] getFluidIDToCopy() {
        var types=new ArrayList<Integer>();
        for(FluidTank tank:((FluidUser)this).getAllTanks()) if(!tank.getTankType().hasNoID()) types.add(tank.getTankType().getID());
        return types.stream().mapToInt(Integer::intValue).toArray();
    }
    default FluidTank getTankToPaste() {
        if(this instanceof StandardFluidTransceiver tile) { var tanks=tile.getReceivingTanks();return tanks!=null&&tanks.length>0?tanks[0]:null; }
        return null;
    }
    @Override default CompoundTag getSettings(Level level,BlockPos position) {
        var tag=new CompoundTag();if(getFluidIDToCopy().length>0) tag.putIntArray("fluidID",getFluidIDToCopy());return tag;
    }
    @Override default void pasteSettings(CompoundTag data,int index,Level level,Player player,BlockPos position) {
        FluidTank tank=getTankToPaste();if(tank==null) return;int[] ids=data.getIntArray("fluidID");
        if(ids.length>0&&index>=0&&index<ids.length) {
            var previous=tank.getTankType();tank.setTankType(HbmFluids.fromID(ids[index]));
            if(previous!=tank.getTankType()&&this instanceof BlockEntity machine) {
                machine.setChanged();
                if(machine.getLevel()!=null&&!machine.getLevel().isClientSide) machine.getLevel().sendBlockUpdated(machine.getBlockPos(),machine.getBlockState(),machine.getBlockState(),Block.UPDATE_CLIENTS);
            }
        }
    }
    @Override default String[] infoForDisplay(Level level,BlockPos position) {
        int[] ids=getFluidIDToCopy();String[] names=new String[ids.length];
        for(int i=0;i<ids.length;i++) names[i]=HbmFluids.fromID(ids[i]).getUnlocalizedName();return names;
    }
}
