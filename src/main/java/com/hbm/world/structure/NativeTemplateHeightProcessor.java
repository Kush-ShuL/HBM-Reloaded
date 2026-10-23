package com.hbm.world.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

/** Snapshot each authored column before native placement mutates any of its blocks. */
public final class NativeTemplateHeightProcessor extends StructureProcessor {
    public static final MapCodec<NativeTemplateHeightProcessor> CODEC=RecordCodecBuilder.mapCodec(instance->instance.group(Codec.INT.fieldOf("origin_y").forGetter(value->value.originY),Codec.INT.fieldOf("height_offset").forGetter(value->value.offset),BoundingBox.CODEC.fieldOf("clip").forGetter(value->value.clip)).apply(instance,NativeTemplateHeightProcessor::new));
    private final int originY,offset;
    private final BoundingBox clip;
    private final Map<Long,Integer> columns=new HashMap<>();
    NativeTemplateHeightProcessor(int originY,int offset,BoundingBox clip){this.originY=originY;this.offset=offset;this.clip=clip;}
    @Override public StructureBlockInfo processBlock(LevelReader level,BlockPos origin,BlockPos pivot,StructureBlockInfo original,StructureBlockInfo transformed,StructurePlaceSettings settings){
        int x=transformed.pos().getX(),z=transformed.pos().getZ();
        if(x<clip.minX()||x>clip.maxX()||z<clip.minZ()||z>clip.maxZ())return null;
        int floor=columns.computeIfAbsent(BlockPos.asLong(x,0,z),key->NativeTemplatePiece.topSolid(level,x,z)+offset);
        var position=transformed.pos().offset(0,floor-originY,0);
        return level.isOutsideBuildHeight(position)?null:new StructureBlockInfo(position,transformed.state(),transformed.nbt());
    }
    @Override protected StructureProcessorType<?> getType(){return NativeTemplateStructures.HEIGHT.get();}
}
