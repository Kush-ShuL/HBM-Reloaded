package com.hbm.client.soyuzlauncher;

import com.hbm.client.weapon.flaregun.FlareSignalRenderer;
import com.hbm.entity.rocket.EntityRocketSoyuz;
import com.hbm.machine.soyuzlauncher.SoyuzLauncherContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** Original RenderSoyuz, original camera-facing engine flare offset and five separate source random samples. */
public final class SoyuzRocketRenderer extends EntityRenderer<EntityRocketSoyuz> {
    public SoyuzRocketRenderer(EntityRendererProvider.Context context){super(context);}
    @Override public ResourceLocation getTextureLocation(EntityRocketSoyuz entity){return SoyuzLauncherContent.id("textures/models/soyuz/payload.png");}
    @Override public void render(EntityRocketSoyuz rocket,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
        poses.pushPose();try{
            SoyuzModels.rocket(rocket.getSkin(),poses,buffers,light,OverlayTexture.NO_OVERLAY);
            var camera=Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();var delta=rocket.getPosition(partial).subtract(camera).normalize().scale(-1.5);poses.translate(delta.x,delta.y,delta.z);
            flare(rocket,partial,poses);for(var offset:new double[][]{{2.5,0},{-2.5,0},{0,2.5},{0,-2.5}}){poses.pushPose();poses.translate(offset[0],0,offset[1]);flare(rocket,partial,poses);poses.popPose();}
        }finally{poses.popPose();}
    }
    private static void flare(EntityRocketSoyuz rocket,float partial,PoseStack poses){if(rocket.tickCount<2)return;poses.pushPose();poses.scale(1.5F,1.5F,1.5F);double size=Math.min(5,(rocket.tickCount+partial-2)*.5)*(.8+rocket.level().random.nextDouble()*.4);FlareSignalRenderer.sprite(poses,size,1,.75F,.5F,.5F,.75F);poses.popPose();}
}
