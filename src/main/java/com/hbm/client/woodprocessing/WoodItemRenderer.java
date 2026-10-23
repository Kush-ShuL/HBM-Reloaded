package com.hbm.client.woodprocessing;
import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.machine.woodprocessing.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
/** Both source IItemRendererProvider paths, with each original inventory and common matrix intact. */
public final class WoodItemRenderer extends BlockEntityWithoutLevelRenderer {
    public WoodItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack pose,MultiBufferSource buffers,int light,int overlay){boolean sawmill=stack.is(WoodProcessingContent.SAWMILL_ITEM.get());pose.pushPose();if(LegacyItemTransforms.isHeld(context))LegacyItemTransforms.held(pose,context);else pose.translate(.5,.5,.5);
        if(context==ItemDisplayContext.GUI){pose.scale(1F/16,-1F/16,1F/16);pose.translate(0,2,0);pose.mulPose(Axis.XP.rotationDegrees(-30));pose.mulPose(Axis.YP.rotationDegrees(45));pose.scale(-1,-1,-1);if(sawmill){pose.translate(0,-1.5,0);pose.scale(3.25F,3.25F,3.25F);}else{pose.translate(0,-3.5,-3);pose.scale(5,5,5);}}
        else{boolean entity=context==ItemDisplayContext.GROUND||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.NONE;if(entity)pose.scale(1.5F,1.5F,1.5F);else pose.translate(.5,.25,0);pose.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)pose.mulPose(Axis.YP.rotationDegrees(90));}
        if(sawmill){pose.mulPose(Axis.YP.rotationDegrees(90));boolean blade=!WoodMachineItem.broken(stack);SawmillRenderer.common(blade?System.currentTimeMillis()%3600*.1F:0,blade,pose,buffers,light,overlay);}else{pose.scale(.5F,.5F,.5F);pose.mulPose(Axis.YN.rotationDegrees(90));AutosawRenderer.common(0,80,System.currentTimeMillis()%3600*.1D,0,pose,buffers,light,overlay);}pose.popPose();
    }
}
