package com.hbm.client.satellite.resources;
import com.hbm.client.model.*;
import com.hbm.entity.mob.fbi.EntityFBIDrone;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
/** Original RenderDrone full Forge quadcopter.obj, y+.25 and deterministic Random(entityID) yaw. */
public final class FbiDroneRenderer extends EntityRenderer<EntityFBIDrone> {
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/entity/quadcopter.png"),MODEL=ResourceLocation.parse("hbm:models/mobs/quadcopter.obj");
    public FbiDroneRenderer(EntityRendererProvider.Context context){super(context);}
    @Override public ResourceLocation getTextureLocation(EntityFBIDrone mob){return TEXTURE;}
    @Override public void render(EntityFBIDrone mob,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){poses.pushPose();poses.translate(0,.25,0);poses.mulPose(Axis.YP.rotationDegrees((float)(new Random(mob.getId()).nextDouble()*360)));LegacyObjRenderer.renderAll(LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL),poses,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.FORGE);poses.popPose();super.render(mob,yaw,partial,poses,buffers,light);}
}
