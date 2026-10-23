package com.hbm.machine.converter;

import com.hbm.energy.ConnectionPriority;
import com.hbm.energy.EnergyReceiver;
import java.math.BigInteger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;

/** Original TileEntityConverterHeRf, receiving native HE and actively pushing modern FE. */
public final class HeToRfConverter extends ConverterBlockEntity implements EnergyReceiver {
    public HeToRfConverter(BlockPos pos,BlockState state){super(pos,state);}
    @Override public boolean heToRf(){return true;}
    @Override public ConnectionPriority getPriority(){return ConnectionPriority.LOW;}
    @Override protected void convert(ServerLevel level){
        var config=ConverterConfig.settings();
        long created=BigInteger.valueOf(power).divide(BigInteger.valueOf(config.heInput())).multiply(BigInteger.valueOf(config.rfOutput())).min(BigInteger.valueOf(MAX_RF-rfStored())).longValueExact();
        if(created>0){
            BigInteger numerator=BigInteger.valueOf(created).multiply(BigInteger.valueOf(config.heInput()));
            // Source defaults divide exactly. Round fractional configured debit upward so a nearly-full output buffer cannot generate free energy.
            long debit=numerator.add(BigInteger.valueOf(config.rfOutput()-1)).divide(BigInteger.valueOf(config.rfOutput())).longValueExact();
            power-=debit;setRf((int)(rfStored()+created));setChanged();
        }
        if(power>0){long previous=power;power*=1D-config.heInputDecay();if(power!=previous)setChanged();}
        for(Direction direction:Direction.values()){
            BlockPos target=worldPosition.relative(direction);if(!level.hasChunkAt(target))continue;
            var receiver=level.getCapability(Capabilities.EnergyStorage.BLOCK,target,direction.getOpposite());
            if(receiver!=null){int offered=extractRf(MAX_RF,true);int accepted=receiver.receiveEnergy(offered,false);if(accepted>0){extractRf(accepted,false);setChanged();}}
        }
    }
}
