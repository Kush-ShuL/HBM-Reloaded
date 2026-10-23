package com.hbm.machine.assemblyfactory;
import com.hbm.energy.EnergyHandler;
import com.hbm.fluid.FluidTank;
import com.hbm.recipe.generic.*;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.Container;
/** Exact ModuleMachineAssembler layout and recipe-dependent4000 minimum/twice-quantity tanks; shared processing engine. */
public final class AssemblyFactoryProcessor extends GenericMachineProcessor {
 public AssemblyFactoryProcessor(Container inventory,int index,FluidTank input,FluidTank output,EnergyHandler battery,Supplier<? extends List<? extends GenericMachineRecipe>> recipes){super(inventory,5+index*14,12,17+index*14,1,new FluidTank[]{input},new FluidTank[]{output},battery,recipes);this.index=index;}
 @Override public void setupTanks(GenericMachineRecipe recipe){super.setupTanks(recipe);if(recipe==null)return;for(int i=0;i<inputTanks.length;i++)if(recipe.inputFluids().size()>i)inputTanks[i].changeTankSize(Math.max(Math.max(inputTanks[i].getFill(),recipe.inputFluids().get(i).fill*2),4000));for(int i=0;i<outputTanks.length;i++)if(recipe.outputFluids().size()>i)outputTanks[i].changeTankSize(Math.max(Math.max(outputTanks[i].getFill(),recipe.outputFluids().get(i).fill*2),4000));}
}
