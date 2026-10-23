package com.hbm.client.rebar;
import com.hbm.block.rebar.RebarConfig;
import java.util.List;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
/** Native chunk-baked geometry; configuration switches exact source12/3-rod meshes, never a per-block entity mesh. */
final class RebarBakedModel extends BakedModelWrapper<BakedModel> {
 private final BakedModel simple;RebarBakedModel(BakedModel original,BakedModel simple){super(original);this.simple=simple;}
 private BakedModel selected(){return RebarConfig.simple()?simple:originalModel;}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return selected().getQuads(state,side,random);}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType type){return selected().getQuads(state,side,random,data,type);}
}
