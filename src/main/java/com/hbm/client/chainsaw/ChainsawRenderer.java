package com.hbm.client.chainsaw;

import com.hbm.client.animation.bus.HbmAnimations;
import com.hbm.client.model.*;
import com.hbm.client.weapon.pepperbox.GunHandTransforms;
import com.hbm.item.toolability.ItemToolAbilityFueled;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.*;

/** Actual ItemRenderChainsaw matrices/UV and20 moving teeth on its original OBJ. No icon replacement or gun dispatch. */
public final class ChainsawRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/weapons/chainsaw.obj");
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/models/weapons/chainsaw.png");
    public ChainsawRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels()); }
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        poses.pushPose();try(var reflection=GunHandTransforms.heldFull3D(poses,context)) {
            if(context.firstPerson()) {
                LegacyItemTransforms.heldFull3D(poses,context);
                var player=Minecraft.getInstance().player;
                boolean left=context==ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
                var hand=player==null||left==(player.getMainArm()==HumanoidArm.LEFT)?InteractionHand.MAIN_HAND:InteractionHand.OFF_HAND;
                try(var animation=HbmAnimations.hand(hand)) {
                    if(player!=null&&player.swingingArm==hand)player.swinging=false;
                    poses.translate(.5,.25,-.25F);poses.mulPose(Axis.ZP.rotationDegrees(45));poses.mulPose(Axis.YP.rotationDegrees(80));poses.scale(.35F,.35F,.35F);
                    if(player==null||!player.isBlocking()) {
                        var rotation=HbmAnimations.getRelevantTransformation("SWING_ROT");var translation=HbmAnimations.getRelevantTransformation("SWING_TRANS");
                        poses.translate(translation[0],translation[1],translation[2]);poses.mulPose(Axis.ZP.rotationDegrees((float)rotation[2]));poses.mulPose(Axis.YP.rotationDegrees((float)rotation[1]));poses.mulPose(Axis.XP.rotationDegrees((float)rotation[0]));
                    }
                    body(stack,poses,buffers,light,overlay);
                }
            } else {
                if(context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
                    poses.scale(-.375F,-.375F,-.375F);poses.mulPose(Axis.YP.rotationDegrees(85));poses.mulPose(Axis.XP.rotationDegrees(135));poses.translate(-.125,-2,1.75);
                } else if(context==ItemDisplayContext.GUI) {
                    poses.translate(0,1,.3125);poses.scale(1F/16,-1F/16,1F/16);
                    poses.translate(8,10,0);poses.mulPose(Axis.YP.rotationDegrees(-90));poses.mulPose(Axis.XP.rotationDegrees(-135));poses.scale(4,4,-4);
                } else { poses.translate(.5,context==ItemDisplayContext.GROUND?.25:.5,.5);poses.scale(.5F,.5F,.5F); }
                body(stack,poses,buffers,light,overlay);
            }
        }finally { poses.popPose(); }
    }
    private static void body(ItemStack stack,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
        var vertices=buffers.getBuffer(RenderType.entityCutout(TEXTURE));
        LegacyObjRenderer.renderOnly(mesh,poses,vertices,light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Saw");
        for(int i=0;i<20;i++) {
            double run=((ItemToolAbilityFueled)stack.getItem()).canOperate(stack)?System.currentTimeMillis()%100D*.25D/100D:.0625D;
            double forward=i*.25+run-2.0625;
            poses.pushPose();poses.translate(0,0,1.9375);poses.translate(0,.375,.5625);
            double angle=Mth.clamp(forward,0,.25*Math.PI);poses.mulPose(Axis.XP.rotationDegrees((float)(angle*180D/(Math.PI*.25))));poses.translate(0,-.375,-.5625);
            if(forward<0)poses.translate(0,0,forward);if(forward>Math.PI*.25)poses.translate(0,0,forward-Math.PI*.25);
            LegacyObjRenderer.renderOnly(mesh,poses,vertices,light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Tooth");poses.popPose();
        }
    }
}
