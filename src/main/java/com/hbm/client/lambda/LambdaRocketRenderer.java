package com.hbm.client.lambda;
import com.hbm.client.weapon.flaregun.FlareSignalRenderer;
import com.hbm.entity.rocket.EntityRocketLambda;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
/** Original smooth Lambda OBJ and engine flare moved one block toward the actual camera. */
public final class LambdaRocketRenderer extends EntityRenderer<EntityRocketLambda> {
    public LambdaRocketRenderer(EntityRendererProvider.Context context){super(context);}
    @Override public ResourceLocation getTextureLocation(EntityRocketLambda rocket){return LambdaModels.ROCKET;}
    @Override public void render(EntityRocketLambda rocket,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){poses.pushPose();try{LambdaModels.rocket(poses,buffers,light,OverlayTexture.NO_OVERLAY);var camera=Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();var delta=rocket.getPosition(partial).subtract(camera).normalize().scale(-1);poses.translate(delta.x,delta.y,delta.z);poses.scale(2,2,2);if(rocket.tickCount>=2){double size=Math.min(5,(rocket.tickCount+partial-2)*.5)*(.8+rocket.level().random.nextDouble()*.4);FlareSignalRenderer.sprite(poses,size,1,.75F,.5F,.5F,.75F);}}finally{poses.popPose();}}
}
