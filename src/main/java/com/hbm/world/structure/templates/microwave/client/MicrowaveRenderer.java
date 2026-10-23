package com.hbm.world.structure.templates.microwave.client;
import com.hbm.client.model.*;
import com.hbm.world.structure.templates.microwave.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
/** Source ForgeWavefront mainbody/window/plate, original world-millis turntable and complete offscreen extent. */
public final class MicrowaveRenderer implements BlockEntityRenderer<MicrowaveBlockEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/microwave.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/microwave.png");
    private static final AABB INFINITE=new AABB(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);
    public MicrowaveRenderer(BlockEntityRendererProvider.Context context){}
    private static void transform(PoseStack pose,int meta){pose.translate(.5D,-.785D,.5D);int angle=switch(meta){case 4->90;case 3->180;case 5->270;default->0;};pose.mulPose(Axis.YP.rotationDegrees(angle));pose.translate(-.5D,0D,.65D);}
    @Override public void render(MicrowaveBlockEntity microwave,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);int meta=microwave.getBlockState().getValue(MicrowaveBlock.META);var local=new PoseStack();transform(local,meta);LegacyContactShadows.render(microwave,mesh,new Matrix4f(local.last().pose()),poses,buffers);poses.pushPose();transform(poses,meta);var vertices=buffers.getBuffer(RenderType.entityCutout(TEXTURE));LegacyObjRenderer.render(mesh,poses,vertices,light,overlay,-1,LegacyObjRenderer.Convention.FORGE,WavefrontMesh.legacyPart("mainbody_Cube.001"));LegacyObjRenderer.render(mesh,poses,vertices,light,overlay,-1,LegacyObjRenderer.Convention.FORGE,WavefrontMesh.legacyPart("window_Cube.002"));if(microwave.time>0){double rotation=(System.currentTimeMillis()*microwave.speed/10D)%360D;poses.translate(.575D,0D,-.45D);poses.mulPose(Axis.YP.rotationDegrees((float)rotation));poses.translate(-.575D,0D,.45D);}LegacyObjRenderer.render(mesh,poses,vertices,light,overlay,-1,LegacyObjRenderer.Convention.FORGE,WavefrontMesh.legacyPart("plate_Cylinder"));poses.popPose();}
    @Override public boolean shouldRenderOffScreen(MicrowaveBlockEntity microwave){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(MicrowaveBlockEntity microwave){return INFINITE;}
}
