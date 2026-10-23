package com.hbm.world.structure.meteor;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

/** Blocks that the original exporter deliberately did not rotate need their original metadata. */
public final class MeteorSourceStateProcessor extends StructureProcessor {
    public static final MeteorSourceStateProcessor INSTANCE=new MeteorSourceStateProcessor();
    public static final MapCodec<MeteorSourceStateProcessor> CODEC=MapCodec.unit(INSTANCE);
    @Override public StructureBlockInfo processBlock(LevelReader level,BlockPos origin,BlockPos pivot,StructureBlockInfo original,StructureBlockInfo transformed,StructurePlaceSettings settings){
        if(original.nbt()!=null&&original.nbt().contains("hbm_loot_replacement"))return transformed;
        if(original.nbt()!=null&&original.nbt().contains(com.hbm.world.structure.templates.TemplateSourceStateProcessor.SOURCE))
            return com.hbm.world.structure.templates.TemplateSourceStateProcessor.INSTANCE.processBlock(level,origin,pivot,original,transformed,settings);
        //Old BlockChest does not extend any transformable branch in NBTStructure. The loot
        //wand instead rotates through onBlockPlacedBy, so that case is explicitly marked.
        if(original.state().getBlock() instanceof ChestBlock&&(original.nbt()==null||!original.nbt().getBoolean("hbm_rotating_loot"))){
            var inverse=switch(settings.getRotation()){case NONE->net.minecraft.world.level.block.Rotation.NONE;case CLOCKWISE_90->net.minecraft.world.level.block.Rotation.COUNTERCLOCKWISE_90;case CLOCKWISE_180->net.minecraft.world.level.block.Rotation.CLOCKWISE_180;case COUNTERCLOCKWISE_90->net.minecraft.world.level.block.Rotation.CLOCKWISE_90;};
            var facing=settings.getMirror().mirror(inverse.rotate(original.state().getValue(ChestBlock.FACING)));
            return new StructureBlockInfo(transformed.pos(),transformed.state().setValue(ChestBlock.FACING,facing),transformed.nbt());
        }
        return transformed;
    }
    @Override protected StructureProcessorType<?> getType(){return MeteorProcessors.SOURCE_STATES.get();}
    private MeteorSourceStateProcessor(){}
}
