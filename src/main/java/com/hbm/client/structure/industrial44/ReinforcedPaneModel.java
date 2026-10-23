package com.hbm.client.structure.industrial44;
import com.hbm.world.structure.dependencies.industrial44.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;
import net.neoforged.neoforge.common.util.TriState;
/** Source render-type18 uses two thin central sheets and explicit rim strips, not a modern solid glass-pane post. */
public final class ReinforcedPaneModel extends BakedModelWrapper<BakedModel> {
 private static final ModelProperty<Integer> KEY=new ModelProperty<>();private final TextureAtlasSprite face,rim;private final RenderType layer;private final Map<Integer,List<BakedQuad>> cache=new ConcurrentHashMap<>();
 public ReinforcedPaneModel(BakedModel model,Function<Material,TextureAtlasSprite> sprites){this(model,sprites,ResourceLocation.parse("hbm:block/reinforced_glass_pane"),ResourceLocation.parse("hbm:block/reinforced_glass_pane_edge"),RenderType.cutout());}
 public ReinforcedPaneModel(BakedModel model,Function<Material,TextureAtlasSprite> sprites,ResourceLocation faceId,ResourceLocation rimId,RenderType layer){super(model);this.layer=layer;face=sprites.apply(new Material(TextureAtlas.LOCATION_BLOCKS,faceId));rim=sprites.apply(new Material(TextureAtlas.LOCATION_BLOCKS,rimId));}
 @Override public boolean useAmbientOcclusion(){return false;}@Override public TriState useAmbientOcclusion(BlockState state,ModelData data,RenderType layer){return TriState.FALSE;}@Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(layer);}
 @Override public ModelData getModelData(BlockAndTintGetter world,BlockPos pos,BlockState state,ModelData data){ReinforcedPaneBlock block=(ReinforcedPaneBlock)state.getBlock();int key=0;Direction[] sides={Direction.NORTH,Direction.SOUTH,Direction.WEST,Direction.EAST};for(int i=0;i<4;i++)if(block.connects(world,pos.relative(sides[i]),sides[i]))key|=1<<i;
  if(visible(world,pos.above(),block))key|=1<<4;if(visible(world,pos.below(),block))key|=1<<5;Direction[] air={Direction.WEST,Direction.EAST,Direction.NORTH,Direction.SOUTH};for(int i=0;i<4;i++){if(world.getBlockState(pos.above().relative(air[i])).isAir())key|=1<<(6+i);if(world.getBlockState(pos.below().relative(air[i])).isAir())key|=1<<(10+i);}if(pos.getY()<world.getMaxBuildHeight()-1)key|=1<<14;if(pos.getY()>world.getMinBuildHeight()+1)key|=1<<15;return data.derive().with(KEY,key).build();}
 private static boolean visible(BlockAndTintGetter world,BlockPos pos,ReinforcedPaneBlock self){var state=world.getBlockState(pos);return !state.is(self)&&!state.isSolidRender(world,pos);}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){if(side!=null)return List.of();Integer value=data.get(KEY);int key=value==null?((state==null?0:ReinforcedPaneBlock.mask(state))|0xFFF0):value;return cache.computeIfAbsent(key,k->LegacyPaneQuads.bake(k,face,rim));}
}
