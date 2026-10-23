package com.hbm.client.charger;

import com.hbm.client.model.*;
import com.hbm.machine.charger.ChargerBlock;
import com.hbm.machine.charger.ChargerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Literal original RenderCharger matrices and its independently moving clamp halves. */
public final class ChargerRenderer implements BlockEntityRenderer<ChargerBlockEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/charger.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/charger.png");
    public ChargerRenderer(BlockEntityRendererProvider.Context context){}
    private static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    private static void part(String part,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderOnly(mesh(),pose,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,part);}
    private static void slide(PoseStack pose,double extend){pose.translate(-.34375,.25,0);pose.mulPose(Axis.ZP.rotationDegrees(10));pose.translate(.34375,-.25,0);pose.translate(0,-.25*extend,0);}
    static void inventory(PoseStack pose,MultiBufferSource buffers,int light,int overlay){pose.scale(2,2,2);pose.translate(.5,0,0);part("Base",pose,buffers,light,overlay);part("Slide",pose,buffers,light,overlay);}
    @Override public void render(ChargerBlockEntity charger,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        int metadata=charger.getBlockState().getValue(ChargerBlock.META);float angle=90+switch(metadata){case 4->90;case 3->180;case 5->270;default->0;};
        LegacyContactShadows.render(charger,mesh(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(angle)),pose,buffers);
        pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(angle));part("Base",pose,buffers,light,overlay);
        double time=(charger.lastUsingTicks()+(charger.usingTicks()-charger.lastUsingTicks())*partial)/20D,extend=Math.min(1,time*2),swivel=Math.max(0,(time-.5)*2);
        pose.pushPose();slide(pose,extend);
        pose.pushPose();pose.translate(0,.28,0);pose.mulPose(Axis.XP.rotationDegrees((float)(30*swivel)));pose.translate(0,-.28,0);part("Left",pose,buffers,light,overlay);pose.popPose();
        pose.pushPose();pose.translate(0,.28,0);pose.mulPose(Axis.XP.rotationDegrees((float)(-30*swivel)));pose.translate(0,-.28,0);part("Right",pose,buffers,light,overlay);pose.popPose();pose.popPose();
        var vertices=buffers.getBuffer(ChargerRenderTypes.LIGHT);
        for(var triangle:mesh().triangles()){
            var face=mesh().faces().get(triangle.face());if(!face.legacyPart().equalsIgnoreCase("Light"))continue;
            int[] corners={triangle.first(),triangle.second(),triangle.third(),triangle.third()};
            for(int corner:corners){var point=mesh().positions().get(face.corners().get(corner).position());vertices.addVertex(pose.last(),point.x(),point.y(),point.z()).setColor(1F,.75F,0F,1F);}
        }
        pose.pushPose();slide(pose,extend);LegacyObjRenderer.renderOnly(mesh(),pose,buffers.getBuffer(ChargerRenderTypes.SLIDE),LightTexture.FULL_BRIGHT,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,"Slide");pose.popPose();pose.popPose();
    }
    @Override public AABB getRenderBoundingBox(ChargerBlockEntity charger){return new AABB(charger.getBlockPos()).inflate(1);}
    @Override public int getViewDistance(){return 256;}
}
