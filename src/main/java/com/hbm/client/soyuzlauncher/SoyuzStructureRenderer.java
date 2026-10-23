package com.hbm.client.soyuzlauncher;

import com.hbm.machine.soyuzlauncher.*;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.world.phys.AABB;

/** Original RenderSoyuzMultiblock's1/16 miniature six faces,0.75alpha,global(1,1,0) and each RZ180. */
public final class SoyuzStructureRenderer implements BlockEntityRenderer<SoyuzStructureEntity> {
    private static final float[][][] FACES={
        {{0.65625F,0.65625F,0.65625F,1F,0F},{0.34375F,0.65625F,0.65625F,0F,0F},{0.34375F,0.34375F,0.65625F,0F,1F},{0.65625F,0.34375F,0.65625F,1F,1F}},
        {{0.65625F,0.65625F,0.34375F,1F,0F},{0.65625F,0.65625F,0.65625F,0F,0F},{0.65625F,0.34375F,0.65625F,0F,1F},{0.65625F,0.34375F,0.34375F,1F,1F}},
        {{0.34375F,0.65625F,0.34375F,1F,0F},{0.65625F,0.65625F,0.34375F,0F,0F},{0.65625F,0.34375F,0.34375F,0F,1F},{0.34375F,0.34375F,0.34375F,1F,1F}},
        {{0.34375F,0.65625F,0.65625F,1F,0F},{0.34375F,0.65625F,0.34375F,0F,0F},{0.34375F,0.34375F,0.34375F,0F,1F},{0.34375F,0.34375F,0.65625F,1F,1F}},
        {{0.65625F,0.65625F,0.34375F,1F,0F},{0.34375F,0.65625F,0.34375F,0F,0F},{0.34375F,0.65625F,0.65625F,0F,1F},{0.65625F,0.65625F,0.65625F,1F,1F}},
        {{0.34375F,0.34375F,0.34375F,1F,0F},{0.65625F,0.34375F,0.34375F,0F,0F},{0.65625F,0.34375F,0.65625F,0F,1F},{0.34375F,0.34375F,0.65625F,1F,1F}}
    };
    private static final float[][] NORMALS={{0,0,1},{1,0,0},{0,0,-1},{-1,0,0},{0,-1,0},{0,1,0}};
    public SoyuzStructureRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(SoyuzStructureEntity core,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        poses.pushPose();poses.translate(1,1,0);
        for(var cell:SoyuzLauncherLayout.MATERIALS){
            var p=cell.offset();String texture=switch(cell.material()){case LAUNCHER->"struct_launcher";case SCAFFOLD->"struct_scaffold";case CONCRETE->"concrete";};
            var out=buffers.getBuffer(RenderType.entityTranslucent(SoyuzLauncherContent.id("textures/block/"+texture+".png")));
            poses.pushPose();poses.translate(p.getX(),p.getY(),p.getZ());poses.mulPose(Axis.ZP.rotationDegrees(180));
            for(int f=0;f<FACES.length;f++)for(var v:FACES[f])out.addVertex(poses.last(),v[0],v[1],v[2]).setColor(1F,1F,1F,.75F).setUv(v[3],v[4]).setOverlay(overlay).setLight(light).setNormal(poses.last(),NORMALS[f][0],NORMALS[f][1],NORMALS[f][2]);
            poses.popPose();
        }
        poses.popPose();
    }
    @Override public boolean shouldRenderOffScreen(SoyuzStructureEntity core){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(SoyuzStructureEntity core){var p=core.getBlockPos();return new AABB(p.getX()-7,p.getY(),p.getZ()-9,p.getX()+8,p.getY()+53,p.getZ()+11);}
}
