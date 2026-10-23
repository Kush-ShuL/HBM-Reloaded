package com.hbm.client.sellafield;
import com.hbm.block.ore.SellafieldModel;
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
/** Reuses the already source-correct coordinate hash, preserving overflow of x*3129871 before its long cast. */
final class ActiveSellafieldModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<Integer> VARIANT=new ModelProperty<>();
    private final BakedModel[] variants;
    ActiveSellafieldModel(BakedModel original,BakedModel[] variants){super(original);this.variants=variants.clone();}
    private BakedModel model(ModelData data){Integer i=data.get(VARIANT);return variants[i==null?0:i];}
    @Override public ModelData getModelData(BlockAndTintGetter level,BlockPos pos,BlockState state,ModelData data){return data.derive().with(VARIANT,SellafieldModel.host(pos)).build();}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){return model(data).getQuads(state,side,random,data,layer);}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return variants[0].getQuads(state,side,random);}
    @Override public net.minecraft.client.renderer.texture.TextureAtlasSprite getParticleIcon(ModelData data){return model(data).getParticleIcon(data);}
}
