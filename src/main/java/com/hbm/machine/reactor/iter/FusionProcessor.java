package com.hbm.machine.reactor.iter;

import com.hbm.energy.EnergyHandler;
import com.hbm.fluid.FluidTank;
import com.hbm.recipe.generic.*;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;

/** Exact source ModuleMachineFusion: progressive per-tick fuel, fractional speed and collector bonus output. */
public final class FusionProcessor extends GenericMachineProcessor {
    public double processSpeed=1,bonusSpeed,bonus;
    public FusionProcessor(Container inventory,FluidTank a,FluidTank b,FluidTank c,FluidTank output,EnergyHandler battery,Supplier<? extends List<? extends GenericMachineRecipe>> recipes){super(inventory,0,0,2,1,new FluidTank[]{a,b,c},new FluidTank[]{output},battery,recipes);}
    public void preUpdate(double processSpeed,double bonusSpeed){this.processSpeed=processSpeed;this.bonusSpeed=bonusSpeed;}
    @Override protected boolean hasInput(GenericMachineRecipe recipe){if(processSpeed<=0)return false;for(int i=0;i<Math.min(recipe.inputFluids().size(),inputTanks.length);i++)if(inputTanks[i].getFill()>0&&inputTanks[i].getFill()<(int)Math.ceil(recipe.inputFluids().get(i).fill*processSpeed))return false;return true;}
    @Override public void process(GenericMachineRecipe recipe,double speed,double power){battery.setPower(battery.getPower()-(long)Math.ceil((power==1?recipe.power():(long)(recipe.power()*power))*processSpeed));double step=Math.min(speed/recipe.duration()*processSpeed,1D);progress+=step;bonus+=step*bonusSpeed;bonus=Math.min(bonus,1.5D);for(int i=0;i<Math.min(recipe.inputFluids().size(),inputTanks.length);i++)inputTanks[i].setFill(Math.max(inputTanks[i].getFill()-(int)Math.ceil(recipe.inputFluids().get(i).fill*processSpeed),0));if(progress>=1){produceItem(recipe);if(canProcess(recipe,speed,power))progress-=1D;else progress=0D;}if(bonus>=1&&canFitOutput(recipe)){produceItem(recipe);bonus-=1D;}}
    @Override public void serialize(FriendlyByteBuf buffer){super.serialize(buffer);buffer.writeDouble(bonus);}@Override public void deserialize(FriendlyByteBuf buffer){super.deserialize(buffer);bonus=buffer.readDouble();}
    @Override public void writeToNBT(CompoundTag tag){super.writeToNBT(tag);tag.putDouble("bonus"+index,bonus);}@Override public void readFromNBT(CompoundTag tag){super.readFromNBT(tag);bonus=tag.getDouble("bonus"+index);}
}
