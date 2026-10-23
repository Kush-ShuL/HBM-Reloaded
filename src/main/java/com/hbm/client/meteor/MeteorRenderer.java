package com.hbm.client.meteor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;

/** Exact RenderMeteor six faces/UVs, X180 + diagonal spin + scale5 + Z180. */
public final class MeteorRenderer extends EntityRenderer<Entity> {
    public static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/blocks/block_meteor_molten.png");
    private static final float AXIS=(float)(1/Math.sqrt(3));
    private static final float[][] VERTICES={
        {-.5F,-.5F,-.5F,1,0},{.5F,-.5F,-.5F,0,0},{.5F,.5F,-.5F,0,1},{-.5F,.5F,-.5F,1,1},
        {-.5F,-.5F,.5F,1,0},{-.5F,-.5F,-.5F,0,0},{-.5F,.5F,-.5F,0,1},{-.5F,.5F,.5F,1,1},
        {.5F,-.5F,.5F,1,0},{-.5F,-.5F,.5F,0,0},{-.5F,.5F,.5F,0,1},{.5F,.5F,.5F,1,1},
        {.5F,-.5F,-.5F,1,0},{.5F,-.5F,.5F,0,0},{.5F,.5F,.5F,0,1},{.5F,.5F,-.5F,1,1},
        {-.5F,-.5F,.5F,1,0},{.5F,-.5F,.5F,0,0},{.5F,-.5F,-.5F,0,1},{-.5F,-.5F,-.5F,1,1},
        {.5F,.5F,.5F,1,0},{-.5F,.5F,.5F,0,0},{-.5F,.5F,-.5F,0,1},{.5F,.5F,-.5F,1,1}
    };
    public MeteorRenderer(EntityRendererProvider.Context context){super(context);shadowRadius=0;}
    @Override public void render(Entity meteor,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
        poses.pushPose();
        poses.mulPose(Axis.XP.rotationDegrees(180));
        poses.mulPose(new Quaternionf().rotationAxis((meteor.tickCount%360+partial)*10*(float)Math.PI/180F,AXIS,AXIS,AXIS));
        poses.scale(5,5,5);poses.mulPose(Axis.ZP.rotationDegrees(180));
        VertexConsumer vertices=buffers.getBuffer(MeteorRenderTypes.METEOR);
        for(float[] vertex:VERTICES)vertices.addVertex(poses.last().pose(),vertex[0],vertex[1],vertex[2]).setUv(vertex[3],vertex[4]).setColor(255,255,255,255);
        poses.popPose();
    }
    @Override public ResourceLocation getTextureLocation(Entity meteor){return TEXTURE;}
}
