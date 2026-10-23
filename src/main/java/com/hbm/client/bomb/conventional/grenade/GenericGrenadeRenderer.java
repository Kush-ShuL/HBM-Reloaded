package com.hbm.client.bomb.conventional.grenade;

import com.hbm.bomb.conventional.grenade.GenericGrenade;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Source RenderGenericGrenade's camera-facing single sprite, .5 scale and -.25 icon offset. */
public final class GenericGrenadeRenderer extends EntityRenderer<GenericGrenade> {
    public GenericGrenadeRenderer(EntityRendererProvider.Context context){super(context);}
    @Override public ResourceLocation getTextureLocation(GenericGrenade grenade){return TextureAtlas.LOCATION_BLOCKS;}
    @Override public void render(GenericGrenade grenade,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){
        var sprite=Minecraft.getInstance().getItemRenderer().getModel(new ItemStack(grenade.grenade()),grenade.level(),null,0).getParticleIcon();
        pose.pushPose();pose.scale(.5F,.5F,.5F);pose.mulPose(entityRenderDispatcher.cameraOrientation());var out=buffers.getBuffer(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));
        out.addVertex(pose.last(),-.5F,-.25F,0).setColor(-1).setUv(sprite.getU0(),sprite.getV1()).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose.last(),0,1,0);
        out.addVertex(pose.last(),.5F,-.25F,0).setColor(-1).setUv(sprite.getU1(),sprite.getV1()).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose.last(),0,1,0);
        out.addVertex(pose.last(),.5F,.75F,0).setColor(-1).setUv(sprite.getU1(),sprite.getV0()).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose.last(),0,1,0);
        out.addVertex(pose.last(),-.5F,.75F,0).setColor(-1).setUv(sprite.getU0(),sprite.getV0()).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose.last(),0,1,0);
        pose.popPose();super.render(grenade,yaw,partial,pose,buffers,light);
    }
}
