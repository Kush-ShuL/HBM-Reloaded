package com.hbm.block.sourceslab;

import com.hbm.block.masonry.LegacyMultiSlabBlock;
import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.world.structure.templates.materials.TemplateMaterials;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Source BlockMultiSlabMeta maps material0/1 to LightstoneType.TILE1/BRICKS2. */
public final class SourceStoneSlabBlock extends LegacyMultiSlabBlock {
    private final MapCodec<SourceStoneSlabBlock> sourceCodec;
    public SourceStoneSlabBlock(boolean doubled, Supplier<LegacyMultiSlabBlock> single, Supplier<LegacyMultiSlabBlock> full) {
        this(Properties.of().strength(2, 9).requiresCorrectToolForDrops().dynamicShape(), doubled, single, full);
    }
    private SourceStoneSlabBlock(Properties properties, boolean doubled, Supplier<LegacyMultiSlabBlock> single, Supplier<LegacyMultiSlabBlock> full) {
        super(properties, doubled, List.of("lightstone", "lightstone"), single, full);
        sourceCodec = simpleCodec(propertiesIn -> new SourceStoneSlabBlock(propertiesIn, doubled, single, full));
    }
    @Override public MapCodec<? extends SlabBlock> codec() { return sourceCodec; }
    @Override protected BlockState parent(BlockState state) {
        return TemplateMaterials.LIGHTSTONE.get().defaultBlockState().setValue(LegacyOreBlock.LEGACY_META, material(state.getValue(MATERIAL)) + 1);
    }
}
