package com.hbm.machine.supercomputer;
import com.hbm.energy.EnergyHandler;
import com.hbm.fluid.FluidTank;
import com.hbm.recipe.generic.*;
import java.math.BigInteger;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.Container;
/** Source ModuleMachineSuperComputer3+3slots/1+1tanks; exactnativeHE delta, actual weighted output inherited. */
public final class SuperComputerProcessor extends GenericMachineProcessor {
 public SuperComputerProcessor(Container inventory,FluidTank input,FluidTank output,EnergyHandler power,Supplier<? extends List<? extends GenericMachineRecipe>> recipes){super(inventory,2,3,5,3,new FluidTank[]{input},new FluidTank[]{output},power,recipes);}
 @Override public void setupTanks(GenericMachineRecipe recipe){super.setupTanks(recipe);if(recipe==null)return;for(int i=0;i<inputTanks.length;i++)if(recipe.inputFluids().size()>i)inputTanks[i].changeTankSize(Math.max(Math.max(inputTanks[i].getFill(),recipe.inputFluids().get(i).fill*2),4000));for(int i=0;i<outputTanks.length;i++)if(recipe.outputFluids().size()>i)outputTanks[i].changeTankSize(Math.max(Math.max(outputTanks[i].getFill(),recipe.outputFluids().get(i).fill*2),4000));}
 @Override public void process(GenericMachineRecipe recipe,double speed,double power){if(restrictedMode)speed*=.25D;battery.setPowerExact(battery.getPowerExact().subtract(BigInteger.valueOf(power==1?recipe.power():(long)(recipe.power()*power))));progress+=Math.min(speed/recipe.duration(),1D);if(progress>=1D){consumeInput(recipe);produceItem(recipe);if(canProcess(recipe,speed,power))progress-=1D;else progress=0D;}}
}
