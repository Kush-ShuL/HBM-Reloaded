package com.hbm.world.structure.logic;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

/** Native source placement's INBTTileEntityTransformable callback; source wand does not rotate its own metadata/NBT. */
public final class LogicStructureProcessor extends StructureProcessor {
    public static final LogicStructureProcessor INSTANCE = new LogicStructureProcessor();
    public static final MapCodec<LogicStructureProcessor> CODEC = MapCodec.unit(INSTANCE);
    @Override public StructureBlockInfo processBlock(LevelReader level,BlockPos origin,BlockPos pivot,StructureBlockInfo original,StructureBlockInfo transformed,StructurePlaceSettings settings) {
        if (!(transformed.state().getBlock() instanceof WandLogicBlock) || original.nbt() == null
                || !original.nbt().contains("hbm_legacy_state") || transformed.nbt() == null) return transformed;
        var tag = transformed.nbt().copy();
        tag.putBoolean("hbm_wand_transform",true);
        return new StructureBlockInfo(transformed.pos(),transformed.state(),tag);
    }
    @Override protected StructureProcessorType<?> getType() { return WandLogicContent.SOURCE_TRANSFORM.get(); }
    private LogicStructureProcessor() {}
}
