package com.hbm.client.meteorstatue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

/** Ordered GL calls from RenderDecoBlockAlt, including the cumulative attachment transforms. */
final class MeteorStatueTransforms {
 private static final float G=.0625F,Q=G*2+.0625F/3;
 static void body(PoseStack pose,int meta){pose.translate(.5F,1.5F,.5F);pose.mulPose(Axis.ZP.rotationDegrees(180));pose.mulPose(Axis.YP.rotationDegrees(switch(meta){case 4->90;case 2->180;case 5->270;default->0;}));}
 static void toWatch(PoseStack pose){pose.translate(0,-2*G,Q);pose.mulPose(Axis.ZP.rotationDegrees(180));}
 static void toGun(PoseStack pose){pose.translate(0,2*G,-Q);pose.mulPose(Axis.ZP.rotationDegrees(180));pose.mulPose(Axis.YP.rotationDegrees(90));pose.scale(.5F,.5F,.5F);pose.translate(-G*20,G*4,G*11);pose.mulPose(Axis.ZP.rotationDegrees(-20));}
 /** Official 1.7 RenderItem.renderInFrame: scale .5128205, y-.05, sprite centre y+.25,z+.0109375.
  * Native generated FIXED already rotates Y180 and centres its extrusion, leaving this exact correction. */
 static void watchItem(PoseStack pose){pose.scale(.5128205F,.5128205F,.5128205F);pose.translate(0,.2F,-.0109375F);}
 private MeteorStatueTransforms(){}
}
