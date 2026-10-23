package com.hbm.machine.rockmill;
import com.hbm.energy.EnergyHandler;
import com.hbm.fluid.FluidTank;
import com.hbm.recipe.generic.*;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.Container;
/** Exact source ModuleMachineRockMill tank adapter over the one shared processing engine. */
public final class RockMillProcessor extends GenericMachineProcessor {
 public RockMillProcessor(Container inventory,FluidTank input,FluidTank output,EnergyHandler battery,Supplier<? extends List<? extends GenericMachineRecipe>> recipes){super(inventory,2,3,5,3,new FluidTank[]{input},new FluidTank[]{output},battery,recipes);withOreProcessing();}
 @Override public void setupTanks(GenericMachineRecipe recipe){super.setupTanks(recipe);if(recipe==null)return;for(int i=0;i<inputTanks.length;i++)if(recipe.inputFluids().size()>i)inputTanks[i].changeTankSize(Math.max(Math.max(inputTanks[i].getFill(),recipe.inputFluids().get(i).fill*2),4000));for(int i=0;i<outputTanks.length;i++)if(recipe.outputFluids().size()>i)outputTanks[i].changeTankSize(Math.max(Math.max(outputTanks[i].getFill(),recipe.outputFluids().get(i).fill*2),4000));}
}
