package com.hbm.client.network.cargo;
import com.hbm.client.model.*;
import com.hbm.network.cargo.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
/** Original smooth HFR Drone/Crate/Barrel parts, no yaw transform or replacement model. */
public final class CargoDroneRenderer<T extends CargoDroneBase> extends EntityRenderer<T> {
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/drone.obj");public CargoDroneRenderer(EntityRendererProvider.Context context){super(context);}
 @Override public ResourceLocation getTextureLocation(T drone){return ResourceLocation.parse("hbm:textures/models/machines/"+(drone.isRequestDrone()?"drone_request":drone instanceof PatrolDrone patrol&&patrol.express()?"drone_express":"drone")+".png");}
 @Override public void render(T drone,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);var vertices=buffers.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(drone)));LegacyObjRenderer.renderOnly(mesh,poses,vertices,light,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.HFR,"Drone");int style=drone.getAppearance();if(style==1||style==2)LegacyObjRenderer.renderOnly(mesh,poses,vertices,light,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.HFR,style==1?"Crate":"Barrel");super.render(drone,yaw,partial,poses,buffers,light);}
}
