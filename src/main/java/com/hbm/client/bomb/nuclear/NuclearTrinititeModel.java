package com.hbm.client.bomb.nuclear;

import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

/** Source getIcon's coordinate hash; vanilla weighted block models use a different axis order. */
final class NuclearTrinititeModel extends BakedModelWrapper<BakedModel> {
 private static final ModelProperty<Integer> VARIANT=new ModelProperty<>();
 private final BakedModel[] variants;
 NuclearTrinititeModel(BakedModel original,BakedModel[] variants){super(original);this.variants=variants;}
 @Override public ModelData getModelData(BlockAndTintGetter level,BlockPos pos,BlockState state,ModelData data){long l=(long)(pos.getX()*3129871)^(long)pos.getY()*116129781L^(long)pos.getZ();l=l*l*42317861L+l*11L;return data.derive().with(VARIANT,(int)(l>>16&3L)).build();}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType renderType){int variant=data.has(VARIANT)?data.get(VARIANT):0;return variants[variant].getQuads(state,side,random,data,renderType);}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return variants[0].getQuads(state,side,random);}
}
