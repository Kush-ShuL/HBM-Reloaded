package com.hbm.client.railway.track;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.hbm.railway.track.SourceRailKind;
/** Exact source ISBRH inventory transforms, before authored OBJ vertices. */
final class SourceRailItemMatrices {static void apply(SourceRailKind kind,PoseStack pose){switch(kind){
case NARROW_STRAIGHT -> {pose.translate(0, -0.0625, 0);}
case NARROW_CURVE -> {pose.scale(0.2F,0.2F,0.2F);pose.translate(2.5, -0.0625, -1.5);}
case STRAIGHT -> {pose.translate(0, -0.0625, 0);pose.mulPose(Axis.YP.rotationDegrees(90F));pose.scale(0.3F,0.3F,0.3F);}
case SHORT -> {pose.translate(0, -0.0625, 0);pose.mulPose(Axis.YP.rotationDegrees(90F));pose.scale(0.7F,0.7F,0.7F);}
case CURVE -> {pose.scale(0.3F,0.3F,0.3F);pose.mulPose(Axis.YP.rotationDegrees(45F));pose.mulPose(Axis.XP.rotationDegrees(60F));pose.translate(2, 0, 2);}
case CURVE7 -> {pose.scale(0.225F,0.225F,0.225F);pose.mulPose(Axis.YP.rotationDegrees(45F));pose.mulPose(Axis.XP.rotationDegrees(60F));pose.translate(3, 0, 3);}
case CURVE9 -> {pose.scale(0.175F,0.175F,0.175F);pose.mulPose(Axis.YP.rotationDegrees(45F));pose.mulPose(Axis.XP.rotationDegrees(60F));pose.translate(4, 0, 4);}
case RAMP -> {pose.translate(0, -0.0625, 0);pose.mulPose(Axis.YP.rotationDegrees(90F));pose.scale(0.3F,0.3F,0.3F);}
case BUFFER -> {pose.translate(0, -0.0625, 0);pose.mulPose(Axis.YP.rotationDegrees(90F));pose.scale(0.3F,0.3F,0.3F);}
case SWITCH -> {pose.translate(0, -0.0625, -0.1875);pose.mulPose(Axis.YP.rotationDegrees(90F));pose.scale(0.1F,0.1F,0.1F);}
case SWITCH_FLIPPED -> {pose.translate(0, -0.0625, 0);pose.mulPose(Axis.YP.rotationDegrees(90F));pose.scale(0.1F,0.1F,0.1F);}
}}private SourceRailItemMatrices(){}}
