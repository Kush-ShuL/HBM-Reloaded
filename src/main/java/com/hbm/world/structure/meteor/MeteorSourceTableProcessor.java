package com.hbm.world.structure.meteor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

/** Original Component selectors make one world.rand.nextFloat per matching
 * source cell. Match the source identity, before loot-marker replacement. */
public final class MeteorSourceTableProcessor extends StructureProcessor {
    public static final MapCodec<MeteorSourceTableProcessor> CODEC=RecordCodecBuilder.mapCodec(instance->instance.group(
        com.mojang.serialization.Codec.STRING.fieldOf("table").forGetter(value->value.table)).apply(instance,MeteorSourceTableProcessor::new));
    private final String table;
    public MeteorSourceTableProcessor(String table){
        if(!table.equals("bricks")&&!table.equals("crates")&&!table.equals("ooze"))throw new IllegalArgumentException("Unknown original Meteor selector "+table);
        this.table=table;
    }
    @Override protected StructureProcessorType<?> getType(){return MeteorProcessors.TABLE.get();}
    @Override public StructureBlockInfo processBlock(LevelReader level,BlockPos origin,BlockPos pivot,StructureBlockInfo original,StructureBlockInfo current,StructurePlaceSettings settings){
        if(original.nbt()==null||original.nbt().contains("hbm_loot_replacement")||!original.nbt().contains("hbm_legacy_state"))return current;
        if(settings.getBoundingBox()!=null&&!settings.getBoundingBox().isInside(current.pos()))return current;
        String source=original.nbt().getCompound("hbm_legacy_state").getString("name").replace("hbm:tile.","hbm:");
        boolean brick=source.equals("hbm:meteor_brick"),crate=table.equals("crates")&&source.equals("hbm:crate"),crab=table.equals("crates")&&source.equals("hbm:meteor_spawner"),ooze=table.equals("ooze")&&source.equals("hbm:concrete_colored");
        if(!brick&&!crate&&!crab&&!ooze)return current;
        if(!(level instanceof ServerLevelAccessor))throw new IllegalStateException("Meteor selector requires the actual source placement context");
        var random=level instanceof net.minecraft.server.level.WorldGenRegion region?region.getRandom():settings.getRandom(current.pos());
        float chance=random.nextFloat();
        String target=brick?(chance<.4F?"hbm:meteor_brick":chance<.7F?"hbm:meteor_brick_mossy":"hbm:meteor_brick_cracked")
            :crate?(chance<.6F?"minecraft:air":chance<.8F?"hbm:crate_ammo":chance<.9F?"hbm:crate_can":"hbm:crate")
            :crab?(chance<.8F?"hbm:meteor_spawner_hidden":"hbm:meteor_spawner")
            :chance<.8F?"hbm:toxic_block":"hbm:meteor_polished";
        var id=ResourceLocation.parse(target);
        if(!BuiltInRegistries.BLOCK.containsKey(id))throw new IllegalStateException("Missing actual Meteor selector block "+id);
        return new StructureBlockInfo(current.pos(),BuiltInRegistries.BLOCK.get(id).defaultBlockState(),current.nbt());
    }
}
