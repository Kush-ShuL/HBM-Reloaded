package com.hbm.client.specialvehicle;

import com.hbm.entity.specialvehicle.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** Actual RenderBoxcar branches; the source ignores entity yaw/pitch and has zero vanilla circular shadow. */
public final class VehicleEntityRenderer<T extends FallingVehicle> extends EntityRenderer<T> {
    private final boolean boat;
    public VehicleEntityRenderer(EntityRendererProvider.Context context,boolean boat){super(context);this.boat=boat;}
    @Override public void render(T entity,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){poses.pushPose();if(boat)poses.translate(0,0,-1);else{poses.translate(0,0,-1.5);poses.mulPose(Axis.ZP.rotationDegrees(180));poses.mulPose(Axis.XP.rotationDegrees(90));}VehicleModels.draw(boat,poses,buffers,light,OverlayTexture.NO_OVERLAY);poses.popPose();}
    @Override public ResourceLocation getTextureLocation(T entity){return boat?VehicleModels.BOAT_TEXTURE:VehicleModels.BOXCAR_TEXTURE;}
}
