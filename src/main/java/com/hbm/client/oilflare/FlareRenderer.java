package com.hbm.client.oilflare;
import com.hbm.client.model.*;
import com.hbm.machine.oilflare.FlareEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Source fixed180-degree HFR model, source tilt transform, actual mesh contact shadow and full render bounds. */
public final class FlareRenderer implements BlockEntityRenderer<FlareEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/flare_stack.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/flare_stack.png");
    public FlareRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void draw(PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),pose,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR);}
    @Override public void render(FlareEntity flare,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var matrix=new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.PI);if(flare.tilted)matrix.translate(0,-.25F,0).rotateZ((float)Math.toRadians(10)).rotateY((float)Math.toRadians(5));LegacyContactShadows.render(flare,model(),matrix,pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(180));if(flare.tilted){pose.translate(0,-.25,0);pose.mulPose(Axis.ZP.rotationDegrees(10));pose.mulPose(Axis.YP.rotationDegrees(5));}draw(pose,buffers,light,overlay);pose.popPose();}
    @Override public boolean shouldRenderOffScreen(FlareEntity entity){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(FlareEntity entity){var p=entity.getBlockPos();return new AABB(p.getX()-2,p.getY()-1,p.getZ()-2,p.getX()+3,p.getY()+13,p.getZ()+3);}
}
