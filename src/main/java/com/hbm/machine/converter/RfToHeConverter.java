package com.hbm.machine.converter;

import com.hbm.energy.EnergyProvider;
import java.math.BigInteger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

/** Original TileEntityConverterRfHe: FE input only, native HE provider, original integer grouping. */
public final class RfToHeConverter extends ConverterBlockEntity implements EnergyProvider {
    public RfToHeConverter(BlockPos pos,BlockState state){super(pos,state);}
    @Override public boolean heToRf(){return false;}
    @Override protected void convert(ServerLevel level){
        var config=ConverterConfig.settings();
        int previousRf=rfStored();
        long used=BigInteger.valueOf(MAX_POWER-power).multiply(BigInteger.valueOf(config.rfInput())).divide(BigInteger.valueOf(config.heOutput())).min(BigInteger.valueOf(rfStored())).longValueExact();
        setRf((int)(rfStored()-used));
        power+=BigInteger.valueOf(used).multiply(BigInteger.valueOf(config.heOutput())).divide(BigInteger.valueOf(config.rfInput())).longValueExact();
        if(rfStored()>0)extractRf((int)Math.ceil(rfStored()*config.rfInputDecay()),false);
        if(used>0||rfStored()!=previousRf)setChanged();
    }
}
