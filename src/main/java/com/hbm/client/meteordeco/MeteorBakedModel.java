package com.hbm.client.meteordeco;
import com.hbm.block.meteordeco.*;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;
import net.neoforged.neoforge.common.util.TriState;
/** Baked source block faces; fence connections refresh from the world, with no ticking render entity. */
final class MeteorBakedModel extends BakedModelWrapper<BakedModel> {
 private static final ModelProperty<BlockState> CONNECTIONS=new ModelProperty<>();private final boolean inventory,corner;private final Function<Material,TextureAtlasSprite> textures;private final Map<BlockState,List<BakedQuad>> cache=new ConcurrentHashMap<>();private volatile List<BakedQuad> item;
 MeteorBakedModel(BakedModel original,boolean inventory,boolean corner,Function<Material,TextureAtlasSprite> textures){super(original);this.inventory=inventory;this.corner=corner;this.textures=textures;}
 @Override public boolean useAmbientOcclusion(){return !inventory;}@Override public TriState useAmbientOcclusion(BlockState state,ModelData data,RenderType type){return inventory?TriState.FALSE:TriState.DEFAULT;}@Override public boolean isGui3d(){return true;}@Override public boolean isCustomRenderer(){return false;}
 @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){getTransforms().getTransform(context).apply(left,poses);return this;}@Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}@Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));}@Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
 @Override public TextureAtlasSprite getParticleIcon(ModelData data){if(data.has(CONNECTIONS)){var state=data.get(CONNECTIONS);if(state.getValue(MetalFenceBlock.META)==1)return textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.fromNamespaceAndPath("hbm","block/fence_metal_post")));}return originalModel.getParticleIcon(data);}
 @Override public ModelData getModelData(BlockAndTintGetter level,BlockPos pos,BlockState state,ModelData data){return state.getBlock() instanceof MetalFenceBlock fence?data.derive().with(CONNECTIONS,fence.refresh(state,level,pos)).build():data;}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}@Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){if(side!=null)return List.of();if(data.has(CONNECTIONS))state=data.get(CONNECTIONS);if(inventory||state==null){var result=item;if(result==null)item=result=build(null);return result;}return cache.computeIfAbsent(state,this::build);}
 private List<BakedQuad> build(BlockState state){var result=new ArrayList<BakedQuad>();for(var face:SourceGeometry.faces(state,inventory,corner)){var sprite=textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.fromNamespaceAndPath("hbm","block/"+face.texture())));int packed=((byte)(face.nx()*127)&255)|((byte)(face.ny()*127)&255)<<8|((byte)(face.nz()*127)&255)<<16;int[] vertices=new int[32];for(int i=0;i<4;i++){var point=face.vertices().get(i);int offset=i*8;vertices[offset]=Float.floatToRawIntBits(point.x());vertices[offset+1]=Float.floatToRawIntBits(point.y());vertices[offset+2]=Float.floatToRawIntBits(point.z()-(inventory&&!corner?.4375F:0));vertices[offset+3]=0xffffffff;vertices[offset+4]=Float.floatToRawIntBits(sprite.getU(point.u()));vertices[offset+5]=Float.floatToRawIntBits(sprite.getV(point.v()));vertices[offset+6]=0;vertices[offset+7]=packed;}result.add(new BakedQuad(vertices,-1,Direction.getNearest(face.nx(),face.ny(),face.nz()),sprite,!inventory));}return List.copyOf(result);}
}
