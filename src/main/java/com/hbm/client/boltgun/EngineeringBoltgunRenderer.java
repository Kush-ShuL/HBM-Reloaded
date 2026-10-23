package com.hbm.client.boltgun;

import com.hbm.client.animation.bus.HbmAnimations;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.*;

/** Original engineering ItemRenderBoltgun, including Barrel-only first-person RECOIL and all original caller matrices. */
public final class EngineeringBoltgunRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/weapons/boltgun.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/weapons/boltgun.png");
    public EngineeringBoltgunRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    private void part(PoseStack pose,MultiBufferSource buffers,int light,int overlay,String part){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);LegacyObjRenderer.renderOnly(mesh,pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,part);}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack pose,MultiBufferSource buffers,int light,int overlay){pose.pushPose();LegacyItemTransforms.held(pose,context);switch(context){case FIRST_PERSON_LEFT_HAND,FIRST_PERSON_RIGHT_HAND->{pose.translate(.5,.35,-.25F);pose.mulPose(Axis.ZP.rotationDegrees(15));pose.mulPose(Axis.YP.rotationDegrees(80));pose.scale(.15F,.15F,.15F);var player=Minecraft.getInstance().player;boolean left=context==ItemDisplayContext.FIRST_PERSON_LEFT_HAND;var hand=player!=null&&left==(player.getMainArm()==HumanoidArm.LEFT)?InteractionHand.MAIN_HAND:InteractionHand.OFF_HAND;try(var scope=HbmAnimations.hand(hand)){double[] animation=HbmAnimations.getRelevantTransformation("RECOIL");pose.pushPose();pose.translate(0,0,-animation[0]);if(animation[0]!=0&&player!=null)player.swinging=false;part(pose,buffers,light,overlay,"Barrel");pose.popPose();}}case THIRD_PERSON_LEFT_HAND,THIRD_PERSON_RIGHT_HAND->{pose.scale(.25F,.25F,.25F);pose.mulPose(Axis.YP.rotationDegrees(10));pose.mulPose(Axis.ZP.rotationDegrees(10));pose.mulPose(Axis.XP.rotationDegrees(10));pose.translate(1.5,-.25,1);}case GROUND,FIXED->{pose.translate(.5,context==ItemDisplayContext.GROUND?0:.5,.5);pose.scale(.1F,.1F,.1F);}case GUI->{pose.translate(0,1,.5);pose.scale(1F/16,-1F/16,1F/16);pose.translate(7,10,0);pose.mulPose(Axis.YP.rotationDegrees(-90));pose.mulPose(Axis.XP.rotationDegrees(-135));pose.scale(1.75F,1.75F,-1.75F);}default->{}}part(pose,buffers,light,overlay,"Gun");if(!context.firstPerson())part(pose,buffers,light,overlay,"Barrel");pose.popPose();}
}
