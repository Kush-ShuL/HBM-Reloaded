package com.hbm.client.railway.train;
import com.hbm.client.model.*;
import com.hbm.railway.train.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
/** Original Forge OBJ transforms and occupied-slot-dependent real crate load; source model and texture stay unchanged. */
public final class RailTrainRenderer<T extends RailCarEntity> extends EntityRenderer<T> {
    public RailTrainRenderer(EntityRendererProvider.Context context){super(context);}
    @Override public ResourceLocation getTextureLocation(T train){return RailTrainContent.id("textures/models/trains/"+(train.trailer?"tram_trailer":"tram")+".png");}
    @Override public void render(T train,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){pose.pushPose();double ix=train.xo+(train.getX()-train.xo)*partial,iy=train.yo+(train.getY()-train.yo)*partial,iz=train.zo+(train.getZ()-train.zo)*partial;double rx=train.lastRenderX+(train.renderX-train.lastRenderX)*partial,ry=train.lastRenderY+(train.renderY-train.lastRenderY)*partial,rz=train.lastRenderZ+(train.renderZ-train.lastRenderZ)*partial;pose.translate(rx-ix,ry-iy,rz-iz);float current=train.rotationYaw,previous=train.prevRotationYaw;if(current-previous>180)current-=360;if(previous-current>180)previous-=360;pose.mulPose(Axis.YP.rotationDegrees(-(previous+(current-previous)*partial-720)));pose.mulPose(Axis.XP.rotationDegrees(-(train.prevRotationPitch+(train.rotationPitch-train.prevRotationPitch)*partial)));var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),RailTrainContent.id("models/vehicles/"+(train.trailer?"tram_trailer":"tram")+".obj"));LegacyObjRenderer.renderAll(mesh,pose,buffers.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(train))),light,OverlayTexture.NO_OVERLAY,0xffffffff,LegacyObjRenderer.Convention.FORGE);if(train.trailer&&train.occupiedSlots()>0){pose.scale(2,2,2);var crate=new ItemStack(com.hbm.world.structure.loot.StructureLootContent.CRATE_ITEM.get());for(var p:RailTrainLoadLayout.positions(train.occupiedSlots())){pose.pushPose();pose.translate(p[0],p[1],p[2]); // Source framed EntityItem: fixed3D item, no bob or yaw.
            var itemModel=Minecraft.getInstance().getItemRenderer().getModel(crate,train.level(),null,0);com.hbm.client.excavator.MovingItemRenderer.renderSourceFrame(train,crate,itemModel,pose,buffers,light);pose.popPose();}}
        pose.popPose();super.render(train,yaw,partial,pose,buffers,light);
    }
}
