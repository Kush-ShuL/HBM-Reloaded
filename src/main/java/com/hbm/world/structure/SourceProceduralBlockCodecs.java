package com.hbm.world.structure;

import com.hbm.multiblock.DummyableBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

/** Only actual admitted source families. Source rotations are explicit; a legacy_meta name alone never selects a policy. */
public final class SourceProceduralBlockCodecs {
    public static BlockState resolve(String id,Block block,int metadata,int turns){
        int turn=turns&3;BlockState state=block.defaultBlockState();
        if(block instanceof com.hbm.block.ore.ResourceOreBlock)return state; // The physical mineral identity has no metadata or rotation state.
        if(block instanceof com.hbm.compat.computercraft.cable.PaintableComputerCableBlock)return state.setValue(com.hbm.compat.computercraft.cable.PaintableComputerCableBlock.META,metadata); // Source plain BlockContainer/raw16; no invented rotation.
        if(block instanceof com.hbm.block.devtesters.EventTesterBlock)return state; // Source tester ignores all metadata/no transform.
        if(block instanceof com.hbm.block.devtesters.ObjTesterBlock)return state.setValue(com.hbm.block.devtesters.ObjTesterBlock.META,metadata); // Placement stores yaw but active renderer ignores it; source no rotation interface.
        if(block instanceof com.hbm.block.sourceslab.SourcePinkSlabBlock pink)return pink.fromLegacy(metadata);
        if(block instanceof com.hbm.block.sourceslab.SourceStoneSlabBlock stone)return stone.fromLegacy(metadata);
        if(block instanceof com.hbm.door.sourceblast.SourceBlastDoorBlock)return state.setValue(com.hbm.door.sourceblast.SourceBlastDoorBlock.META,metadata); // Plain legacy BlastDoor has no INBT transform: preserve raw16.
        if(block instanceof com.hbm.door.sourceblast.SourceBlastDoorDummyBlock)return state; // Actual DummyBlockBlast ignores all metadata and has no source transform.
        if(block instanceof com.hbm.block.sourcecrystal.CrystalVirusBlock||block instanceof com.hbm.block.sourcecrystal.CrystalPulsarBlock)return state; // Source plain blocks ignore all metadata and have no INBTBlockTransformable.
        if(block instanceof com.hbm.machine.radiobox.RadioboxBlock)return state.setValue(com.hbm.machine.radiobox.RadioboxBlock.META,metadata); // Source Radiobox is not INBTBlockTransformable; preserve raw16 without structure rotation.
        if(block instanceof com.hbm.block.sourceemitter.EmitterBlock)return state.setValue(com.hbm.block.sourceemitter.EmitterBlock.META,metadata); // Plain source BlockEmitter has no INBTBlockTransformable: all16 raw states, no source rotation.
        if(block instanceof com.hbm.block.sourceemitter.PartEmitterBlock)return state; // Actual PartEmitter ignores every raw metadata value and has no source transform.
        if(block instanceof com.hbm.block.ore.LegacyOreBlock depth&&depth.spec().originalClass().equals("BlockDepth")&&com.hbm.block.depthconstruction.DepthConstructionContent.NAMES.contains(depth.spec().id()))return state; // These actual source building blocks ignore metadata/rotation; existing depth backend owns the native default state.
        if(block instanceof com.hbm.block.bottlecap.BottleCapBlock)return state.setValue(com.hbm.block.ore.LegacyOreBlock.LEGACY_META,metadata); // Original BlockCap preserves raw16; enum icons/drops rectify locally, no source rotation interface.
        if(id.equals("hbm:lightstone_tile_stairs")&&block instanceof StairBlock){var source=com.hbm.world.structure.vanilla.SourceVanillaStates.resolve("minecraft:stone_stairs",metadata,turns);return state.setValue(StairBlock.FACING,source.getValue(StairBlock.FACING)).setValue(StairBlock.HALF,source.getValue(StairBlock.HALF)).setValue(StairBlock.SHAPE,source.getValue(StairBlock.SHAPE)).setValue(StairBlock.WATERLOGGED,false);} // Same original BlockStairs source decoder/rotation; exact admitted TILE host only.
        if(block instanceof com.hbm.machine.pistoninserter.PistonInserterBlock)return state.setValue(com.hbm.machine.pistoninserter.PistonInserterBlock.META,metadata); // Actual hidden PistonInserter has no source structure transform; preserve raw16.
        if(block instanceof com.hbm.conveyor.crane.CraneBaseBlock)return state.setValue(com.hbm.conveyor.crane.CraneBaseBlock.META,metadata); // Actual BlockCraneBase is not INBTBlockTransformable; preserve raw16 without invented rotation.
        if(block instanceof com.hbm.block.demonlamp.DemonLampBlock)return state.setValue(com.hbm.block.demonlamp.DemonLampBlock.META,metadata); // Source DemonLamp has no INBTBlockTransformable; preserve all16 raw face metadata, without added structure rotation.
        if(block instanceof com.hbm.block.tritiumlamp.TritiumLampBlock)return state; // Actual source lamp ignores all raw metadata and source rotation.
        if(block instanceof com.hbm.block.luminousglass.LuminousGlassBlock)return state; // Actual source glass ignores metadata and source rotation.
        if(block instanceof com.hbm.world.structure.dependencies.industrial44.ReinforcedPaneBlock)return state.setValue(com.hbm.world.structure.dependencies.industrial44.ReinforcedPaneBlock.META,metadata); // Original panes keep raw metadata, no source rotation.
        if(block instanceof com.hbm.block.lithiumstorage.LithiumStorageBlock)return state; // Actual BlockLithium ignores metadata/rotation.
        if(block instanceof com.hbm.machine.sourcevents.SourceVentBlock)return state; // Both actual source vent classes ignore raw metadata/rotation.
        if(block instanceof com.hbm.block.guidedisplay.GuideDisplayBlock)return state.setValue(com.hbm.block.guidedisplay.GuideDisplayBlock.META,metadata); // Original Guide has no source structure rotation; preserve all16 raw face mappings.
        if(block instanceof com.hbm.machine.refueler.RefuelerBlock)return state.setValue(com.hbm.machine.refueler.RefuelerBlock.META,metadata); // Actual BlockContainer source has no INBTBlockTransformable.
        if(block instanceof com.hbm.machine.conveyorpartitioner.ConveyorPartitionerBlock)return state.setValue(com.hbm.machine.conveyorpartitioner.ConveyorPartitionerBlock.META,metadata); // Actual CranePartitioner ignores structure rotation, preserves raw16.
        if(block instanceof com.hbm.machine.conveyorrouter.CraneRouterBlock)return state; // Actual CraneRouter ignores all metadata and has no INBTBlockTransformable.
        if(block instanceof com.hbm.block.cokestorage.CokeStorageBlock)return state;
        if(block instanceof com.hbm.block.masonry.AsphaltBlock)return state; // Source BlockSpeedy ignores all metadata/rotation.
        if(block instanceof com.hbm.armor.neutrinolens.PinkLogBlock log)return log.fromLegacy(metadata).rotate(rotation(turn));
        if(block instanceof com.hbm.world.glyphidhive.GlyphidSpawnerBlock)return state.setValue(com.hbm.world.glyphidhive.GlyphidSpawnerBlock.META,metadata);
        if(block instanceof com.hbm.world.glyphidhive.GlyphidHiveMaterialBlock)return state.setValue(com.hbm.world.glyphidhive.GlyphidHiveMaterialBlock.META,metadata);
        if(block instanceof com.hbm.block.structurebasic.SourceVinylTileBlock)return state.setValue(com.hbm.block.ore.LegacyOreBlock.LEGACY_META,metadata);
        if(block instanceof com.hbm.block.structurebasic.SourceReedsBlock||block instanceof com.hbm.block.structurebasic.MarkedConcreteBlock)return state;
        if(block instanceof com.hbm.item.recipedependencies.ReinforcedLaminateBlock laminate)return laminate.fromLegacy(metadata);
        // Original CM components/controller/anchor have no INBTBlockTransformable; preserve all16 raw metadata values.
        if(block instanceof com.hbm.machine.custommachine.CMComponentBlock||block instanceof com.hbm.machine.custommachine.CMAnchorBlock||block instanceof com.hbm.machine.custommachine.CustomMachineBlock)return state.setValue(com.hbm.machine.custommachine.CMComponentBlock.META,metadata);
        // Original MachineSiren ignores all raw metadata and has no rotation interface.
        if(block instanceof com.hbm.machine.siren.SirenBlock)return state;
        // Both original teleport blocks ignore metadata and implement no INBTBlockTransformable rotation.
        if(block instanceof com.hbm.machine.teleport.TeleporterBlock||block instanceof com.hbm.machine.teleport.TeleanchorBlock)return state;
        if(block instanceof com.hbm.machine.storagedrum.StorageDrumBlock||block instanceof com.hbm.machine.autocrafter.AutocrafterBlock)return state;
        if(block instanceof com.hbm.machine.woodprocessing.AutosawBlock)return state;
        // Source MachineReactorControl is not INBTBlockTransformable: keep all16 raw metadata values.
        if(block instanceof com.hbm.machine.reactorcontroller.ReactorControllerBlock controller)return controller.fromLegacy(metadata);
        if(block instanceof com.hbm.machine.storage.StorageCrateBlock crate)return crate.fromLegacy(metadata);
        if(block instanceof com.hbm.machine.rtgblast.RtgBlastBlock)return state.setValue(com.hbm.machine.rtgblast.RtgBlastBlock.META,metadata);
        if(block instanceof com.hbm.machine.brickfurnace.BrickFurnaceBlock)return state.setValue(com.hbm.machine.brickfurnace.BrickFurnaceBlock.META,metadata);
        // Source MachineRtgFurnace is only BlockContainer; NBTStructure preserves all16 raw metadata values.
        if(block instanceof com.hbm.machine.rtgfurnace.RtgFurnaceBlock)return state.setValue(com.hbm.machine.rtgfurnace.RtgFurnaceBlock.META,metadata);
        if(block instanceof com.hbm.machine.missileassembly.MissileAssemblyBlock assembly)return assembly.fromLegacy(metadata);
        if(block instanceof com.hbm.missile.custom.launcher.CustomLauncherBlock launcher)return launcher.fromLegacy(metadata);
        if(block instanceof com.hbm.missile.custom.launcher.CustomLauncherCoreBlock)return state;
        if(block instanceof com.hbm.machine.soyuzlauncher.SoyuzStructureBlock)return state; // Source BlockSoyuzStruct ignores raw metadata.
        if(block instanceof com.hbm.machine.drain.OilSpillBlock)return state.setValue(com.hbm.machine.drain.OilSpillBlock.META,metadata&15);
        // FluidSwitch has no transform interface; deprecated FluidPump uses original transformMetaDeco.
        if(block instanceof com.hbm.network.fluidcontrols.FluidSwitchBlock)return state.setValue(com.hbm.block.pipe.NativePipeBlock.OPEN,metadata==1);
        if(block instanceof com.hbm.network.fluidcontrols.FluidPumpBlock)return state.setValue(com.hbm.network.fluidcontrols.FluidPumpBlock.META,deco(metadata,turn));
        if(block instanceof DummyableBlock){int offset=metadata>=12?10:metadata>=6?6:0;return state.setValue(DummyableBlock.LEGACY_META,offset+deco(metadata-offset,turn));}
        if(block instanceof com.hbm.machine.anvil.NtmAnvilBlock)return state.setValue(com.hbm.machine.anvil.NtmAnvilBlock.META,metadata);
        if(block instanceof com.hbm.world.structure.materials.ColoredConcreteBlock)return state.setValue(com.hbm.block.ore.LegacyOreBlock.LEGACY_META,metadata);
        if(block instanceof com.hbm.item.bobmazondeps.SnowglobeBlock)return state.setValue(com.hbm.item.bobmazondeps.TrinketBlock.META,metadata);
        if(block instanceof com.hbm.item.bobmazondeps.PlushieBlock)return state.setValue(com.hbm.item.bobmazondeps.TrinketBlock.META,(metadata+turn*4)%16);
        if(block instanceof com.hbm.block.bobble.BobbleBlock)return state.setValue(com.hbm.block.bobble.BobbleBlock.META,(metadata+turn*4)%16);
        if(block instanceof com.hbm.block.toaster.ToasterBlock toaster)return toaster.fromLegacy(com.hbm.block.toaster.ToasterBlock.transformMeta(metadata,turn));
        if(block instanceof com.hbm.world.structure.templates.computer.ComputerBlock){int rotation=metadata>>2;if(turn==1)rotation^=(rotation&3)<2?3:2;else if(turn==2)rotation^=1;else if(turn==3)rotation^=(rotation&3)<2?2:3;return state.setValue(com.hbm.world.structure.templates.computer.ComputerBlock.META,(rotation<<2)|(metadata&3));}
        // MachineFan and CableDiode do not implement INBTBlockTransformable in X5846.
        if(block instanceof com.hbm.machine.fan.FanBlock fan)return fan.fromLegacy(metadata);
        if(block instanceof com.hbm.block.cable.FacingCableBlock&&id.equals("hbm:cable_diode"))return state.setValue(com.hbm.block.cable.FacingCableBlock.FACING,sixDirection(metadata));
        if(block instanceof com.hbm.block.pipe.SpecialPipeBlock pipe&&pipe.kind()==com.hbm.block.pipe.PipeKind.GAUGE)return state.setValue(com.hbm.block.pipe.SpecialPipeBlock.FACING,sixDirection(deco(metadata,turn)));
        if(block instanceof com.hbm.block.pipe.decorative.DecorativePipeBlock)return state.setValue(com.hbm.block.pipe.decorative.DecorativePipeBlock.AXIS,(metadata&12)==4?Direction.Axis.X:(metadata&12)==8?Direction.Axis.Z:Direction.Axis.Y).rotate(rotation(turn));
        if(block instanceof com.hbm.block.masonry.LegacyLadderBlock ladder)return metadata==0?state:ladder.fromLegacy(deco(metadata,turn));
        if(block instanceof com.hbm.geiger.GeigerBlock)return metadata==0?state:state.setValue(com.hbm.geiger.GeigerBlock.FACING,horizontal(metadata));
        if(block instanceof com.hbm.machine.generator.diesel.DieselBlock)return metadata==0?state:state.setValue(com.hbm.machine.generator.diesel.DieselBlock.FACING,horizontal(deco(metadata,turn)));
        if(block instanceof com.hbm.network.rtty.RadioTorchBlock)return state.setValue(com.hbm.network.rtty.RadioTorchBlock.LEGACY_META,metadata);
        if(block instanceof com.hbm.block.sellafield.SellafieldBlock)return state.setValue(com.hbm.block.sellafield.SellafieldBlock.META,metadata);
        // Actual source BlockBeaconable/BlockBase ignores metadata in icon, geometry, drops and behavior.
        if(block instanceof com.hbm.door.sourceseal.SourceSealBlock)return state.setValue(com.hbm.door.sourceseal.SourceSealBlock.META,metadata); // Original BlockSeal has no source structure rotation, preserve raw16 orientation/redstone latch.
        if(block instanceof com.hbm.door.sourceseal.SourceSealHatchBlock||block instanceof com.hbm.block.BasicBlock&&id.equals("hbm:seal_frame"))return state; // Source BlockHatch/BlockGeneric frame ignore metadata.
        if(block instanceof com.hbm.block.forgotten.ForgottenBrickBlock)return state.setValue(com.hbm.block.ore.LegacyOreBlock.LEGACY_META,metadata); // Both actual BlockMulti subclasses preserve raw16 with no INBT source rotation; not directional red-room brick_red.
        if(block instanceof com.hbm.block.sourcestones.BiomeStoneBlock)return state.setValue(com.hbm.block.ore.LegacyOreBlock.LEGACY_META,metadata); // Source raw16 enum metadata, no INBT rotation.
        if(block instanceof com.hbm.block.sourcestones.PorousStoneBlock)return state; // Actual BlockStone subclass ignores source metadata.
        if(block instanceof com.hbm.bomb.taintbarrel.TaintBarrelBlock)return state; // Actual RedBarrel ignores raw metadata and source rotation; no unused TileEntityRedBarrel.
        if(block instanceof com.hbm.block.phosphorvine.PhosphorVineBlock)return state; // Source BlockHangingVine ignores all metadata and has no INBT transform.
        if(block instanceof com.hbm.block.polarizedglass.PolarizedGlassBlock)return state; // Actual source CT glass ignores metadata/no INBT rotation.
        if(block instanceof com.hbm.item.structuretandem.TandemBlock)return state.setValue(com.hbm.item.structuretandem.TandemBlock.FACING,sixDirection(deco(metadata,turn))); // Real INBT transformMetaDeco; source inputs only, no old-world importer.
        if(block instanceof com.hbm.item.structurelooteditor.LootEditorBlock)return state.setValue(com.hbm.item.structurelooteditor.LootEditorBlock.ROTATION,metadata>=0&&metadata<4?metadata:0); // Source has only a TE transform; raw uvRotate values outside1/2/3 render as0.
        if(block instanceof com.hbm.block.BasicBlock&&id.equals("hbm:block_lead"))return state;
        return null;
    }
    private static Direction sixDirection(int metadata){if(metadata<0||metadata>5)throw new IllegalArgumentException("Invalid original six-direction metadata "+metadata);return Direction.from3DDataValue(metadata);}
    private static Direction horizontal(int metadata){if(metadata<2||metadata>5)throw new IllegalArgumentException("Invalid original horizontal metadata "+metadata);return Direction.from3DDataValue(metadata);}
    private static int deco(int metadata,int turn){return metadata>=2&&metadata<=5?rotation(turn).rotate(Direction.from3DDataValue(metadata)).get3DDataValue():metadata;}
    private static Rotation rotation(int turn){return switch(turn&3){case 1->Rotation.CLOCKWISE_90;case 2->Rotation.CLOCKWISE_180;case 3->Rotation.COUNTERCLOCKWISE_90;default->Rotation.NONE;};}
    private SourceProceduralBlockCodecs(){}
}
