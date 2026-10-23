package com.hbm.client.door.fire;

import com.hbm.client.model.*;
import com.hbm.door.generic.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** Original RenderFireDoor: fixed frame, linear DOOR bus *2.75, source five authored skins. */
public final class FireDoorModel {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/pheodoors/fire_door.obj");
    private static final String[] SKINS={"fire_door","fire_door_black","fire_door_orange","fire_door_yellow","fire_door_trefoil"};
    public static ResourceLocation texture(int skin){return ResourceLocation.parse("hbm:textures/models/pheodoors/"+SKINS[Math.abs(skin)%SKINS.length]+".png");}
    public static void world(GenericDoorBlockEntity door,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        double raise=door.state==GenericDoorBlockEntity.OPEN?2.75:0;
        if(door.state==GenericDoorBlockEntity.OPENING)raise=Mth.clamp((System.currentTimeMillis()-door.animStartTime)/8000D,0,1)*2.75;
        else if(door.state==GenericDoorBlockEntity.CLOSING)raise=Mth.clamp(1-(System.currentTimeMillis()-door.animStartTime)/8000D,0,1)*2.75;
        poses.pushPose();poses.mulPose(Axis.YP.rotationDegrees(90));poses.translate(-.5,0,0);
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);var consumer=buffers.getBuffer(RenderType.entityCutout(texture(door.skinIndex)));
        LegacyObjRenderer.renderOnly(mesh,poses,consumer,light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Frame");
        poses.translate(0,Mth.clamp(raise,0,2.75),0);LegacyObjRenderer.renderOnly(mesh,poses,consumer,light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Door");poses.popPose();
    }
    public static void item(PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        int skin=(int)((System.currentTimeMillis()%(SKINS.length*1000))/1000);var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
        poses.mulPose(Axis.YP.rotationDegrees(90));LegacyObjRenderer.renderAll(mesh,poses,buffers.getBuffer(RenderType.entityCutout(texture(skin))),light,overlay,-1,LegacyObjRenderer.Convention.HFR);
    }
    private FireDoorModel(){}
}
