package com.hbm.client.glyphidhive;
import com.hbm.world.glyphidhive.*;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;

/** The source coordinate hash chooses the same original icon on all six faces, without consuming model RNG. */
final class GlyphidHiveMaterialModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<Integer> ICON=new ModelProperty<>();
    private final BakedModel[][] cubes;
    private final int metadata;
    GlyphidHiveMaterialModel(BakedModel original,BakedModel[][] cubes,int metadata){super(original);this.cubes=cubes;this.metadata=metadata;}
    static int coordinateIcon(BlockPos pos){
        // The X multiplication is deliberately int-overflowing BEFORE the long cast, as in source.
        long value=(long)(pos.getX()*3129871)^(long)pos.getY()*116129781L^(long)pos.getZ();
        value=value*value*42317861L+value*11L;int index=(int)(value>>16&3L);return Math.abs(index)%2;
    }
    private BakedModel cube(int icon){return cubes[GlyphidHiveMaterialBlock.family(metadata)][icon];}
    @Override public ModelData getModelData(BlockAndTintGetter level,BlockPos pos,BlockState state,ModelData data){return data.derive().with(ICON,coordinateIcon(pos)).build();}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return cube(metadata%2).getQuads(state,side,random);}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){var icon=data.get(ICON);return cube(icon==null?metadata%2:icon).getQuads(state,side,random,data,layer);}
    @Override public TextureAtlasSprite getParticleIcon(ModelData data){var icon=data.get(ICON);return cube(icon==null?metadata%2:icon).getParticleIcon(data);}
}
