package com.hbm.client.generator.steamengine;
import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.hazard.HbmHazards;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
public final class SteamEngineItemRenderer extends BlockEntityWithoutLevelRenderer {
    public SteamEngineItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();if(LegacyItemTransforms.isHeld(context))LegacyItemTransforms.held(poses,context);else poses.translate(.5,.5,.5);if(context==ItemDisplayContext.GUI){poses.scale(1F/16,-1F/16,1F/16);poses.translate(0,2,0);poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);poses.mulPose(Axis.YN.rotationDegrees(90));poses.translate(0,-1.5,0);poses.scale(2,2,2);}else{boolean entity=context==ItemDisplayContext.GROUND||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.NONE;if(entity)poses.scale(1.5F,1.5F,1.5F);else poses.translate(.5,.25,0);poses.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)poses.mulPose(Axis.YP.rotationDegrees(90));}poses.mulPose(Axis.YP.rotationDegrees(90));SteamEngineRenderer.common(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)!=1?System.currentTimeMillis()%3600*.1D:0,poses,buffers,light,overlay);poses.popPose();}
}
