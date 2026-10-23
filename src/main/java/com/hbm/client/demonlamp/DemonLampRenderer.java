package com.hbm.client.demonlamp;

import com.hbm.block.demonlamp.*;
import com.hbm.client.model.*;
import com.hbm.client.render.WorldEffectsForward;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

/** Original RenderDemonLamp OBJ, clicked-face matrices and both 16-segment additive halos. */
public final class DemonLampRenderer implements BlockEntityRenderer<DemonLampEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/blocks/demon_lamp.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/demon_lamp.png");
    public DemonLampRenderer(BlockEntityRendererProvider.Context context){}
    static void model(PoseStack poses,MultiBufferSource buffers,int light,int overlay){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);var type=RenderType.entitySolid(TEXTURE);LegacyObjRenderer.renderAll(mesh,poses,buffers.getBuffer(type),light,overlay,-1,LegacyObjRenderer.Convention.HFR);if(buffers instanceof MultiBufferSource.BufferSource source)source.endBatch(type);}
    @Override public void render(DemonLampEntity lamp,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        poses.pushPose();poses.translate(.5,.5,.5);
        switch(lamp.getBlockState().getValue(DemonLampBlock.META)){
            case 0->poses.mulPose(Axis.XP.rotationDegrees(180));case 2->{poses.mulPose(Axis.XP.rotationDegrees(90));poses.mulPose(Axis.ZP.rotationDegrees(180));}
            case 3->poses.mulPose(Axis.XP.rotationDegrees(90));case 4->{poses.mulPose(Axis.XP.rotationDegrees(90));poses.mulPose(Axis.ZP.rotationDegrees(90));}
            case 5->{poses.mulPose(Axis.XP.rotationDegrees(90));poses.mulPose(Axis.ZP.rotationDegrees(270));}default->{}
        }
        poses.translate(0,-.5,0);model(poses,buffers,light,overlay);var matrix=new Matrix4f(poses.last().pose());
        if(!WorldEffectsForward.defer(()->glow(matrix)))glow(matrix);
        poses.popPose();
    }
    private static void vertex(BufferBuilder out,Matrix4f matrix,double x,double y,double z,float alpha){out.addVertex(matrix,(float)x,(float)y,(float)z).setColor(0F,.75F,1F,alpha);}
    private static void glow(Matrix4f matrix){
        try(var memory=new ByteBufferBuilder(4096)){
            var out=new BufferBuilder(memory,VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_COLOR);double vx=1,vz=0;float angle=(float)(Math.PI*2/16),cos=Mth.cos(angle),sin=Mth.sin(angle);
            for(int j=0;j<2;j++){
                double height=j==0?-.5:.5;
                for(int i=0;i<16;i++){
                    vertex(out,matrix,vx*.375,.5+j*.125,vz*.375,.25F);vertex(out,matrix,vx*15,.5+j*.125+height,vz*15,0);
                    double nx=vx*cos+vz*sin,nz=vz*cos-vx*sin;vx=nx;vz=nz;
                    vertex(out,matrix,vx*15,.5+j*.125+height,vz*15,0);vertex(out,matrix,vx*.375,.5+j*.125,vz*.375,.25F);
                }
            }
            var mesh=out.build();if(mesh!=null)DemonLampRenderTypes.GLOW.draw(mesh);
        }
    }
    @Override public boolean shouldRenderOffScreen(DemonLampEntity lamp){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(DemonLampEntity lamp){return new AABB(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);}
}
