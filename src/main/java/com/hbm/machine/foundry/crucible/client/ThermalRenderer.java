package com.hbm.machine.foundry.crucible.client;
import com.hbm.client.model.*;
import com.hbm.machine.foundry.crucible.*;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** X5846 RenderFirebox/RenderCrucible transforms and loader conventions, no proxy meshes. */
public final class ThermalRenderer<T extends ThermalMachine> implements BlockEntityRenderer<T> {
 private final boolean firebox;
 public ThermalRenderer(boolean firebox){this.firebox=firebox;}
 static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
 static WavefrontMesh model(boolean firebox){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),id("models/machines/"+(firebox?"firebox":"crucible")+".obj"));}
 static ResourceLocation texture(boolean firebox){return id("textures/models/machines/"+(firebox?"firebox":"crucible_heat")+".png");}
 static void parts(boolean firebox,PoseStack poses,MultiBufferSource buffers,int light,int overlay,String... parts){var mesh=model(firebox);var consumer=buffers.getBuffer(RenderType.entityCutout(texture(firebox)));var convention=firebox?LegacyObjRenderer.Convention.HFR_FLAT:LegacyObjRenderer.Convention.FORGE;if(parts.length==0)LegacyObjRenderer.renderAll(mesh,poses,consumer,light,overlay,0xffffffff,convention);else LegacyObjRenderer.renderOnly(mesh,poses,consumer,light,overlay,0xffffffff,convention,parts);}
 @Override public void render(T machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){int facing=switch(DummyableBlock.facing(machine.getBlockState())){case SOUTH->firebox?-90:270;case EAST->firebox?0:0;case NORTH->firebox?90:90;case WEST->firebox?180:180;default->0;};LegacyContactShadows.render(machine,model(firebox),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(facing)),poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(facing));
  if(machine instanceof FireboxBlockEntity b){parts(true,poses,buffers,light,overlay,"Main");poses.pushPose();poses.translate(1.375,0,.375);poses.mulPose(Axis.YN.rotationDegrees(b.previousDoorAngle+(b.doorAngle-b.previousDoorAngle)*partial));poses.translate(-1.375,0,-.375);parts(true,poses,buffers,light,overlay,"Door");poses.popPose();if(b.wasOn)LegacyObjRenderer.renderOnly(model(true),poses,ThermalRenderTypes.unlitVertices(buffers.getBuffer(ThermalRenderTypes.unlit(texture(true)))),LightTexture.FULL_BRIGHT,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR_FLAT,"InnerBurning");else parts(true,poses,buffers,light,overlay,"InnerEmpty");}
  else if(machine instanceof CrucibleBlockEntity b){parts(false,poses,buffers,light,overlay);if(!b.recipeStack.isEmpty()||!b.wasteStack.isEmpty()){float y=.5F+(CrucibleBlockEntity.quanta(b.recipeStack,null)+CrucibleBlockEntity.quanta(b.wasteStack,null))*.875F/(CrucibleConfig.recipeCapacity+CrucibleConfig.wasteCapacity);var v=buffers.getBuffer(ThermalRenderTypes.unlit(id("textures/models/machines/lava.png")));vertex(v,poses,-1,y,-1,0,0);vertex(v,poses,-1,y,1,0,1);vertex(v,poses,1,y,1,1,1);vertex(v,poses,1,y,-1,1,0);}}
  poses.popPose();
 }
 private static void vertex(VertexConsumer v,PoseStack p,float x,float y,float z,float u,float w){v.addVertex(p.last(),x,y,z).setUv(u,w).setColor(0xffffffff).setLight(LightTexture.FULL_BRIGHT);}
 @Override public boolean shouldRenderOffScreen(T be){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(T be){var p=be.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+(firebox?1:2),p.getZ()+2);}
}
