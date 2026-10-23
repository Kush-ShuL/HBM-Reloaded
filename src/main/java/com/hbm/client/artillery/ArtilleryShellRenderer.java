package com.hbm.client.artillery;
import com.hbm.client.model.*;
import com.hbm.weapon.artillery.ArtilleryShellEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
/** Source actual Grenade OBJ part, yaw-90/pitch-90, asymmetric2.5/5/2.5 scale, smooth HFR normals and source no-fog draw. */
public final class ArtilleryShellRenderer extends EntityRenderer<ArtilleryShellEntity> {
    public ArtilleryShellRenderer(EntityRendererProvider.Context context){super(context);shadowRadius=0;}
    @Override public ResourceLocation getTextureLocation(ArtilleryShellEntity shell){return ResourceLocation.parse("hbm:textures/models/projectiles/grenade.png");}
    @Override public boolean shouldRender(ArtilleryShellEntity entity,Frustum frustum,double x,double y,double z){return true;}
    @Override public void render(ArtilleryShellEntity shell,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partial,shell.yRotO,shell.getYRot())-90));pose.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partial,shell.xRotO,shell.getXRot())-90));pose.scale(2.5F,5,2.5F);var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/projectiles/projectiles.obj"));LegacyObjRenderer.renderOnly(mesh,pose,buffers.getBuffer(ArtilleryShellRenderType.TYPE),light,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.HFR,"Grenade");pose.popPose();}
}
