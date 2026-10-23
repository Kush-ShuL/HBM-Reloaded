package com.hbm.client.entity.cataclysm;

import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.entity.projectile.cataclysm.EntityBurningFOEQ;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** Original body and40 cumulative OBJ fire transforms; not a meteor/smoke/vanilla particle replacement. */
public final class FoeqRenderer extends EntityRenderer<EntityBurningFOEQ> {
    private static final ResourceLocation BODY=ResourceLocation.parse("hbm:models/sat_foeq_burning.obj"),FIRE=ResourceLocation.parse("hbm:models/sat_foeq_fire.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/sat_foeq_burning.png");
    public FoeqRenderer(EntityRendererProvider.Context context){super(context);}
    @Override public ResourceLocation getTextureLocation(EntityBurningFOEQ entity){return TEXTURE;}
    @Override public void render(EntityBurningFOEQ entity,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
        poses.pushPose();
        try{
            poses.translate(0,-75,0);poses.mulPose(Axis.YP.rotationDegrees(entity.yRotO+(entity.getYRot()-entity.yRotO)*partial-90));poses.mulPose(Axis.ZP.rotationDegrees(180));poses.mulPose(Axis.ZP.rotationDegrees(entity.xRotO+(entity.getXRot()-entity.xRotO)*partial));
            var resources=Minecraft.getInstance().getResourceManager();var bodyType=RenderType.entityCutout(TEXTURE);
            LegacyObjRenderer.renderAll(LegacyObjModels.INSTANCE.get(resources,BODY),poses,buffers.getBuffer(bodyType),15728880,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.FORGE);
            if(buffers instanceof MultiBufferSource.BufferSource direct)direct.endBatch(bodyType);
            var fire=LegacyObjModels.INSTANCE.get(resources,FIRE);var output=buffers.getBuffer(FoeqRenderTypes.FIRE);
            Random random=new Random(System.currentTimeMillis()/50);
            poses.scale(1.15F,.75F,1.15F);poses.translate(0,-.5,.3);
            int[] colors={0xFFFFBF3F,0xFFFF7F00,0xFFFF3F00,0xFFFF2600};
            for(int i=0;i<10;i++){
                for(int j=0;j<4;j++){
                    poses.mulPose(Axis.YP.rotationDegrees(random.nextInt(360)));
                    LegacyObjRenderer.renderAll(fire,poses,output,15728880,OverlayTexture.NO_OVERLAY,colors[j],LegacyObjRenderer.Convention.FORGE);
                    if(j<3)poses.translate(0,2,0);
                }
                poses.translate(0,-3.8,0);poses.scale(.95F,1.2F,.95F);
            }
            if(buffers instanceof MultiBufferSource.BufferSource direct)direct.endBatch(FoeqRenderTypes.FIRE);
        }finally{poses.popPose();}
    }
}
