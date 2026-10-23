package com.hbm.client.entity.plasticbag;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.entity.mob.plasticbag.EntityPlasticBag;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
/** Original RenderPlasticBag exact source OBJ, +90Y/-90Z rotations and no-cull/zero-shadow material. */
public final class PlasticBagMobRenderer extends EntityRenderer<EntityPlasticBag> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/mobs/plasticbag.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/entity/plasticbag.png");
    public PlasticBagMobRenderer(EntityRendererProvider.Context context){super(context);shadowStrength=0F;}
    @Override public void render(EntityPlasticBag entity,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){
        pose.pushPose();
        try{
            pose.mulPose(Axis.YP.rotationDegrees(entity.yRotO+(entity.getYRot()-entity.yRotO)*partial+90F));
            pose.mulPose(Axis.ZP.rotationDegrees(entity.xRotO+(entity.getXRot()-entity.xRotO)*partial-90F));
            var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
            LegacyObjRenderer.renderAll(mesh,pose,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.FORGE);
        }finally{pose.popPose();}
    }
    @Override public ResourceLocation getTextureLocation(EntityPlasticBag entity){return TEXTURE;}
}
