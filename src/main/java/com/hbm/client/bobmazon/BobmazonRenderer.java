package com.hbm.client.bobmazon;

import com.hbm.bobmazon.BobmazonDelivery;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** RenderMinerRocket's original 180-degree X transform, texture and complete original mesh. */
public final class BobmazonRenderer extends EntityRenderer<BobmazonDelivery> {
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/miner_rocket.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/bobmazon.png");
 public BobmazonRenderer(EntityRendererProvider.Context context){super(context);}
 @Override public ResourceLocation getTextureLocation(BobmazonDelivery entity){return TEXTURE;}
 @Override public void render(BobmazonDelivery entity,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){poses.pushPose();poses.mulPose(Axis.XP.rotationDegrees(180));LegacyObjRenderer.renderAll(LegacyObjModels.INSTANCE.get(net.minecraft.client.Minecraft.getInstance().getResourceManager(),MODEL),poses,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.HFR);poses.popPose();}
}
