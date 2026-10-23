package com.hbm.client.constructionwand;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;

/** Source RenderBlockWand renders the exact inset box once from inside and once normally. */
public final class WandAirModel extends BakedModelWrapper<BakedModel> {
    public WandAirModel(BakedModel original){super(original);}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){
        var original=originalModel.getQuads(state,side,random,data,layer);var result=new ArrayList<BakedQuad>(original.size()*2);
        for(var quad:original){
            int[] from=quad.getVertices(),to=new int[from.length];int stride=from.length/4;
            for(int vertex=0;vertex<4;vertex++)System.arraycopy(from,(3-vertex)*stride,to,vertex*stride,stride);
            float minU=Float.POSITIVE_INFINITY,maxU=Float.NEGATIVE_INFINITY;
            for(int vertex=0;vertex<4;vertex++){float u=Float.intBitsToFloat(from[vertex*stride+4]);minU=Math.min(minU,u);maxU=Math.max(maxU,u);}
            for(int vertex=0;vertex<4;vertex++)to[vertex*stride+4]=Float.floatToRawIntBits(minU+maxU-Float.intBitsToFloat(to[vertex*stride+4]));
            // renderFromInside swaps the face positions, preserving the source side's brightness while mirroring U.
            result.add(new BakedQuad(to,quad.getTintIndex(),quad.getDirection(),quad.getSprite(),quad.isShade(),quad.hasAmbientOcclusion()));result.add(quad);
        }
        return result;
    }
}
