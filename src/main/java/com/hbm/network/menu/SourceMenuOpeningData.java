package com.hbm.network.menu;

import com.hbm.machine.oilextraction.OilDrillEntity;
import com.hbm.machine.storage.LockableBlockEntity;
import com.hbm.pneumatic.tube.PneumoTubeBlockEntity;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Context for the vanilla MenuProvider path, restricted to actual, source-evidenced HBM schemas.
 * Normal explicit opening callbacks and a provider's own writer remain authoritative. */
public final class SourceMenuOpeningData {
    private static final Set<String> BLOCK_POSITION_ONLY=Set.of(
            "com.hbm.bomb.nuclear.programmable.CustomNuclearBlockEntity",
            "com.hbm.bomb.nuclear.programmable.TimedBalefireBlockEntity",
            "com.hbm.machine.alloy.AlloyBlockEntity",
            "com.hbm.machine.ammopress.AmmoPressEntity",
            "com.hbm.machine.annihilator.AnnihilatorEntity",
            "com.hbm.machine.arcfurnace.ArcFurnaceBlockEntity",
            "com.hbm.machine.arcwelder.ArcWelderBlockEntity",
            "com.hbm.machine.ashpit.AshpitBlockEntity",
            "com.hbm.machine.assembler.AssemblerBlockEntity",
            "com.hbm.machine.autocrafter.AutocrafterBlockEntity",
            "com.hbm.machine.barrel.BarrelBlockEntity",
            "com.hbm.machine.bat9000.Bat9000BlockEntity",
            "com.hbm.machine.batterysocket.BatterySocketBlockEntity",
            "com.hbm.machine.bigasstank.BigAssTankBlockEntity",
            "com.hbm.machine.blastfurnace.BlastFurnaceBlockEntity",
            "com.hbm.machine.centrifuge.CentrifugeBlockEntity",
            "com.hbm.machine.chemicalfactory.ChemicalFactoryEntity",
            "com.hbm.machine.chemicalplant.ChemicalPlantBlockEntity",
            "com.hbm.machine.coker.CokerEntity",
            "com.hbm.machine.compressor.CompressorBlockEntity",
            "com.hbm.machine.crystallizer.CrystallizerBlockEntity",
            "com.hbm.machine.electrolyser.ElectrolyserBlockEntity",
            "com.hbm.machine.excavator.ExcavatorBlockEntity",
            "com.hbm.machine.fel.FelBlockEntity",
            "com.hbm.machine.fensu.FensuBlockEntity",
            "com.hbm.machine.fluidtank.IndustrialFluidTankBlockEntity",
            "com.hbm.machine.forcefield.ForceFieldBlockEntity",
            "com.hbm.machine.foundry.crucible.CrucibleBlockEntity",
            "com.hbm.machine.foundry.crucible.FireboxBlockEntity",
            "com.hbm.machine.gascentrifuge.GasCentrifugeBlockEntity",
            "com.hbm.machine.generator.combustion.CombustionBlockEntity",
            "com.hbm.machine.generator.diesel.DieselBlockEntity",
            "com.hbm.machine.generator.gasturbine.GasTurbineBlockEntity",
            "com.hbm.machine.heaterheatex.HeatexBlockEntity",
            "com.hbm.machine.heatingoven.HeatingOvenBlockEntity",
            "com.hbm.machine.keyforge.KeyForgeBlockEntity",
            "com.hbm.machine.massstorage.MassStorageBlockEntity",
            "com.hbm.machine.oilburner.OilburnerBlockEntity",
            "com.hbm.machine.oilflare.FlareEntity",
            "com.hbm.machine.oilfracking.FrackingEntity",
            "com.hbm.machine.oilhydrotreater.HydrotreaterBlockEntity",
            "com.hbm.machine.oilliquefactor.LiquefactorEntity",
            "com.hbm.machine.oilmixer.MixerEntity",
            "com.hbm.machine.oilreformer.ReformerEntity",
            "com.hbm.machine.oreslopper.OreSlopperBlockEntity",
            "com.hbm.machine.pa.cooled.PaDetectorEntity",
            "com.hbm.machine.pa.cooled.PaDipoleEntity",
            "com.hbm.machine.pa.cooled.PaQuadrupoleEntity",
            "com.hbm.machine.pa.cooled.PaRfcEntity",
            "com.hbm.machine.pa.cooled.PaSourceEntity",
            "com.hbm.machine.plasmaforge.PlasmaForgeBlockEntity",
            "com.hbm.machine.pneumaticstorage.PneumaticAccess",
            "com.hbm.machine.pneumaticstorage.PneumaticClutter",
            "com.hbm.machine.pneumaticstorage.PneumaticExporter",
            "com.hbm.machine.pneumaticstorage.PneumaticImporter",
            "com.hbm.machine.pneumaticstorage.PneumaticMono",
            "com.hbm.machine.press.PressBlockEntity",
            "com.hbm.machine.purex.PurexBlockEntity",
            "com.hbm.machine.pyrooven.PyroOvenEntity",
            "com.hbm.machine.radar.RadarBlockEntity",
            "com.hbm.machine.radiolysis.RadiolysisBlockEntity",
            "com.hbm.machine.reactor.BreedingReactorBlockEntity",
            "com.hbm.machine.reactor.ResearchReactorBlockEntity",
            "com.hbm.machine.reactor.dfc.DfcCoreEntity",
            "com.hbm.machine.reactor.dfc.components.DfcEmitterEntity",
            "com.hbm.machine.reactor.dfc.components.DfcInjectorEntity",
            "com.hbm.machine.reactor.dfc.components.DfcReceiverEntity",
            "com.hbm.machine.reactor.dfc.components.DfcStabilizerEntity",
            "com.hbm.machine.reactor.fuelpool.FuelPoolBlockEntity",
            "com.hbm.machine.reactor.icf.IcfReactorEntity",
            "com.hbm.machine.reactor.icf.pellet.IcfPelletPressEntity",
            "com.hbm.machine.reactor.iter.breeder.FusionBreederEntity",
            "com.hbm.machine.reactor.iter.klystron.KlystronEntity",
            "com.hbm.machine.reactor.iter.torus.TorusEntity",
            "com.hbm.machine.reactor.pwr.PwrControllerBlockEntity",
            "com.hbm.machine.reactor.rbmk.autoloader.RbmkAutoloaderEntity",
            "com.hbm.machine.reactor.rbmk.boiler.RbmkBoilerEntity",
            "com.hbm.machine.reactor.rbmk.channel.RbmkFuelChannelEntity",
            "com.hbm.machine.reactor.rbmk.control.RbmkControlEntity",
            "com.hbm.machine.reactor.rbmk.irradiation.RbmkOutgasserEntity",
            "com.hbm.machine.reactor.rbmk.storage.RbmkStorageEntity",
            "com.hbm.machine.reactor.rbmk.thermal.RbmkHeaterEntity",
            "com.hbm.machine.reactor.watz.WatzEntity",
            "com.hbm.machine.reactor.zirnox.ZirnoxBlockEntity",
            "com.hbm.machine.refinery.RefineryBlockEntity",
            "com.hbm.machine.silex.SilexBlockEntity",
            "com.hbm.machine.soldering.SolderingBlockEntity",
            "com.hbm.machine.storagedrum.StorageDrumEntity",
            "com.hbm.machine.strandcaster.StrandCasterEntity",
            "com.hbm.machine.supercomputer.SuperComputerEntity",
            "com.hbm.machine.turret.DamagedSentryEntity",
            "com.hbm.machine.turret.SentryEntity",
            "com.hbm.machine.turret.greg.GregTurretEntity",
            "com.hbm.machine.turret.howard.HowardTurretEntity",
            "com.hbm.machine.turret.jeremy.JeremyTurretEntity",
            "com.hbm.machine.turret.rotary.RotaryTurretEntity",
            "com.hbm.machine.vacuumdistill.VacuumDistillBlockEntity",
            "com.hbm.machine.woodburner.WoodBurnerBlockEntity",
            "com.hbm.missile.launchpad.large.LargeLaunchPadBlockEntity",
            "com.hbm.missile.launchpad.rusted.RustedLaunchPadBlockEntity",
            "com.hbm.missile.launchpad.standard.StandardLaunchPadBlockEntity",
            "com.hbm.network.cargo.DroneCrateEntity",
            "com.hbm.network.cargo.request.DroneDockEntity",
            "com.hbm.network.cargo.request.DroneProviderEntity",
            "com.hbm.network.cargo.request.DroneRequesterEntity",
            "com.hbm.network.rtty.counter.RadioCounterEntity",
            "com.hbm.satellite.cargo.SatelliteDockBlockEntity",
            "com.hbm.satellite.control.SatelliteLinkerBlockEntity",
            "com.hbm.world.structure.cabinet.FilingCabinetBlockEntity",
            "com.hbm.world.structure.templates.funnel.FunnelBlockEntity",
            "com.hbm.world.structure.templates.microwave.MicrowaveBlockEntity"
    );
    private static boolean blockPositionOnly(MenuProvider provider){return BLOCK_POSITION_ONLY.contains(provider.getClass().getName());}
    public static boolean knownProvider(MenuProvider provider){return provider!=null&&(blockPositionOnly(provider)||provider.getClass()==PneumoTubeBlockEntity.class||provider.getClass()==OilDrillEntity.class);}
    public static Consumer<RegistryFriendlyByteBuf> complete(MenuProvider provider,Consumer<RegistryFriendlyByteBuf> explicitWriter){
        if(explicitWriter!=null||!(provider instanceof BlockEntity machine)||!knownProvider(provider))return explicitWriter;
        return buffer->{
            // ServerPlayer has already invoked provider.writeClientSideData by this point.
            // Any real provider payload is complete and must never be prefixed/duplicated.
            if(buffer.writerIndex()!=0)return;
            buffer.writeBlockPos(machine.getBlockPos());
            if(provider.getClass()==PneumoTubeBlockEntity.class){var tube=(PneumoTubeBlockEntity)provider;buffer.writeBoolean(tube.isEndpoint()&&!tube.isCompressor());}
            else if(provider.getClass()==OilDrillEntity.class)buffer.writeUtf(((OilDrillEntity)provider).kind().name());
        };
    }
    /** The vanilla spectator route bypasses the block's one-time source lockpick authentication. */
    public static boolean deniesSpectator(ServerPlayer player,MenuProvider provider){return player.isSpectator()&&knownProvider(provider)&&provider instanceof LockableBlockEntity locked&&locked.isLocked()&&!locked.hasMatchingKey(player);}
    private SourceMenuOpeningData(){}
}
