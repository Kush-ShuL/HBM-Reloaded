package com.hbm.world.structure;

import com.hbm.block.masonry.LegacyMultiSlabBlock;
import com.hbm.block.scaffold.SteelScaffoldBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;

/** Source metadata is decoded before clockwise rotation; no missing block becomes AIR. */
public final class LegacyStructureStates {
    public static BlockState resolve(String source,int metadata,int turns){
        String actualName=source.replace("hbm:tile.","hbm:");
        var actualId=ResourceLocation.parse(actualName);
        if(BuiltInRegistries.BLOCK.containsKey(actualId)){
            Block actual=BuiltInRegistries.BLOCK.get(actualId);
            if(actual instanceof com.hbm.block.junglemechanics.JungleMetadataBlock jungle)return jungle.fromLegacy(metadata);
            var sourceCodec=SourceProceduralBlockCodecs.resolve(actualName,actual,metadata,turns);
            if(sourceCodec!=null)return sourceCodec;
            // Source BlockStorageCrate is not INBTBlockTransformable: raw facing was already chosen by Component.getDecoMeta.
            if(actual instanceof com.hbm.machine.storage.StorageCrateBlock crate)return crate.fromLegacy(metadata);
            // Procedural metadata is independent of any particular template's companion-door/hinge context.
            if(actual instanceof com.hbm.block.oilplant.OilFlowerBlock){
                if(metadata<0||metadata>=com.hbm.block.oilplant.OilFlowerBlock.NAMES.size())throw new IllegalStateException("Invalid source flower metadata "+source+":"+metadata);
                return actual.defaultBlockState().setValue(com.hbm.block.oilplant.OilFlowerBlock.META,metadata);
            }
            if(actual instanceof com.hbm.block.door.SourceDoorBlock){
                var sourceDoor=com.hbm.world.structure.vanilla.SourceVanillaStates.resolve("minecraft:wooden_door",metadata,turns);
                return actual.defaultBlockState().setValue(DoorBlock.FACING,sourceDoor.getValue(DoorBlock.FACING))
                    .setValue(DoorBlock.HALF,sourceDoor.getValue(DoorBlock.HALF)).setValue(DoorBlock.OPEN,sourceDoor.getValue(DoorBlock.OPEN))
                    .setValue(DoorBlock.HINGE,sourceDoor.getValue(DoorBlock.HINGE)).setValue(DoorBlock.POWERED,sourceDoor.getValue(DoorBlock.POWERED));
            }
        }
        if(com.hbm.world.structure.templates.TemplateStateMappings.hasMapping(source,metadata))
            return com.hbm.world.structure.templates.TemplateStateMappings.resolve(source,metadata,turns);
        if(com.hbm.world.structure.vanilla.SourceVanillaStates.hasSource(source))
            return com.hbm.world.structure.vanilla.SourceVanillaStates.resolve(source,metadata,turns);
        String name=source.replace("hbm:tile.","hbm:");
        //Flatten the old colour ID before registry lookup; colour is no longer world metadata.
        if(name.equals("minecraft:stained_glass")){name="minecraft:"+net.minecraft.world.item.DyeColor.byId(metadata&15).getName()+"_stained_glass";metadata=0;}
        var id=ResourceLocation.parse(name);
        if(!BuiltInRegistries.BLOCK.containsKey(id))throw new IllegalStateException("Missing actual structure block "+source+":"+metadata);
        Block block=BuiltInRegistries.BLOCK.get(id);BlockState state=block.defaultBlockState();
        //Source BlockScaffold is not INBTBlockTransformable or a vanilla rotatable class.
        //Its raw orientation stays unchanged even when the structure coordinates rotate.
        if(block instanceof SteelScaffoldBlock scaffold)return scaffold.fromLegacy(metadata);
        if(block instanceof com.hbm.block.crt.DecoCRTBlock crt)return crt.fromLegacy(com.hbm.block.crt.DecoCRTBlock.transformMeta(metadata,turns));
        if(block instanceof com.hbm.world.structure.templates.oldboiler.OldBoilerBlock)return state.setValue(com.hbm.world.structure.templates.oldboiler.OldBoilerBlock.META,metadata);
        if(block instanceof com.hbm.world.structure.templates.microwave.MicrowaveBlock)return state.setValue(com.hbm.world.structure.templates.microwave.MicrowaveBlock.META,rotateSourceFacing(metadata,turns));
        if(block instanceof com.hbm.world.structure.templates.heatboiler.HeatBoilerBlock||block instanceof com.hbm.missile.launchpad.rusted.RustedLaunchPadBlock){
            int offset=metadata>=12?10:metadata>=6?6:0;
            return state.setValue(com.hbm.multiblock.DummyableBlock.LEGACY_META,offset+rotateSourceFacing(metadata-offset,turns));
        }
        if(block instanceof LegacyMultiSlabBlock slab)state=slab.fromLegacy(metadata);
        else if(block instanceof StairBlock){
            Direction facing=switch(metadata&3){case 0->Direction.EAST;case 1->Direction.WEST;case 2->Direction.SOUTH;default->Direction.NORTH;};
            state=state.setValue(StairBlock.FACING,facing).setValue(StairBlock.HALF,(metadata&4)==0?Half.BOTTOM:Half.TOP).setValue(StairBlock.SHAPE,StairsShape.STRAIGHT);
        }else if(state.hasProperty(BlockStateProperties.AXIS)){
            state=state.setValue(BlockStateProperties.AXIS,(metadata&12)==4?Direction.Axis.X:(metadata&12)==8?Direction.Axis.Z:Direction.Axis.Y);
        }else if(metadata!=0)throw new IllegalStateException("Unported source structure metadata "+source+":"+metadata);
        Rotation rotation=switch(turns&3){case 1->Rotation.CLOCKWISE_90;case 2->Rotation.CLOCKWISE_180;case 3->Rotation.COUNTERCLOCKWISE_90;default->Rotation.NONE;};
        return state.rotate(rotation);
    }
    /** Resolve every finite raw metadata value actually emitted by the source procedures, before any placement begins. */
    public static void requireProceduralStates(String source){
        for(int metadata:ProceduralStateUses.metadata(source))for(int turns=0;turns<4;turns++)resolve(source,metadata,turns);
    }
    private static int rotateSourceFacing(int metadata,int turns){
        if(metadata<2||metadata>5)return metadata;
        Rotation rotation=switch(turns&3){case 1->Rotation.CLOCKWISE_90;case 2->Rotation.CLOCKWISE_180;case 3->Rotation.COUNTERCLOCKWISE_90;default->Rotation.NONE;};
        return rotation.rotate(Direction.from3DDataValue(metadata)).get3DDataValue();
    }
    private LegacyStructureStates(){}
}
