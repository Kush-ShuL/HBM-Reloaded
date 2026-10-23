package com.hbm.client.missile.rusted;
import com.hbm.client.model.*;
import com.hbm.entity.missile.EntityMissileTier4.EntityMissileDoomsdayRusted;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
/** Source RenderMissileNuclear, restricted to its actual rusted doomsday class, with no substitute rocket. */
public final class RustedMissileRenderer extends EntityRenderer<EntityMissileDoomsdayRusted> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/missile_atlas.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/missile/missile_atlas_doomsday_weathered.png");
    public RustedMissileRenderer(EntityRendererProvider.Context context){super(context);}
    public static void model(PoseStack poses,MultiBufferSource buffers,int light,int overlay,boolean cull){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);LegacyObjRenderer.renderAll(mesh,poses,buffers.getBuffer(cull?RenderType.entityCutout(TEXTURE):RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR);}
    @Override public void render(EntityMissileDoomsdayRusted missile,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
        poses.pushPose();float ry=missile.yRotO+(missile.getYRot()-missile.yRotO)*partial-90,rx=missile.xRotO+(missile.getXRot()-missile.xRotO)*partial;
        poses.mulPose(Axis.YP.rotationDegrees(ry));poses.mulPose(Axis.ZP.rotationDegrees(rx));poses.mulPose(Axis.YN.rotationDegrees(ry));switch(missile.rotation()){case 2->poses.mulPose(Axis.YP.rotationDegrees(90));case 4->poses.mulPose(Axis.YP.rotationDegrees(180));case 3->poses.mulPose(Axis.YP.rotationDegrees(270));}
        model(poses,buffers,light,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,true);poses.popPose();
    }
    @Override public ResourceLocation getTextureLocation(EntityMissileDoomsdayRusted entity){return TEXTURE;}
}
