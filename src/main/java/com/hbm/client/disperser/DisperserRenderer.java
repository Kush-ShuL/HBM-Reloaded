package com.hbm.client.disperser;

import com.hbm.entity.grenade.disperser.EntityDisperserCanister;
import com.hbm.item.disperser.DisperserItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** Exact two flat icon passes of RenderGenericGrenade, source half-scale/.25 vertical anchor and signed-byte fluid tint. */
public final class DisperserRenderer extends EntityRenderer<EntityDisperserCanister> {
    private static final ResourceLocation CANISTER=ResourceLocation.parse("hbm:textures/item/disperser_canister.png"),GLAND=ResourceLocation.parse("hbm:textures/item/glyphid_gland.png"),CANISTER_OVERLAY=ResourceLocation.parse("hbm:textures/item/disperser_canister_overlay.png"),GLAND_OVERLAY=ResourceLocation.parse("hbm:textures/item/fluid_identifier_overlay.png");
    public DisperserRenderer(EntityRendererProvider.Context context){super(context);}
    @Override public ResourceLocation getTextureLocation(EntityDisperserCanister entity){return entity.getSourceItem() instanceof DisperserItem item&&item.isGland()?GLAND:CANISTER;}
    @Override public void render(EntityDisperserCanister entity,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
        if(!(entity.getSourceItem() instanceof DisperserItem item))return;
        poses.pushPose();try{poses.scale(.5F,.5F,.5F);poses.mulPose(entityRenderDispatcher.cameraOrientation());poses.mulPose(Axis.YP.rotationDegrees(180));for(int pass=0;pass<2;pass++){var texture=pass==0?getTextureLocation(entity):item.isGland()?GLAND_OVERLAY:CANISTER_OVERLAY;int hex=entity.getFluid().getColor();float r=pass==0?1:(hex>>16&255)/2/127F,g=pass==0?1:(hex>>8&255)/2/127F,b=pass==0?1:(hex&255)/2/127F;var out=buffers.getBuffer(RenderType.entityCutout(texture));float[][] corners={{-.5F,-.25F,0,1},{.5F,-.25F,1,1},{.5F,.75F,1,0},{-.5F,.75F,0,0}};for(var corner:corners)out.addVertex(poses.last(),corner[0],corner[1],0).setColor(r,g,b,1).setUv(corner[2],corner[3]).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(poses.last(),0,1,0);}}finally{poses.popPose();}
    }
}
