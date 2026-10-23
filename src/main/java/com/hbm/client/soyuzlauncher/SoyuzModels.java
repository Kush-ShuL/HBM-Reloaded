package com.hbm.client.soyuzlauncher;

import com.hbm.client.model.*;
import com.hbm.machine.soyuzlauncher.SoyuzLauncherContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;

/** Source SoyuzPronter and SoyuzLauncherPronter; original authored meshes, texture/group bindings and pivots. */
public final class SoyuzModels {
    private static final String[] SKINS={"soyuz","soyuz_luna","soyuz_authentic"};
    public static WavefrontMesh mesh(String path){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),SoyuzLauncherContent.id(path));}
    private static void launcherPart(String name,String texture,PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(mesh("models/launch_table/soyuz_launcher_"+name+".obj"),poses,buffers.getBuffer(RenderType.entityCutoutNoCull(SoyuzLauncherContent.id("textures/models/soyuz_launcher/launcher_"+texture+".png"))),light,overlay,-1,LegacyObjRenderer.Convention.HFR_FLAT);}
    public static void launcher(double rotation,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        launcherPart("legs","leg",poses,buffers,light,overlay);launcherPart("table","table",poses,buffers,light,overlay);launcherPart("tower_base","tower_base",poses,buffers,light,overlay);
        poses.pushPose();poses.translate(0,5.5,5.5);poses.mulPose(Axis.XP.rotationDegrees((float)rotation));poses.translate(0,-5.5,-5.5);launcherPart("tower","tower",poses,buffers,light,overlay);poses.popPose();
        launcherPart("support_base","support_base",poses,buffers,light,overlay);
        poses.pushPose();poses.translate(0,5.5,-6.5);poses.mulPose(Axis.XN.rotationDegrees((float)rotation));poses.translate(0,-5.5,6.5);launcherPart("support","support",poses,buffers,light,overlay);poses.popPose();
    }
    private static void part(String skin,String texture,String group,PoseStack poses,MultiBufferSource buffers,int light,int overlay){var tex=SoyuzLauncherContent.id(texture.equals("memento")?"textures/items/polaroid_memento.png":"textures/models/"+skin+"/"+texture+".png");LegacyObjRenderer.renderOnly(mesh("models/soyuz.obj"),poses,buffers.getBuffer(RenderType.entityCutout(tex)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,group);}
    public static void rocket(int type,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        if(type<0||type>=SKINS.length)return;String skin=SKINS[type];
        String[][] groups={{"engineblock","EngineBlock"},{"bottomstage","BottomStage"},{"topstage","TopStage"},{"payload","Payload"},{"memento","Memento"},{"payloadblocks","PayloadBlocks"},{"les","LES"},{"lesthrusters","LESThrusters"},{"mainengines","MainEngines"},{"sideengines","SideEngines"}};
        for(var group:groups)part(skin,group[0],group[1],poses,buffers,light,overlay);
        // Source groups are texture-batched in this exact order, not per-booster interleaved.
        for(int i=0;i<4;i++)part(skin,"booster","Booster.00"+i,poses,buffers,light,overlay);
        for(int i=0;i<4;i++)part(skin,"mainengines","BoosterEngines.00"+i,poses,buffers,light,overlay);
        for(int i=0;i<4;i++)part(skin,"boosterside","BoosterSide.00"+i,poses,buffers,light,overlay);
    }
    private SoyuzModels(){}
}
