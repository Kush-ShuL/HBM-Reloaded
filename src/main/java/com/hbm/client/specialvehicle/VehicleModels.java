package com.hbm.client.specialvehicle;

import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;

/** Source AdvancedModelLoader uses Forge flat face normals and its original UV inset. */
public final class VehicleModels {
    static final ResourceLocation BOXCAR=ResourceLocation.parse("hbm:models/boxcar.obj"),BOAT=ResourceLocation.parse("hbm:models/duchessgambit.obj");
    static final ResourceLocation BOXCAR_TEXTURE=ResourceLocation.parse("hbm:textures/models/boxcar.png"),BOAT_TEXTURE=ResourceLocation.parse("hbm:textures/models/duchessgambit.png");
    static WavefrontMesh mesh(boolean boat){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),boat?BOAT:BOXCAR);}
    static void draw(boolean boat,PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(mesh(boat),poses,buffers.getBuffer(RenderType.entityCutout(boat?BOAT_TEXTURE:BOXCAR_TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.FORGE);}
    static void blockPose(PoseStack poses,boolean boat,int meta){
        poses.translate(.5,1.5,.5);poses.mulPose(Axis.ZP.rotationDegrees(180));
        if(boat){poses.mulPose(Axis.ZP.rotationDegrees(180));poses.translate(0,0,-1.5);poses.translate(0,.5,0);}
        else {poses.mulPose(Axis.ZP.rotationDegrees(180));poses.translate(0,-1.5,0);switch(meta){case 4->{}case 2->poses.mulPose(Axis.YP.rotationDegrees(270));case 5->poses.mulPose(Axis.YP.rotationDegrees(180));case 3->poses.mulPose(Axis.YP.rotationDegrees(90));default->{poses.mulPose(Axis.ZP.rotationDegrees(180));poses.mulPose(Axis.XP.rotationDegrees(90));poses.translate(0,-1.5,0);}}}
    }
    private VehicleModels(){}
}
