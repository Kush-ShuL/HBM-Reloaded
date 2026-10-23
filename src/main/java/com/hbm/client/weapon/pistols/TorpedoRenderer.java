package com.hbm.client.weapon.pistols;

import com.hbm.client.model.*;
import com.hbm.entity.projectile.EntityTorpedo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** Original RenderBoxcar's torpedo branch, preserving mesh/skin and age-driven tilt. */
public final class TorpedoRenderer extends EntityRenderer<EntityTorpedo> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/weapons/torpedo.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/weapons/torpedo.png");
    public TorpedoRenderer(EntityRendererProvider.Context context){super(context);}
    @Override public void render(EntityTorpedo entity,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
        poses.pushPose();poses.mulPose(Axis.XP.rotationDegrees(Math.min(85,(entity.tickCount+partial)*3)));
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
        LegacyObjRenderer.renderAll(mesh,poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.HFR);poses.popPose();
    }
    @Override public ResourceLocation getTextureLocation(EntityTorpedo entity){return TEXTURE;}
}
