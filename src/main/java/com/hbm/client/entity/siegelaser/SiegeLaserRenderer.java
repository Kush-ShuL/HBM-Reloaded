package com.hbm.client.entity.siegelaser;

import com.hbm.entity.projectile.siegelaser.EntitySiegeLaser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class SiegeLaserRenderer extends EntityRenderer<EntitySiegeLaser> {
    public SiegeLaserRenderer(EntityRendererProvider.Context context){super(context);}
    @Override public ResourceLocation getTextureLocation(EntitySiegeLaser entity){return ResourceLocation.parse("hbm:textures/models/thegadget3_.png");} // Source return-only; no texture is bound by this untextured renderer.
    @Override public void render(EntitySiegeLaser laser,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
        poses.pushPose();try{poses.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partial,laser.yRotO,laser.getYRot())-90F));poses.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partial,laser.xRotO,laser.getXRot())+180F));poses.scale(.25F,.125F,.125F);poses.scale(-1,1,1);poses.scale(2,2,2);SiegeLaserMesh.draw(poses,buffers,laser.getColor());}finally{poses.popPose();}
    }
}
