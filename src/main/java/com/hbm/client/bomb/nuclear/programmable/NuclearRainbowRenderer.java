package com.hbm.client.bomb.nuclear.programmable;
import com.hbm.bomb.nuclear.programmable.NuclearRainbowCloud;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/** RenderCloudRainbow: original Sphere.obj, random RGB each frame and original float shell loop. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class NuclearRainbowRenderer {
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/sphere.obj");
 private static final RenderType OPAQUE=com.hbm.client.render.WorldEffectMeshes.occluder(SphereType.sourceLayer(false)),ADDITIVE=SphereType.sourceLayer(true);
 private static final MultiBufferSource.BufferSource BUFFERS=MultiBufferSource.immediate(new ByteBufferBuilder(262144));
 @SubscribeEvent public static void render(RenderLevelStageEvent event){if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES)return;var mc=Minecraft.getInstance();if(mc.level==null)return;var eye=event.getCamera().getPosition();WavefrontMesh mesh=null;for(var entity:mc.level.entitiesForRendering())if(entity instanceof NuclearRainbowCloud cloud&&!cloud.isRemoved()&&cloud.shouldRenderAtSqrDistance(cloud.distanceToSqr(eye))){if(mesh==null)mesh=LegacyObjModels.INSTANCE.get(mc.getResourceManager(),MODEL);var pose=event.getPoseStack();pose.pushPose();pose.translate(cloud.getX()-eye.x,cloud.getY()-eye.y,cloud.getZ()-eye.z);pose.scale(cloud.age,cloud.age,cloud.age);pose.scale(.5F,.5F,.5F);sphere(mesh,pose,OPAQUE,mc);pose.scale(2,2,2);for(float scale=.6F;scale<=1F;scale+=.1F){pose.scale(scale,scale,scale);sphere(mesh,pose,ADDITIVE,mc);pose.scale(1/scale,1/scale,1/scale);}pose.popPose();}}
 private static void sphere(WavefrontMesh mesh,PoseStack pose,RenderType type,Minecraft mc){int color=0xff000000|mc.level.random.nextInt(256)<<16|mc.level.random.nextInt(256)<<8|mc.level.random.nextInt(256);var out=BUFFERS.getBuffer(type);for(var triangle:mesh.triangles()){var face=mesh.faces().get(triangle.face());int[] indices={triangle.first(),triangle.second(),triangle.third(),triangle.third()};for(int index:indices){var p=mesh.positions().get(face.corners().get(index).position());out.addVertex(pose.last().pose(),p.x(),p.y(),p.z()).setColor(color);}}BUFFERS.endBatch(type);}
 private static final class SphereType extends RenderType {
  private SphereType(String n,VertexFormat f,VertexFormat.Mode m,int size,boolean c,boolean s,Runnable setup,Runnable clear){super(n,f,m,size,c,s,setup,clear);}
  static RenderType sourceLayer(boolean additive){return com.hbm.client.render.WorldEffectMeshes.register(create("hbm_rainbow_"+additive,DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,262144,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getRendertypeLightningShader)).setTransparencyState(additive?LIGHTNING_TRANSPARENCY:NO_TRANSPARENCY).setCullState(CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(com.hbm.client.render.SourceDepthWrite.MASK).createCompositeState(false)),"Rainbow/Core+Shell");}
 }
 private NuclearRainbowRenderer(){}
}
