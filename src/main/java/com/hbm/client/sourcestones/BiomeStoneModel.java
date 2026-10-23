package com.hbm.client.sourcestones;

import com.hbm.block.ore.LegacyOreBlock;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;

/** Source world getIcon compares the complete raw metadata above; item getIcon deliberately uses a different cube. */
final class BiomeStoneModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<Boolean> COVERED=new ModelProperty<>();
    private final BakedModel[] joined,exposed;
    BiomeStoneModel(BakedModel original,BakedModel[] joined,BakedModel[] exposed){super(original);this.joined=joined;this.exposed=exposed;}
    @Override public ModelData getModelData(BlockAndTintGetter level,BlockPos pos,BlockState state,ModelData data){var above=level.getBlockState(pos.above());boolean same=above.is(state.getBlock())&&above.getValue(LegacyOreBlock.LEGACY_META).equals(state.getValue(LegacyOreBlock.LEGACY_META));return data.derive().with(COVERED,same).build();}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType type){int meta=state==null?0:state.getValue(LegacyOreBlock.LEGACY_META)%2;return (Boolean.TRUE.equals(data.get(COVERED))?joined:exposed)[meta].getQuads(state,side,random,data,type);}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return exposed[state==null?0:state.getValue(LegacyOreBlock.LEGACY_META)%2].getQuads(state,side,random);}
}
