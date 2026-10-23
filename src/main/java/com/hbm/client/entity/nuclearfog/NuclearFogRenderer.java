package com.hbm.client.entity.nuclearfog;

import com.hbm.entity.particle.EntityFogFX;
import com.hbm.entity.particle.NuclearFogContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** Actual FogRenderer25 fixed-seed billboards, source UV order, camera rotations, scale and age alpha. */
public final class NuclearFogRenderer extends EntityRenderer<EntityFogFX> {
    public NuclearFogRenderer(EntityRendererProvider.Context context){super(context);}
    @Override public void render(EntityFogFX entity,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
        float alpha=(float)Math.sin(entity.particleAge*Math.PI/400F)*.25F;
        var out=buffers.getBuffer(NuclearFogRenderType.FOG);var shape=new Random(50);
        poses.pushPose();
        try{
            poses.scale(7.5F,7.5F,7.5F);
            for(int i=0;i<25;i++){
                float dx=(float)((shape.nextGaussian()-1D)*.5D),dy=(float)((shape.nextGaussian()-1D)*.15D),dz=(float)((shape.nextGaussian()-1D)*.5D);
                float size=(float)(shape.nextDouble()*.5D+.25D);
                poses.pushPose();
                try{
                    poses.translate(dx,dy,dz);
                    poses.mulPose(Axis.YP.rotationDegrees(180-entityRenderDispatcher.camera.getYRot()));
                    poses.mulPose(Axis.XP.rotationDegrees(180-entityRenderDispatcher.camera.getXRot()));
                    poses.scale(size,size,size);
                    vertex(out,poses,-1,-1,1,0,alpha);vertex(out,poses,-1,1,0,0,alpha);
                    vertex(out,poses,1,1,0,1,alpha);vertex(out,poses,1,-1,1,1,alpha);
                }finally{poses.popPose();}
            }
        }finally{poses.popPose();}
    }
    private static void vertex(com.mojang.blaze3d.vertex.VertexConsumer out,PoseStack poses,float x,float y,float u,float v,float alpha){out.addVertex(poses.last().pose(),x,y,0).setUv(u,v).setColor(.85F,.9F,.5F,alpha);}
    @Override public ResourceLocation getTextureLocation(EntityFogFX entity){return NuclearFogRenderType.TEXTURE;}
    @EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
    public static final class Client {
        private Client(){}
        @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(NuclearFogContent.FOG.get(),NuclearFogRenderer::new);}
    }
}
