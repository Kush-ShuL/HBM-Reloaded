package com.hbm.client.satellite.resources;
import com.hbm.client.animation.hmf.*;
import com.hbm.client.model.*;
import com.hbm.satellite.resources.EntityTom;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
/** Actual RenderTom/TomPronter matrices,20 deterministic flame passes, original HMF scrolling and UVs. */
public final class TomRenderer extends EntityRenderer<EntityTom> {
    private static final ResourceLocation BODY=ResourceLocation.parse("hbm:models/weapons/tom_main.obj"),FLAME=ResourceLocation.parse("hbm:models/weapons/tom_flame.hmf");
    public TomRenderer(EntityRendererProvider.Context context){super(context);}
    @Override public ResourceLocation getTextureLocation(EntityTom entity){return ResourceLocation.parse("hbm:textures/models/weapons/tom_main.png");}
    @Override public void render(EntityTom entity,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
        if(com.hbm.client.render.WorldEffectsForward.shadowPass())return;
        poses.pushPose();poses.translate(0,-50,0);poses.scale(100,100,100);var resources=Minecraft.getInstance().getResourceManager();
        LegacyObjRenderer.renderAll(LegacyObjModels.INSTANCE.get(resources,BODY),poses,buffers.getBuffer(TomRenderTypes.BODY),LightTexture.FULL_BRIGHT,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.FORGE);
        double oldModulo=HmfController.modoloMod,oldQuotient=HmfController.quotientMod;HmfController.setMod(50000,2500);
        try{long frameMillis=com.hbm.client.animation.AnimationClock.get_ms();float rotation=-frameMillis/10%360;float scroll=(float)((frameMillis%50000L)/2500D);poses.scale(.8F,5,.8F);var random=new Random(0);var model=HmfModel.load(resources,FLAME);for(int i=0;i<20;i++){int r=random.nextInt(90);poses.mulPose(Axis.YP.rotationDegrees(rotation+r));model.renderAllWithScroll(poses,buffers.getBuffer(TomRenderTypes.FLAME),LightTexture.FULL_BRIGHT,OverlayTexture.NO_OVERLAY,-1,scroll);poses.mulPose(Axis.YN.rotationDegrees(rotation));poses.scale(-1.015F,.9F,1.015F);}}
        finally{HmfController.setMod(oldModulo,oldQuotient);poses.popPose();}
        super.render(entity,yaw,partial,poses,buffers,light);
    }
}
