package com.hbm.client.electricpress;

import com.hbm.client.model.*;
import com.hbm.machine.electricpress.ConveyorPressEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Source Press/Piston/Belt parts,stamp-gated visible piston and exact worldTime16 texture-matrix translation. */
public final class ConveyorPressRenderer implements BlockEntityRenderer<ConveyorPressEntity> {
    static final ResourceLocation MODEL=ElectricPressRenderer.id("models/machines/conveyor_press.obj"),TEXTURE=ElectricPressRenderer.id("textures/models/machines/conveyor_press.png"),BELT=ElectricPressRenderer.id("textures/models/machines/conveyor_press_belt.png");
    private static final Map<WavefrontMesh,WavefrontMesh> STATIC_SHADOW=new WeakHashMap<>();
    public ConveyorPressRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh model(){return ElectricPressRenderer.mesh(MODEL);}
    private static WavefrontMesh shadow(WavefrontMesh mesh){return STATIC_SHADOW.computeIfAbsent(mesh,value->new WavefrontMesh(value.positions(),value.textureCoordinates(),value.normals(),value.faces(),value.triangles().stream().filter(triangle->!value.faces().get(triangle.face()).legacyPart().equalsIgnoreCase("Piston")).toList(),value.materialLibraries(),value.nonFaceElements(),value.otherDirectives()));}
    static void parts(PoseStack pose,MultiBufferSource buffers,int light,int overlay,String... parts){LegacyObjRenderer.renderOnly(model(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.FORGE,parts);}
    static void belt(PoseStack pose,MultiBufferSource buffers,int light,int overlay,float offset){LegacyObjRenderer.renderOnly(model(),pose,shift(buffers.getBuffer(RenderType.entityCutout(BELT)),offset),light,overlay,-1,LegacyObjRenderer.Convention.FORGE,"Belt");}
    private static VertexConsumer shift(VertexConsumer out,float offset){return new VertexConsumer(){public VertexConsumer addVertex(float x,float y,float z){out.addVertex(x,y,z);return this;}public VertexConsumer setColor(int r,int g,int b,int a){out.setColor(r,g,b,a);return this;}public VertexConsumer setUv(float u,float v){out.setUv(u,v+offset);return this;}public VertexConsumer setUv1(int u,int v){out.setUv1(u,v);return this;}public VertexConsumer setUv2(int u,int v){out.setUv2(u,v);return this;}public VertexConsumer setNormal(float x,float y,float z){out.setNormal(x,y,z);return this;}};}
    @Override public void render(ConveyorPressEntity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){float rotation=ElectricPressRenderer.yaw(machine.getBlockState().getValue(DummyableBlock.LEGACY_META));LegacyContactShadows.render(machine,shadow(model()),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(rotation)),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(rotation));parts(pose,buffers,light,overlay,"Press");if(!machine.renderedStamp.isEmpty()){pose.pushPose();pose.translate(0,-machine.renderedPress(partial)*.75,0);parts(pose,buffers,light,overlay,"Piston");pose.popPose();}belt(pose,buffers,light,overlay,(float)((int)(machine.getLevel().getGameTime()%16)-2)/16F);pose.popPose();}
    @Override public boolean shouldRenderOffScreen(ConveyorPressEntity machine){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(ConveyorPressEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+3,p.getZ()+2);}
}
