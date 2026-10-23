package com.hbm.client.door.vault;

import com.hbm.client.animation.bus.BusAnimationSequence;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import com.hbm.client.model.*;
import com.hbm.door.generic.GenericDoorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;

/** Exact original PULL/SLIDE buses,4.25diameter roll, actual source raw skin switch and separate number-label material. */
public final class VaultDoorModel {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/pheodoors/vault_door.obj");
    private static final String[] LABELS={"101","87","106","81","111","2","99"};
    private static final BusAnimationSequence OPEN_PULL=new BusAnimationSequence().setPos(0,0,0).addPos(0,0,1,2000,IType.SIN_FULL),OPEN_SLIDE=new BusAnimationSequence().addPos(0,0,0,2000).addPos(1,0,0,4000),CLOSE_PULL=new BusAnimationSequence().setPos(0,0,1).addPos(0,0,1,4000).addPos(0,0,0,2000,IType.SIN_FULL),CLOSE_SLIDE=new BusAnimationSequence().setPos(1,0,0).addPos(0,0,0,4000);
    private static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    private static int sourceSkin(int skin){return skin>=1&&skin<=6?skin:0;}
    private static ResourceLocation body(int skin){int choice=sourceSkin(skin);return ResourceLocation.parse("hbm:textures/models/pheodoors/vault/vault_door_"+(choice>=5?"s":choice>=3?"4":"3")+".png");}
    private static ResourceLocation label(int skin){return ResourceLocation.parse("hbm:textures/models/pheodoors/vault/label_"+LABELS[sourceSkin(skin)]+".png");}
    private static void part(String name,ResourceLocation texture,PoseStack pose,MultiBufferSource buffers,int light,int overlay,boolean inventory){LegacyObjRenderer.renderOnly(mesh(),pose,buffers.getBuffer(RenderType.entityCutout(texture)),light,overlay,-1,inventory?LegacyObjRenderer.Convention.HFR_FLAT:LegacyObjRenderer.Convention.HFR,name);}
    public static void world(GenericDoorBlockEntity door,PoseStack pose,MultiBufferSource buffers,int light,int overlay){double pull=door.state==GenericDoorBlockEntity.OPEN?1:0,slide=door.state==GenericDoorBlockEntity.OPEN?1:0;if(door.animStartTime!=0){int millis=(int)Math.min(Integer.MAX_VALUE,Math.max(0,System.currentTimeMillis()-door.animStartTime));boolean opening=door.state==GenericDoorBlockEntity.OPENING||door.state==GenericDoorBlockEntity.OPEN;pull=(opening?OPEN_PULL:CLOSE_PULL).getTransformation(millis)[2];slide=(opening?OPEN_SLIDE:CLOSE_SLIDE).getTransformation(millis)[0];}double circumference=4.25D*Math.PI;slide*=5D;double roll=360D*slide/circumference;var body=body(door.skinIndex);part("Frame",body,pose,buffers,light,overlay,false);pose.translate(-pull,0,0);pose.translate(0,0,slide);pose.translate(0,2.5,0);pose.mulPose(Axis.XP.rotationDegrees((float)roll));pose.translate(0,-2.5,0);part("Door",body,pose,buffers,light,overlay,false);part("Label",label(door.skinIndex),pose,buffers,light,overlay,false);}
    public static void item(PoseStack pose,MultiBufferSource buffers,int light,int overlay){int skin=(int)((System.currentTimeMillis()%(7*1000))/1000);pose.translate(0,-1,0);part("Door",body(skin),pose,buffers,light,overlay,true);part("Label",label(skin),pose,buffers,light,overlay,true);}
    private VaultDoorModel(){}
}
