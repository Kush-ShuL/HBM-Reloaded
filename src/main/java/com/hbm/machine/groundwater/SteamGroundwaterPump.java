package com.hbm.machine.groundwater;

import com.hbm.fluid.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Source STEAM100 -> SPENTSTEAM1, producing configured water even if the last partial tank fill wastes yield. */
public final class SteamGroundwaterPump extends GroundwaterPumpEntity {
    public final FluidTank steam=new FluidTank(HbmFluids.fromName("STEAM"),1000);
    public final FluidTank lps=new FluidTank(HbmFluids.fromName("SPENTSTEAM"),10);
    public SteamGroundwaterPump(BlockPos pos,BlockState state){super(GroundwaterPumpContent.STEAM_ENTITY.get(),pos,state,GroundwaterPumpKind.STEAM);}
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{water,steam,lps};}
    @Override public FluidTank[] getSendingTanks(){return new FluidTank[]{water,lps};}
    @Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{steam};}
    @Override protected boolean canOperate(){return steam.getFill()>=100&&lps.getMaxFill()-lps.getFill()>0&&water.getFill()<water.getMaxFill();}
    @Override protected void operate(){steam.setFill(steam.getFill()-100);lps.setFill(lps.getFill()+1);water.setFill(Math.min(water.getFill()+GroundwaterPumpSettings.values().steamSpeed(),water.getMaxFill()));}
    @Override protected void writeExtra(ByteBuf buffer){steam.serialize(buffer);lps.serialize(buffer);}
    @Override protected void readExtra(ByteBuf buffer){steam.deserialize(buffer);lps.deserialize(buffer);}
}
