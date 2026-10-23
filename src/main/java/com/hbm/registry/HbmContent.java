package com.hbm.registry;

import com.google.gson.Gson;
import com.hbm.block.BasicBlock;
import com.hbm.block.LegacyHazardBlock;
import com.hbm.block.LegacyOutgassingBlock;
import com.hbm.block.fluid.HbmWorldFluids;
import com.hbm.item.BasicItem;
import com.hbm.item.CustomLoreItem;
import com.hbm.item.BasicBlockItem;
import com.hbm.block.cable.CableContent;
import com.hbm.block.gas.GasContent;
import com.hbm.block.pipe.PipeContent;
import com.hbm.item.fluid.FluidItemContent;
import com.hbm.item.autogen.AutogenContent;
import com.hbm.energy.HbmEnergy;
import com.hbm.LegacyStartup;
import com.hbm.machine.electricfurnace.ElectricFurnaceContent;
import com.hbm.machine.press.PressContent;
import com.hbm.machine.centrifuge.CentrifugeContent;
import com.hbm.geiger.GeigerContent;
import com.hbm.machine.shredder.ShredderContent;
import com.hbm.machine.batterysocket.BatterySocketContent;
import com.hbm.machine.alloy.AlloyContent;
import com.hbm.machine.converter.ConverterContent;
import com.hbm.machine.woodburner.WoodBurnerContent;
import com.hbm.machine.battery.BatteryMachineContent;
import com.hbm.block.pylon.PylonContent;
import com.hbm.item.settings.SettingsToolContent;
import com.hbm.machine.anvil.AnvilContent;
import com.hbm.armor.HazmatContent;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Source-backed simple registrations; specialized behavior is supplied by its real block/item class. */
public final class HbmContent {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "hbm");
    private static final Map<String, Supplier<? extends Item>> ITEM_LOOKUP = new LinkedHashMap<>();
    private static final Map<String, Supplier<? extends Block>> BLOCK_LOOKUP = new LinkedHashMap<>();
    private static final Map<String, List<Supplier<? extends Item>>> TAB_ITEMS = new LinkedHashMap<>();

    private HbmContent() {}

    public static void register(IEventBus bus) {
        for (String tab : HbmCreativeTabs.ORDER) {
            TAB_ITEMS.put(tab, new java.util.ArrayList<>());
        }
        HbmEnumItems.tabNames().forEach(tab -> TAB_ITEMS.computeIfAbsent(tab, ignored -> new java.util.ArrayList<>()));
        ContentSpec content;
        try (var stream = HbmContent.class.getResourceAsStream("/hbm/basic-content.json")) {
            if (stream == null) throw new IllegalStateException("Missing generated hbm/basic-content.json: run tools/port_basic_content.py");
            content = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), ContentSpec.class);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Cannot load basic content definitions", e);
        }
        for (var alias : content.aliases().entrySet()) {
            ResourceLocation target = ResourceLocation.parse(alias.getValue());
            ITEM_LOOKUP.put(alias.getKey(), () -> BuiltInRegistries.ITEM.get(target));
            if (BuiltInRegistries.BLOCK.containsKey(target)) BLOCK_LOOKUP.put(alias.getKey(), () -> BuiltInRegistries.BLOCK.get(target));
        }
        for (ItemSpec spec : content.items()) {
            var item = ITEMS.register(spec.id(), () -> "ItemCustomLore".equals(spec.itemClass())
                    ? new CustomLoreItem(itemProperties(spec), spec.container(), spec.id(), spec.effect())
                    : new BasicItem(itemProperties(spec), spec.container()));
            String tab = spec.creativePolaroid() == 0 || spec.creativePolaroid() == LegacyStartup.polaroidId ? spec.tab() : null;
            addItem(spec.id(), tab, item);
            ITEM_LOOKUP.put(spec.symbol(), item);
        }
        for (BlockSpec spec : content.blocks()) {
            var block = BLOCKS.register(spec.id(), () -> switch (spec.blockClass()) {
                case "BlockRotatablePillar" -> new RotatedPillarBlock(blockProperties(spec));
                case "BlockHazard", "BlockHotHazard", "BlockNuclearWaste" -> new LegacyHazardBlock(blockProperties(spec),
                        spec.displayEffect(), spec.blockClass().equals("BlockHotHazard"), spec.blockClass().equals("BlockNuclearWaste"));
                case "BlockOutgas" -> new LegacyOutgassingBlock(blockProperties(spec), spec.symbol(), spec.randomTick(),
                        spec.gasTickRate(), spec.breakGas(), spec.neighbourGas());
                default -> new BasicBlock(blockProperties(spec), spec.canSpawn());
            });
            BLOCK_LOOKUP.put(spec.id(), block);
            BLOCK_LOOKUP.put(spec.symbol(), block);
            var item = ITEMS.register(spec.id(), () -> new BasicBlockItem(block.get(),
                    spec.rarity() != null ? new Item.Properties().rarity(Rarity.valueOf(spec.rarity())) : new Item.Properties(),
                    spec.blockClass(), spec.id()));
            addItem(spec.id(), spec.tab(), item);
            ITEM_LOOKUP.put(spec.symbol(), item);
        }
        TAB_ITEMS.forEach((tab, items) -> TABS.register(tab, () -> createTab(tab, items)));
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TABS.register(bus);
        HbmSounds.register(bus);
        HbmEnergy.register(bus);
    }

    private static CreativeModeTab createTab(String tab, List<Supplier<? extends Item>> items) {
        var builder = CreativeModeTab.builder().title(Component.translatable("itemGroup.hbm." + tab))
                .icon(() -> HbmCreativeTabs.icon(tab))
                .displayItems((parameters, output) -> {
                    var stacks = new java.util.ArrayList<ItemStack>();
                    items.forEach(item -> stacks.add(new ItemStack(item.get())));
                    stacks.addAll(HbmCreativeTabs.vanillaReplacements(tab));
                    stacks.addAll(HbmEnumItems.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.ammo.FlatAmmoContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.PepperboxContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.specialvehicle.VehicleContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.pinkcloud.PinkCloudBroadcasterContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.energy.bag.AmmoBagContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.pistols.PistolContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.legendary.LegendaryGunsContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.kinetic.KineticContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.pedestal.PedestalContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.barbedwire.SpecialBarbedWireContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.sourceemitter.SourceEmittersContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.radiobox.RadioboxContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.door.sourceblast.SourceBlastDoorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.crt.DecoCRTContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.weapontable.WeaponTableContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.energy.EnergyContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.grenade40.Grenade40Content.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.rifle556.Rifle556Content.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.pwr.PwrContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.rifle762.Rifle762Content.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.rifle50.Rifle50Content.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.heavypowder.HeavyPowderContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.aberrator.AberratorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.accelerator.AcceleratorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.arcfurnace.ArcFurnaceContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.generator.steamengine.SteamEngineContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.fuelpool.FuelPoolContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.structure.dependencies.fusionhatch.FusionHatchContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.purex.PurexContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.structure.dependencies.materials.StructureMaterialsContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.structure.dependencies.hadron.HadronCoilContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.junglecrate.JungleCrateContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.worldgen.depthdeposit.DepthDeposits.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.pwrprinter.PwrPrinterContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.worldgen.dimensionores.DimensionOres.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.airintake.AirIntakeContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.pile.PileFuelContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.worldgen.orecave.OreCaves.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.pile.core.PileCoreContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.worldgen.bedrockresource.BedrockResources.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.dye.ChemicalDyeContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.pollution.PollutionContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.compressor.CompressorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.bedrockprocessing.BedrockProcessingContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.crystallizer.CrystallizerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.radiolysis.RadiolysisContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.material.crystallizer.CrystallizerMaterials.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.oreslopper.OreSlopperContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.excavator.ExcavatorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.rebar.RebarContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.radiationcleanup.RadiationCleanupContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.coolingtower.CoolingTowerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.bigasstank.BigAssTankContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.boltgun.EngineeringBoltgunContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.watz.fuel.WatzFuelContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.fuel.RbmkFuelContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.plasmaforge.PlasmaForgeContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.guidedisplay.GuideDisplayContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.structurewand.StructureWandContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.debugwand.DebugWandContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.constructionwand.ConstructionWandContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.sourceslab.SourceSlabsContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.artilleryremote.ArtilleryRemoteContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.archineer.ArchineerWrenchContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.meltdowntool.MeltdownToolContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.powernettool.PowerNetToolContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.refueler.RefuelerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.glitch.GlitchContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.rttycard.RttyCardContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.rttyhandheld.RttyHandheldContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.depthconstruction.DepthConstructionContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.cargoelevator.CargoElevatorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.disperser.DisperserContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.door.slidingblast.SlidingBlastDoorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.bottlecap.BottleCapContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.lambda.LambdaContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.bomb.fireworks.FireworksContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.door.fire.FireDoorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.lightstonestairs.LightstoneTileStairsContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.conveyorgrabber.ConveyorGrabberContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.conveyorpartitioner.ConveyorPartitionerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.conveyorextraction.CraneExtractionContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.fusionblanket.FusionBlanketContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.conveyorrouter.CraneRouterContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.conveyorsplitter.ConveyorSplitterContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.veganschnitzel.VeganSchnitzelContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.fertilizer.FertilizerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.conveyorpackaging.ConveyorPackagingContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.demonlamp.DemonLampContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.tritiumlamp.TritiumLampContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.luminousglass.LuminousGlassContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.laminatepane.LaminatePaneContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.armor.blackjack.BlackjackContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.plasticbag.PlasticBagContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.toolbox.ToolboxContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.marshmallow.MarshmallowContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.sourcedrinks.SourceDrinksContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.sourcesnacks.SourceSnacksContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.armor.shackles.ShacklesContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.armor.wd40.Wd40Content.creativeStacks(tab));
                    stacks.addAll(com.hbm.armor.charms.ArmorCharmsContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.armor.neutrinolens.NeutrinoLensContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.armor.nightvision.NightVisionContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.armor.gassensor.GasSensorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.armor.gasmaskmods.GasmaskAttachmentsContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.armor.cladding.ArmorCladdingContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.armor.pads.ArmorPadsContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.armor.servos.ArmorServosContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.armor.shields.ArmorShieldsContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.armor.mods.ArmorModsContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.specialfood.SpecialFoodContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.lemonfood.SourceLemonFoodContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.siphon.SiphonContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.casingbag.CasingBagContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.boronchemistry.BoronChemistryContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.pipette.PipetteContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.potatos.PotatosContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.containment.ContainmentBoxContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.dosimeter.DosimeterContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.demoncore.DemonCoreContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.sourceapple.SourceApplesContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.diagnostic.DiagnosticToolsContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.custommachine.CustomMachineContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.analysis.AnalysisToolContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.satellite.resources.ImpactTerrainContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.lpw2.Lpw2Content.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.soyuzlauncher.SoyuzLauncherContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.launchpadsoyuz.LaunchpadSoyuzContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.siren.SirenContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.tapedrive.TapeDriveContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.teleport.TeleportContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.mininglaser.MiningLaserContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.rotaryfurnace.RotaryFurnaceContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.turret.himars.HimarsTurretContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.thresher.ThresherContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.exposurechamber.ExposureChamberContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.turret.maxwell.MaxwellTurretContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.glyphidhive.GlyphidHiveContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.glyphidhive.GlyphidHiveMaterialContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.lantern.LanternContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.rockmill.RockMillContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.hephaestus.HephaestusContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.turbofan.TurbofanContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.surveyscanner.SurveyScannerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.oldlantern.OldLanternContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.equipment.jetpack.VectorJetpackContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.crucible.CrucibleContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.bobmazon.LegacyShopProducts74.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.bobmazondeps.BobmazonDependenciesContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.bobmazon.BobmazonContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.soyuz.SoyuzCapsulesContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.fireext.FireExtContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.utility.ChargeThrowerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.turret.tauon.TauonTurretContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.turret.richard.RichardTurretContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.fatman.AntimatterM42Content.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.missileassembly.MissileAssemblyContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.missile.custom.CustomMissileContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.missile.custom.launcher.CustomLauncherContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.electricpress.ElectricPressContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.orbus.OrbusContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.poweredcondenser.PoweredCondenserContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.solidifier.SolidifierContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.largeturbine.LargeTurbineContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.cyclotron.CyclotronContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.assemblyfactory.AssemblyFactoryContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.brickfurnace.BrickFurnaceContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.combinationfurnace.CombinationFurnaceContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.steelfurnace.SteelFurnaceContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.ironfurnace.IronFurnaceContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.rtgblast.RtgBlastContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.turret.fritz.FritzTurretContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.armor.rpa.RpaContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.woodprocessing.WoodProcessingContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.groundwater.GroundwaterPumpContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.precisionassembler.PrecisionAssemblerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.radgen.RadGenContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactorcontroller.ReactorControllerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.autocrafter.AutocrafterContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.recipedependencies.RecipeInputsContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.storagedrum.StorageDrumContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.hexafluoride.HexafluorideTankContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.capacitors.CapacitorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.nuclear.waste.IsotopicWasteContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.structurebasic.BasicStructureContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.deuterium.DeuteriumContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.electrolyser.ElectrolyserContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.junglemechanics.JungleMechanicsContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.drill.DrillContent.creativeStacks(tab));
                    if("weapon".equals(tab)) stacks.addAll(com.hbm.weapon.legacymelee.LegacyMeleeContent.weaponCreative());
                    stacks.addAll(com.hbm.weapon.sedna.gun.flamer.FlamerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.armor.robes.RobesContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.door.generic.GenericDoorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.door.family.GenericDoorFamilyContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.door.vault.VaultDoorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.door.transition.TransitionSealContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.door.sourceseal.SourceSealContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.railway.track.SourceRailContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.railway.train.RailTrainContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.entity.aircraft.bomber.BomberContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.defuser.DefuserContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.canteen.CanteenContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.structure.vending.VendingMachineContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.missilelauncher.MissileLauncherContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.fan.FanContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.sourcevents.SourceVentContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.sandbags.SandbagsContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.quadro.QuadroContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.weaponcrate.WeaponCrateContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.toaster.ToasterContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.stinger.StingerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.hevbattery.HevBatteryContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.armor.t51.T51Content.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.woodfixtures46.WoodFixturesContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.flaregun.FlaregunContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.panzerschreck.PanzerschreckContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.repairkits.RepairKitContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.structure.dependencies.materials45.StructuralMaterials45Content.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.structure.dependencies.industrial44.IndustrialFixturesContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.sedna.gun.chemthrower.ChemthrowerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.chemicalfactory.ChemicalFactoryContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.bat9000.Bat9000Content.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.pyrooven.PyroOvenContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.oilliquefactor.LiquefactorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.oilhydrotreater.HydrotreaterContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.entity.mob.ufo.UfoContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.oilreformer.ReformerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.oilcatalyticcracker.CatalyticCrackerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.entity.mob.hunterchopper.HunterChopperContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.entity.mob.botprime.BotPrimeContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.entity.mob.maskman.MaskManContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.entity.mob.duck.DuckContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.oilmixer.MixerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.coker.CokerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.light.reinforced.ReinforcedLampContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.turret.greg.GregTurretContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.light.floodlight.FloodlightContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.oilfracking.FrackingContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.light.SourceLightContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.turret.howard.HowardTurretContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.structure.cabinet.FilingCabinetContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.turret.jeremy.JeremyTurretContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.oilflare.FlareContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.fractiontower.FractionContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.turret.rotary.RotaryTurretContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.oilplant.OilBotanyContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.oilextraction.OilExtractionContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.artillery.ArtilleryAmmoContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.singularity.refinement.SingularityRefinementContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.structure.forest.ForestFixtureContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.turret.TurretContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.annihilator.AnnihilatorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.ammopress.AmmoPressContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.singularity.SourceSingularityContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.strandcaster.StrandCasterContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.drain.DrainContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.icf.IcfReactorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.satellite.link.SatelliteLinkContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.satellite.cargo.SatelliteCargoContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.dfc.components.DfcComponentsContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.dfc.DfcCoreContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.dfc.DfcMaterials.creativeStacks(tab));
                    stacks.addAll(com.hbm.network.separator.NetworkSeparatorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.satellite.control.SatelliteControlContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.satellite.SatelliteContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.radar.RadarContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.pa.cooled.PaContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.pa.beamline.PaBeamlineContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.network.cargo.request.CargoRequestContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.network.cargo.CargoDroneContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.designator.advanced.AdvancedDesignatorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.pa.parts.PaPartsContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.missile.launchpad.large.LargeLaunchPadContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.icf.pellet.IcfPelletPressContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.panels.terminal.RbmkTerminalContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.panels.indicator.RbmkIndicatorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.panels.lever.RbmkLeverContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.panels.graph.RbmkGraphContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.network.rtty.autocal.AutocalContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.missile.completion.MissileContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.network.rtty.telex.RadioTelexContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.panels.numitron.RbmkNumitronContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.panels.keypad.RbmkKeypadContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.reactorsensor.ReactorSensorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.panels.gauge.RbmkGaugeContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.network.rtty.ror.RadioRorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.network.rtty.counter.RadioCounterContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.network.rtty.logic.RadioLogicContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.arcwelder.ArcWelderContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.network.rtty.RadioTorchContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.genericpart.GenericPartContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.display.RbmkDisplayContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.gascentrifuge.GasCentrifugeContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.fel.FelContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.digamma.DigammaBlocks.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.silex.SilexContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.iter.breeder.FusionBreederContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.thermal.RbmkThermalContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.irradiation.RbmkOutgasserContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.iter.mhdt.MhdtContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.iter.utilities.FusionUtilityContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.supercomputer.SuperComputerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.soldering.SolderingContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.storage.RbmkStorageContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.crane.RbmkCraneContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.console.RbmkConsoleContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.muffler.MufflerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.generator.leviathan.LeviathanContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.autoloader.RbmkAutoloaderContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.meteor.MeteorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.meteor.MeteorImpactMaterials.creativeStacks(tab));
                    stacks.addAll(com.hbm.weapon.b92.B92Content.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.forcefield.ForceFieldContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.iter.auxiliary.FusionAuxiliaryContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.iter.torus.TorusContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.iter.klystron.KlystronContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.boiler.RbmkBoilerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.control.RbmkControlsContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.channel.RbmkFuelChannels.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.guidebook.GuideBookContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.column.RbmkColumnsContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.holotape.HolotapeContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.watz.WatzContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.residue.RbmkResidueContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.rbmk.glass.RbmkGlassContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.powerdetector.PowerDetectorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.generator.gasturbine.GasTurbineContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.pneumaticstorage.PneumaticStorageContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.pneumatic.tube.PneumaticTubeContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.generator.combustion.CombustionContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.generator.industrialmemorial.IndustrialMemorialContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.generator.stirling.StirlingContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.fensu.FensuContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.conveyor.belt.ConveyorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.solarboiler.SolarBoilerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.redroom.RedroomLootContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.blastfurnace.BlastFurnaceContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.structure.templates.microwave.MicrowaveContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.structure.templates.heatboiler.HeatBoilerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.pwr.component.PwrFuelPillarContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.missile.rusted.RustedMissileContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.missile.launchpad.rusted.RustedLaunchPadContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.survivalloot.SurvivalLootContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.structure.loot.StructureLootContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.structure.templates.materials.TemplateMaterials.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.door.SourceDoorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.rtg.RtgContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.bottle.BottleContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.structureloot.StructureLootEssentials.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.treasure.TreasureArmorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.structure.templates.radio.RadioReceiverContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.bobble.BobbleContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.structure.templates.funnel.FunnelContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.structure.templates.computer.ComputerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.charger.ChargerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.sellafield.SellafieldContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.designator.DesignatorContent.creativeStacks(tab));
                    stacks.addAll(HbmOres.creativeStacks(tab));
                    stacks.addAll(HbmDeepslateOres.creativeStacks(tab));
                    stacks.addAll(com.hbm.ore.RawOreContent.creativeStacks(tab));
                    stacks.addAll(HbmStairs.creativeStacks(tab));
                    stacks.addAll(HbmStamps.creativeStacks(tab));
                    stacks.addAll(FluidItemContent.creativeStacks(tab));
                    stacks.addAll(HbmWorldFluids.creativeStacks(tab));
                    stacks.addAll(PipeContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.network.fluidcontrols.FluidControlsContent.creativeStacks(tab));
                    stacks.addAll(GeigerContent.creativeStacks(tab));
                    stacks.addAll(ShredderContent.creativeItems(tab));
                    stacks.addAll(HbmFallingBlocks.creativeStacks(tab));
                    stacks.addAll(BatterySocketContent.creativeStacks(tab));
                    stacks.addAll(ConverterContent.creativeStacks(tab));
                    stacks.addAll(PylonContent.creativeStacks(tab));
                    stacks.addAll(SettingsToolContent.creativeStacks(tab));
                    stacks.addAll(AnvilContent.creativeStacks(tab));
                    stacks.addAll(HazmatContent.creativeItems(tab));
                    stacks.addAll(com.hbm.item.medicine.MedicineContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.bomb.conventional.ConventionalContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.bomb.fishing.FishingDynamiteContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.bomb.fissure.FissureBombContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.assembler.AssemblerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.chemicalplant.ChemicalPlantContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.bomb.nuclear.NuclearContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.explosion.ThermalContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.barrel.BarrelContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.fluidtank.IndustrialFluidTankContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.refinery.deco.DecoSteelContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.masonry.MasonryContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.polarizedglass.PolarizedGlassContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.phosphorvine.PhosphorVinesContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.bomb.taintbarrel.TaintBarrelContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.sourcestones.SourceStonesContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.lithiumstorage.LithiumStorageContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.cokestorage.CokeStorageContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.glowingasphalt.GlowingAsphaltContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.ReactorContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.reactor.zirnox.ZirnoxContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.foundry.FoundryContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.foundry.crucible.CrucibleContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.ashpit.AshpitContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.chimney.ChimneyContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.refinery.RefineryContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.vacuumdistill.VacuumDistillContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.tesla.TeslaContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.transformer.TransformerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.world.structure.materials.MeteorMaterialContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.entity.mob.crab.CyberCrabContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.meteordeco.MeteorDecorationContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.heatingoven.HeatingOvenContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.crate.CrateContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.electricheater.ElectricHeaterContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.heaterheatex.HeatexContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.meteorbook.tool.MeteorBookToolsContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.food.CrateFoodContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.crate.ConserveContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.oilburner.OilburnerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.block.scaffold.ScaffoldContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.industrialboiler.IndustrialBoilerContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.meteorunstable.MeteorUnstableContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.keyforge.KeyForgeContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.storage.StorageCrateContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.item.tool.ToolingContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.turbine.IndustrialTurbineContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.massstorage.MassStorageContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.turbine.quartz.QuartzGlassContent.creativeStacks(tab));
                    stacks.addAll(com.hbm.machine.generator.diesel.DieselContent.creativeStacks(tab));
                    if (tab.equals("parts")) stacks.addAll(AutogenContent.creativeStacks());
                    if (tab.equals("machine")) {
                        stacks.addAll(ElectricFurnaceContent.machineItems());
                        stacks.addAll(PressContent.creativeItems());
                        stacks.addAll(CentrifugeContent.creativeItems());
                        stacks.addAll(AlloyContent.creativeItems());
                        stacks.addAll(BatteryMachineContent.machineItems());
                        stacks.addAll(WoodBurnerContent.creativeItems());
                        stacks.addAll(CableContent.creativeStacks());
                        stacks.addAll(com.hbm.compat.computercraft.cable.PaintableComputerCableContent.creativeStacks(tab));
                        stacks.addAll(GasContent.creativeStacks());
                    }
                    if (tab.equals("control")) {
                        stacks.addAll(ElectricFurnaceContent.controlItems());
                        stacks.addAll(CentrifugeContent.controlItems());
                    }
                    HbmCreativeTabs.sort(stacks);
                    stacks.forEach(output::accept);
                });
        int index = HbmCreativeTabs.ORDER.indexOf(tab);
        // NeoForge names the listed neighbours: tabsBefore are BEFORE this tab.
        // Keep the original HBM order after vanilla's ten ordinary tabs (page2).
        if (index > 0) builder.withTabsBefore(ResourceLocation.fromNamespaceAndPath("hbm", HbmCreativeTabs.ORDER.get(index - 1)));
        else if (index == 0) builder.withTabsBefore(net.minecraft.world.item.CreativeModeTabs.SPAWN_EGGS);
        if (tab.equals("template")) builder.withSearchBar().backgroundTexture(ResourceLocation.fromNamespaceAndPath("hbm", "textures/gui/creative/tab_item_search.png"));
        if (tab.equals("nuke")) builder.backgroundTexture(ResourceLocation.withDefaultNamespace("textures/gui/container/creative_inventory/tab_nuke.png"));
        return builder.build();
    }

    private static void addItem(String id, String tab, Supplier<? extends Item> item) {
        if (ITEM_LOOKUP.putIfAbsent(id, item) != null) throw new IllegalStateException("Duplicate item " + id);
        if (tab != null) TAB_ITEMS.computeIfAbsent(tab, key -> new java.util.ArrayList<>()).add(item);
    }

    private static Item.Properties itemProperties(ItemSpec spec) {
        var properties = new Item.Properties().stacksTo(spec.stackSize());
        if (spec.maxDamage() > 0) properties.durability(spec.maxDamage());
        if (spec.fireResistant()) properties.fireResistant();
        if (spec.rarity() != null) properties.rarity(Rarity.valueOf(spec.rarity()));
        return properties;
    }

    private static BlockBehaviour.Properties blockProperties(BlockSpec spec) {
        var properties = BlockBehaviour.Properties.of()
                .mapColor(color(spec.material()))
                .destroyTime(spec.hardness())
                .explosionResistance(spec.explosionResistance())
                .sound(sound(spec.sound()))
                .friction(spec.friction())
                .lightLevel(state -> spec.light());
        if (spec.requiresTool()) properties.requiresCorrectToolForDrops();
        return properties;
    }

    private static SoundType sound(String sound) {
        return switch (sound) {
            case "metal", "anvil" -> SoundType.METAL;
            case "wood" -> SoundType.WOOD;
            case "sand" -> SoundType.SAND;
            case "gravel" -> SoundType.GRAVEL;
            case "grass" -> SoundType.GRASS;
            case "glass" -> SoundType.GLASS;
            case "snow" -> SoundType.SNOW;
            case "cloth" -> SoundType.WOOL;
            default -> SoundType.STONE;
        };
    }

    private static MapColor color(String material) {
        return switch (material) {
            case "iron" -> MapColor.METAL;
            case "wood" -> MapColor.WOOD;
            case "sand" -> MapColor.SAND;
            case "ground" -> MapColor.DIRT;
            case "grass" -> MapColor.GRASS;
            default -> MapColor.STONE;
        };
    }

    /** Resolve original field names, including vanilla replacements such as ingot_copper. */
    public static Item item(String symbol) {
        if (symbol.indexOf(':') >= 0) {
            ResourceLocation id = ResourceLocation.parse(symbol);
            if (BuiltInRegistries.ITEM.containsKey(id)) return BuiltInRegistries.ITEM.get(id);
            throw new IllegalArgumentException("Item is not registered: " + symbol);
        }
        var item = ITEM_LOOKUP.get(symbol);
        if (item == null) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("hbm", symbol);
            if (BuiltInRegistries.ITEM.containsKey(id)) return BuiltInRegistries.ITEM.get(id);
            throw new IllegalArgumentException("Item is not migrated: " + symbol);
        }
        return item.get();
    }

    public static Block block(String symbol) {
        var block = BLOCK_LOOKUP.get(symbol);
        if (block == null) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("hbm", symbol);
            if (BuiltInRegistries.BLOCK.containsKey(id)) return BuiltInRegistries.BLOCK.get(id);
            throw new IllegalArgumentException("Block is not migrated: " + symbol);
        }
        return block.get();
    }

    private record ContentSpec(List<ItemSpec> items, List<BlockSpec> blocks, Map<String, String> aliases) {}
    private record ItemSpec(String id, String symbol, String tab, int stackSize, int maxDamage, String container,
                            String rarity, boolean fireResistant, String itemClass, boolean effect, int creativePolaroid) {}
    private record BlockSpec(String id, String symbol, String tab, String material, String sound, float hardness,
                             float explosionResistance, float friction, int light, boolean requiresTool,
                             boolean canSpawn, String blockClass, String displayEffect, String rarity,
                             boolean randomTick, int gasTickRate, boolean breakGas, boolean neighbourGas) {}
}
