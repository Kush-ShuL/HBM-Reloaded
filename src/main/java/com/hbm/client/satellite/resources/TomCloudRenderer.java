package com.hbm.client.satellite.resources;

import com.hbm.client.bomb.nuclear.MukeRenderTypes;
import com.hbm.satellite.resources.EntityCloudTom;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** Actual16x5 cylindrical Tom cloud; the single existing nuclear cloud-depth queue owns native-cloud composition. */
public final class TomCloudRenderer extends EntityRenderer<EntityCloudTom> {
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/models/explosion/tomblast.png");
    private static final ParticleRenderType MATERIAL=MukeRenderTypes.cloudTexture(TEXTURE,false);
    public TomCloudRenderer(EntityRendererProvider.Context context){super(context);}
    @Override public ResourceLocation getTextureLocation(EntityCloudTom entity){return TEXTURE;}
    @Override public void render(EntityCloudTom cloud,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
        var matrix=new org.joml.Matrix4f(poses.last().pose());double scale=cloud.age+partial;var player=Minecraft.getInstance().player;float movement=-(player==null?partial:player.tickCount+partial)*.005F*10;
        MukeRenderTypes.deferDraw(()->{
            var out=MukeRenderTypes.begin(MATERIAL);float angle=(float)Math.toRadians(360D/16);
            for(int i=0;i<16;i++)for(int j=0;j<5;j++){
                double mod=1-j*.025,height=20+j*10,offset=1D/j; // Source j0 intentionally has infinite V; do not invent another UV or remove a source layer.
                double x0=scale*Mth.cos(angle*i),z0=-scale*Mth.sin(angle*i);
                vertex(out,matrix,x0*mod,height,z0*mod,0,1+offset+movement,0);
                vertex(out,matrix,x0*mod,-20,z0*mod,0,offset+movement,255);
                double x1=x0*Mth.cos(angle)+z0*Mth.sin(angle),z1=z0*Mth.cos(angle)-x0*Mth.sin(angle);
                vertex(out,matrix,x1*mod,-20,z1*mod,1,offset+movement,255);
                vertex(out,matrix,x1*mod,height,z1*mod,1,1+offset+movement,0);
            }
        });
    }
    private static void vertex(com.mojang.blaze3d.vertex.VertexConsumer out,org.joml.Matrix4f matrix,double x,double y,double z,double u,double v,int alpha){out.addVertex(matrix,(float)x,(float)y,(float)z).setUv((float)u,(float)v).setColor(255,255,255,alpha).setLight(LightTexture.FULL_BRIGHT);}
}
