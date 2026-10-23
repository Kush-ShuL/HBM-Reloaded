package com.hbm.client.door.sourceblast;

import com.hbm.client.model.*;
import com.hbm.door.sourceblast.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Four original Forge OBJ files/textures and the source wall-clock telescope; no generic-door model substitution. */
public final class SourceBlastDoorRenderer implements BlockEntityRenderer<SourceBlastDoorEntity> {
    public SourceBlastDoorRenderer(BlockEntityRendererProvider.Context context){}
    private static void part(String part,PoseStack poses,MultiBufferSource buffers,int light,int overlay,boolean world){
        var model=ResourceLocation.parse("hbm:models/blast_door_"+part+".obj");var texture=ResourceLocation.parse("hbm:textures/models/blast_door_"+part+".png");
        LegacyObjRenderer.renderAll(LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),model),poses,buffers.getBuffer(world?RenderType.entityCutoutNoCull(texture):RenderType.entityCutout(texture)),light,overlay,-1,LegacyObjRenderer.Convention.FORGE);
    }
    private static double extend(long time){return Math.max(Math.min(time,5000L)/5000D*5D,0D);}
    @Override public void render(SourceBlastDoorEntity door,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(180));if(door.getBlockState().getValue(SourceBlastDoorBlock.META)==2)poses.mulPose(Axis.YP.rotationDegrees(90));
        double extension=door.state==0?extend(5000):door.state==2?0:door.isOpening?extend(door.sysTime+5000-System.currentTimeMillis()):extend(System.currentTimeMillis()-door.sysTime);
        part("base",poses,buffers,light,overlay,true);poses.translate(0,3,0);part("block",poses,buffers,light,overlay,true);poses.translate(0,-extension+2,0);part("tooth",poses,buffers,light,overlay,true);
        if(extension>1)part("slider",poses,buffers,light,overlay,true);if(extension>2){poses.translate(0,1,0);part("slider",poses,buffers,light,overlay,true);}if(extension>3){poses.translate(0,1,0);part("slider",poses,buffers,light,overlay,true);}if(extension>4){poses.translate(0,1,0);part("slider",poses,buffers,light,overlay,true);}poses.popPose();
    }
    static void item(PoseStack poses,MultiBufferSource buffers,int light,int overlay){for(var part:new String[]{"base","tooth","slider","block"})part(part,poses,buffers,light,overlay,false);}
    @Override public boolean shouldRenderOffScreen(SourceBlastDoorEntity door){return true;}
    @Override public AABB getRenderBoundingBox(SourceBlastDoorEntity door){return AABB.INFINITE;}
    @Override public int getViewDistance(){return 256;}
}
