package com.hbm.client.structurebasic;
import com.hbm.block.structurebasic.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.ResourceLocation;
/** Literal RenderReeds loop and RenderBlocks.drawCrossedSquares geometry, with original per-segment block light. */
public final class ReedsRenderer implements BlockEntityRenderer<ReedsRenderEntity> {
 public ReedsRenderer(BlockEntityRendererProvider.Context context){}
 @Override public int getViewDistance(){return Minecraft.getInstance().options.getEffectiveRenderDistance()*16;}
 @Override public boolean shouldRenderOffScreen(ReedsRenderEntity reeds){return true;}
 @Override public net.minecraft.world.phys.AABB getRenderBoundingBox(ReedsRenderEntity reeds){var level=reeds.getLevel();return new net.minecraft.world.phys.AABB(reeds.getBlockPos()).expandTowards(0,level==null?0:-Math.max(0,ReedsRenderEntity.depth(level,reeds.getBlockPos(),true)-1),0);}
 @Override public void render(ReedsRenderEntity reeds,float partial,PoseStack poses,MultiBufferSource buffers,int packedLight,int overlay){
  var level=reeds.getLevel();if(level==null)return;int depth=ReedsRenderEntity.depth(level,reeds.getBlockPos(),ReedsRenderConfig.full());
  var atlas=Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS);var vertices=buffers.getBuffer(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
  for(int i=0;i<depth;i++){
   String segment=i==0?"top":i==depth-1?"bottom":"mid";var sprite=atlas.apply(ResourceLocation.parse("hbm:block/reeds_"+segment));
   int light=LevelRenderer.getLightColor(level,reeds.getBlockState(),reeds.getBlockPos().below(i));float y=-i;
   // Original corners are 0.5±0.45 doubles. Four faces retain the original two-sided mirrored-U order.
   quad(vertices,poses.last(),sprite,light,overlay,.05F,.05F,.95F,.95F,y);
   quad(vertices,poses.last(),sprite,light,overlay,.95F,.95F,.05F,.05F,y);
   quad(vertices,poses.last(),sprite,light,overlay,.05F,.95F,.95F,.05F,y);
   quad(vertices,poses.last(),sprite,light,overlay,.95F,.05F,.05F,.95F,y);
  }
 }
 private static void quad(VertexConsumer vertices,PoseStack.Pose pose,TextureAtlasSprite sprite,int light,int overlay,float x0,float z0,float x1,float z1,float y){
  vertex(vertices,pose,x0,y+1,z0,sprite.getU0(),sprite.getV0(),light,overlay);vertex(vertices,pose,x0,y,z0,sprite.getU0(),sprite.getV1(),light,overlay);vertex(vertices,pose,x1,y,z1,sprite.getU1(),sprite.getV1(),light,overlay);vertex(vertices,pose,x1,y+1,z1,sprite.getU1(),sprite.getV0(),light,overlay);
 }
 private static void vertex(VertexConsumer vertices,PoseStack.Pose pose,float x,float y,float z,float u,float v,int light,int overlay){
  // Flat source block colour is exactly .75 white; UP keeps the modern entity shader's diffuse factor1.
  vertices.addVertex(pose.pose(),x,y,z).setColor(.75F,.75F,.75F,1).setUv(u,v).setOverlay(overlay).setLight(light).setNormal(pose,0,1,0);
 }
}
