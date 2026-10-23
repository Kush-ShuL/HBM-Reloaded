package com.hbm.client.reactor.pile.core;
import com.hbm.client.model.*;import com.hbm.machine.reactor.pile.core.*;import com.mojang.blaze3d.vertex.PoseStack;import com.mojang.math.Axis;import net.minecraft.client.Minecraft;import net.minecraft.client.renderer.*;import net.minecraft.client.renderer.blockentity.*;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.phys.AABB;
/** Source HFR meshes, authored normals, exact lever/slider/fan/control-rod matrices. */
public final class PileRenderer<T extends TileEntityPileDeviceBase> implements BlockEntityRenderer<T> {
 public PileRenderer(BlockEntityRendererProvider.Context context){}
 private static String type(int kind){return new String[]{"loader","vent","control"}[kind];}
 static WavefrontMesh model(int kind){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),PileCoreContent.id("models/pile/pile_"+type(kind)+".obj"));}
 static ResourceLocation texture(int kind){return PileCoreContent.id("textures/models/pile/pile_"+type(kind)+".png");}
 private static void part(int kind,PoseStack poses,MultiBufferSource buffers,int light,int overlay,String...parts){LegacyObjRenderer.renderOnly(model(kind),poses,buffers.getBuffer(RenderType.entityCutout(texture(kind))),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,parts);}
 static void all(int kind,PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(kind),poses,buffers.getBuffer(RenderType.entityCutout(texture(kind))),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
 @Override public void render(T entity,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){int kind=entity.getBlockMetadata()/4;int angle=new int[]{90,270,180,0}[entity.getBlockMetadata()%4];LegacyContactShadows.render(entity,model(kind),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(angle)),poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(angle));
  if(entity instanceof TileEntityPileLoader loader){double position=loader.lastLevel+(loader.level-loader.lastLevel)*partial;part(0,poses,buffers,light,overlay,"Loader");poses.pushPose();poses.translate(-.1875,.5,0);poses.mulPose(Axis.ZP.rotationDegrees((float)(position*90)));poses.translate(.1875,-.5,0);part(0,poses,buffers,light,overlay,"Lever");poses.popPose();poses.translate(position*-.5,0,0);part(0,poses,buffers,light,overlay,"Slider");if(loader.syncStack!=null)part(0,poses,buffers,light,overlay,"Rod");}
  else if(entity instanceof TileEntityPileVent vent){part(1,poses,buffers,light,overlay,"Pipe");poses.mulPose(Axis.YP.rotationDegrees(vent.lastFan+(vent.fan-vent.lastFan)*partial));part(1,poses,buffers,light,overlay,"Fan");}
  else if(entity instanceof TileEntityPileControl control){part(2,poses,buffers,light,overlay,"Base");poses.translate(0,(control.lastLevel+(control.level-control.lastLevel)*partial)*.75,0);part(2,poses,buffers,light,overlay,"Rod");}
 poses.popPose();}
 @Override public boolean shouldRenderOffScreen(T entity){return true;}@Override public int getViewDistance(){return 256;}
 @Override public AABB getRenderBoundingBox(T entity){var pos=entity.getBlockPos();return new AABB(pos.getX()-.1,pos.getY(),pos.getZ()-.1,pos.getX()+1.6,pos.getY()+1.75,pos.getZ()+1.6).inflate(.5,0,.5);}
}
