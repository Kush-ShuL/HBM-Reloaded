package com.hbm.client.structure.industrial44;
import com.hbm.client.model.*;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.neoforged.neoforge.common.util.TriState;
/** Exact RenderAntennaTop/noSmooth OBJ and ObjUtil face brightness; full source icon UVs. */
final class PoleTopModel extends BakedModelWrapper<BakedModel> {
 private final boolean inventory;private final TextureAtlasSprite sprite;private List<BakedQuad> quads;
 PoleTopModel(BakedModel model,boolean inventory,Function<Material,TextureAtlasSprite> sprites){super(model);this.inventory=inventory;sprite=sprites.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/deco_pole_top")));}
 @Override public boolean isCustomRenderer(){return false;}@Override public boolean isGui3d(){return true;}@Override public boolean useAmbientOcclusion(){return false;}@Override public TriState useAmbientOcclusion(BlockState state,ModelData data,RenderType layer){return TriState.FALSE;}
 @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){int hand=context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.FIRST_PERSON_LEFT_HAND?-1:1;if(context.firstPerson()){poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(hand*45));poses.scale(.4F,.4F,.4F);}else if(context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){poses.translate(-hand/16D,-.125,.625);poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-180));poses.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));poses.translate(-hand/16D,.4375,.0625);poses.translate(0,.1875,-.3125);poses.mulPose(com.mojang.math.Axis.XP.rotationDegrees(20));poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(hand*45));poses.scale(-.375F,-.375F,.375F);}else getTransforms().getTransform(context).apply(left,poses);return this;}
 @Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}@Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));}@Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){if(side!=null)return List.of();if(quads==null)quads=bake();return quads;}
 private List<BakedQuad> bake(){var result=new ArrayList<BakedQuad>();var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/blocks/antenna_top.obj"));for(var face:mesh.faces()){
  var n=face.normal();float brightness=inventory?1F:Math.clamp((n.y()+.7F)*.9F-Math.abs(n.x())*.1F+Math.abs(n.z())*.1F,.45F,1F);var out=new QuadBakingVertexConsumer();out.setSprite(sprite);out.setShade(false);out.setHasAmbientOcclusion(false);out.setDirection(Direction.getNearest(n.x(),n.y(),n.z()));
  if(face.corners().size()!=3&&face.corners().size()!=4)throw new IllegalStateException("Unexpected source antenna face "+face.corners().size());for(int i=0;i<4;i++){var corner=face.corners().get(Math.min(i,face.corners().size()-1));var p=mesh.positions().get(corner.position());var uv=mesh.textureCoordinates().get(corner.texture());float scale=inventory?.75F:1F;out.addVertex(p.x()*scale+.5F,p.y()*scale,p.z()*scale+.5F).setColor(brightness,brightness,brightness,1F).setUv(sprite.getU(uv.u()),sprite.getV(1F-uv.v())).setLight(0).setNormal(n.x(),n.y(),n.z());}result.add(out.bakeQuad());
 }return List.copyOf(result);}
}
