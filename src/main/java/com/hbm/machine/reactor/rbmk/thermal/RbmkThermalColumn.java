package com.hbm.machine.reactor.rbmk.thermal;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnEntity;
import com.hbm.machine.reactor.rbmk.core.RbmkFluxColumn;
import com.hbm.machine.reactor.rbmk.fluid.RbmkAdaptivePorts;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
/** Shared actual adaptive native port topology; no additional tank, HE or generic machine behavior. */
public abstract class RbmkThermalColumn extends RbmkColumnEntity implements StandardFluidTransceiver {
    protected final RbmkAdaptivePorts ports;
    protected RbmkThermalColumn(BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state);ports=new RbmkAdaptivePorts(pos);}
    @Override public RbmkFluxColumn.Type neutronType(){return RbmkFluxColumn.Type.OTHER;}
    protected void ports(ServerLevel level){ports.update(level,this);}
    private void disconnect(){if(level instanceof ServerLevel world)ports.remove(world);}
    @Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public boolean canConnect(HbmFluidType type,Direction side){return level!=null&&side==Direction.DOWN&&ports.loaderMode(level)!=1&&(type==getAllTanks()[0].getTankType()||type==getAllTanks()[1].getTankType());}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rejected=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(rejected!=amount)setChanged();return rejected;}
    @Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);if(amount>0)setChanged();}
    @Override public List<TankView> tanks(){return java.util.Arrays.stream(getAllTanks()).map(tank->new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill())).toList();}
}
