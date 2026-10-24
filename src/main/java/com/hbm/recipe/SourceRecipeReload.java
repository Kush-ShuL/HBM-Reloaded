package com.hbm.recipe;

import com.hbm.recipe.generic.Blueprints;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.profiling.InactiveProfiler;

/** Calls the actual source handler sequence, without reposting lifecycle events. */
public final class SourceRecipeReload {
    private static final PreparableReloadListener.PreparationBarrier READY = new PreparableReloadListener.PreparationBarrier() {
        @Override public <T> CompletableFuture<T> wait(T prepared) { return CompletableFuture.completedFuture(prepared); }
    };

    public static void reload(MinecraftServer server) {
        // Reload only the same recipe manager's resources. Its existing NeoForge
        // condition context stays intact; no packs, tags or server reload is run.
        server.getRecipeManager().reload(READY, server.getResourceManager(),
            InactiveProfiler.INSTANCE, InactiveProfiler.INSTANCE, Runnable::run, Runnable::run).getNow(null);
        Blueprints.clearRecipes();
        // PressRecipes
        com.hbm.machine.press.PressRecipeConfig.reload(server);
        // BlastFurnaceRecipes
        com.hbm.machine.alloy.AlloyRecipeConfig.reload(server);
        // ShredderRecipes
        com.hbm.machine.shredder.ShredderRecipes.reload(server);
        // SolderingRecipes
        com.hbm.machine.soldering.SolderingRecipes.reload(server);
        // CombinationRecipes
        com.hbm.machine.combinationfurnace.CombinationFurnaceRecipes.reload(server);
        // CentrifugeRecipes
        com.hbm.machine.centrifuge.CentrifugeRecipeConfig.reload(server);
        // CrystallizerRecipes
        com.hbm.machine.crystallizer.CrystallizerRecipeConfig.reload(server);
        // RefineryRecipes
        com.hbm.machine.refinery.RefineryRecipeConfig.reload(server);
        // VacuumRefineryRecipes
        com.hbm.machine.vacuumdistill.VacuumDistillRecipeConfig.reload(server);
        // FractionRecipes
        com.hbm.machine.fractiontower.FractionRecipeConfig.reload(server);
        // CrackingRecipes
        com.hbm.machine.oilcatalyticcracker.CatalyticCrackerRecipes.reload(server);
        // ReformingRecipes
        com.hbm.machine.oilreformer.ReformerRecipeConfig.reload(server);
        // HydrotreatingRecipes
        com.hbm.machine.oilhydrotreater.HydrotreaterRecipes.reload(server);
        // LiquefactionRecipes
        com.hbm.machine.oilliquefactor.LiquefactionRecipes.reload(server);
        // SolidificationRecipes
        com.hbm.machine.solidifier.SolidificationRecipes.reload(server);
        // CokerRecipes
        com.hbm.machine.coker.CokerRecipeConfig.reload(server);
        // PyroOvenRecipes
        com.hbm.machine.pyrooven.PyroOvenRecipes.reload(server);
        // BreederRecipes
        com.hbm.machine.reactor.BreedingRecipeConfig.reload(server);
        // CyclotronRecipes
        com.hbm.machine.cyclotron.CyclotronRecipes.reload(server);
        // FuelPoolRecipes
        com.hbm.machine.reactor.fuelpool.FuelPoolRecipeConfig.reload(server);
        // MixerRecipes
        com.hbm.machine.oilmixer.MixerRecipeConfig.reload(server);
        // OutgasserRecipes
        com.hbm.machine.reactor.rbmk.irradiation.RbmkIrradiationRecipes.reload(server);
        // FluidBreederRecipes
        com.hbm.machine.reactor.iter.breeder.FluidBreedingRecipes.reload(server);
        // CompressorRecipes
        com.hbm.machine.compressor.CompressorRecipeConfig.reload(server);
        // ElectrolyserFluidRecipes + ElectrolyserMetalRecipes (same real loader, fluid first)
        com.hbm.machine.electrolyser.ElectrolyserRecipes.reload(server);
        // ArcWelderRecipes
        com.hbm.machine.arcwelder.ArcWelderRecipes.reload(server);
        // RotaryFurnaceRecipes
        com.hbm.machine.rotaryfurnace.RotaryFurnaceRecipes.reload(server);
        // ExposureChamberRecipes
        com.hbm.machine.exposurechamber.ExposureChamberRecipes.reload(server);
        // ParticleAcceleratorRecipes
        com.hbm.machine.pa.recipe.PaRecipes.reload(server);
        // AmmoPressRecipes
        com.hbm.machine.ammopress.AmmoPressRecipes.reload(server);
        // AnvilRecipes
        com.hbm.machine.anvil.AnvilRecipeConfig.reload(server);
        // PedestalRecipes
        com.hbm.machine.pedestal.PedestalRecipeConfig.reload(server);
        // AnnihilatorRecipes
        com.hbm.machine.annihilator.AnnihilatorRecipes.reload();
        // CrucibleRecipes
        com.hbm.machine.foundry.crucible.CrucibleRecipeConfig.reload(server);
        // AssemblyMachineRecipes
        com.hbm.machine.assembler.AssemblerRecipeConfig.reload(server);
        // ChemicalPlantRecipes
        com.hbm.machine.chemicalplant.ChemicalPlantRecipeConfig.reload(server);
        // PUREXRecipes
        com.hbm.machine.purex.PurexRecipeConfig.reload(server);
        // FusionRecipes
        com.hbm.machine.reactor.iter.FusionRecipeConfig.reload(server);
        // PrecAssRecipes
        com.hbm.machine.precisionassembler.PrecisionAssemblerRecipeConfig.reload(server);
        // PlasmaForgeRecipes
        com.hbm.machine.plasmaforge.PlasmaForgeRecipeConfig.reload(server);
        // BlastFurnaceRecipesNT
        com.hbm.machine.blastfurnace.BlastFurnaceRecipes.reload(server);
        // RockMillRecipes
        com.hbm.machine.rockmill.RockMillRecipeConfig.reload(server);
        // SuperComputerRecipes
        com.hbm.machine.supercomputer.SuperComputerRecipeConfig.reload(server);
        // SpaceAssemblerRecipes
        com.hbm.satellite.science.SpaceAssemblerRecipes.reload(server);
        // MatDistribution: rebuild real defaults, then the existing replacement reader.
        com.hbm.material.HbmMaterials.initializeDistributions();
        // CustomMachineRecipes
        com.hbm.machine.custommachine.CustomMachineRecipes.reload(server);
        // ArcFurnaceRecipes
        com.hbm.machine.arcfurnace.ArcFurnaceRecipes.reload(server);
    }

    private SourceRecipeReload() {}
}
