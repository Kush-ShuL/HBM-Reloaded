package com.hbm.client.bomb.nuclear;
import com.hbm.bomb.nuclear.*;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.WavefrontMesh;
import com.mojang.blaze3d.vertex.*;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
/** Exact original sphere_new geometry, Fleija shell formulae and Solinium's three additive shells. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class NuclearCloudRenderer {
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/sphere_new.obj");
 private static final RenderType OPAQUE=com.hbm.client.render.WorldEffectMeshes.occluder(SphereType.layer(false,false)),DOUBLE_SIDED=com.hbm.client.render.WorldEffectMeshes.occluder(SphereType.layer(false,true)),ADDITIVE=SphereType.layer(true,false);
 private static final MultiBufferSource.BufferSource BUFFERS=MultiBufferSource.immediate(new ByteBufferBuilder(262144));
 @SubscribeEvent public static void render(RenderLevelStageEvent event){
  if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES)return;var mc=Minecraft.getInstance();if(mc.level==null)return;var eye=event.getCamera().getPosition();float partial=event.getPartialTick().getGameTimeDeltaPartialTick(false);WavefrontMesh mesh=null;
  for(var entity:mc.level.entitiesForRendering())if(entity instanceof NuclearFleijaCloud cloud&&!cloud.isRemoved()&&cloud.shouldRenderAtSqrDistance(cloud.distanceToSqr(eye))){
   if(mesh==null)mesh=LegacyObjModels.INSTANCE.get(mc.getResourceManager(),MODEL);var pose=event.getPoseStack();pose.pushPose();pose.translate(cloud.getX()-eye.x,cloud.getY()-eye.y,cloud.getZ()-eye.z);
   if(cloud instanceof NuclearSoliniumCloud){
    pose.scale(cloud.age+partial,cloud.age+partial,cloud.age+partial);sphere(mesh,pose,DOUBLE_SIDED,0x27/255F,1,0xDA/255F,1);
    for(int i=0;i<3;i++){pose.scale(1.025F,1.025F,1.025F);sphere(mesh,pose,ADDITIVE,0x27/255F,1,0xDA/255F,.125F);}
   }else{
    double base=(cloud.age+partial)*2,ageScale=base/cloud.getMaxAge();pose.pushPose();double scale=ageScale*1.2;if(scale>1)scale=Math.max(1-(scale-1)*5,0);scale*=2*base;pose.scale((float)scale,(float)scale,(float)scale);sphere(mesh,pose,OPAQUE,0,1,1,1);
    for(int i=0;i<3;i++){pose.scale(1.05F,1.05F,1.05F);sphere(mesh,pose,ADDITIVE,0,.125F,.125F,1);}pose.popPose();
    float shock=(float)(5*base),tint=net.minecraft.util.Mth.clamp((1F-(float)ageScale)*.75F,0,1);pose.scale(shock,shock,shock);sphere(mesh,pose,ADDITIVE,tint,tint,tint,1);
   }
   pose.popPose();
  }
 }
 private static void sphere(WavefrontMesh mesh,PoseStack pose,RenderType type,float r,float g,float b,float a){
  var out=BUFFERS.getBuffer(type);var matrix=pose.last().pose();for(var triangle:mesh.triangles()){var face=mesh.faces().get(triangle.face());var first=mesh.positions().get(face.corners().get(triangle.first()).position());var second=mesh.positions().get(face.corners().get(triangle.second()).position());var third=mesh.positions().get(face.corners().get(triangle.third()).position());out.addVertex(matrix,first.x(),first.y(),first.z()).setColor(r,g,b,a);out.addVertex(matrix,second.x(),second.y(),second.z()).setColor(r,g,b,a);out.addVertex(matrix,third.x(),third.y(),third.z()).setColor(r,g,b,a);out.addVertex(matrix,third.x(),third.y(),third.z()).setColor(r,g,b,a);}BUFFERS.endBatch(type);
 }
 private static final class SphereType extends RenderType {
  private SphereType(String n,VertexFormat f,VertexFormat.Mode m,int size,boolean crumble,boolean sorted,Runnable setup,Runnable clear){super(n,f,m,size,crumble,sorted,setup,clear);}
  static RenderType layer(boolean additive,boolean noCull){return com.hbm.client.render.WorldEffectMeshes.register(create("hbm_nuclear_sphere_"+additive+"_"+noCull,DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,262144,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getRendertypeLightningShader)).setTransparencyState(additive?LIGHTNING_TRANSPARENCY:NO_TRANSPARENCY).setCullState(noCull?NO_CULL:CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(com.hbm.client.render.SourceDepthWrite.MASK).createCompositeState(false)),"FLEIJA+Solinium/Core+Shell");}
 }
 private NuclearCloudRenderer(){}
}
