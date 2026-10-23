package com.hbm.client.bobble;
import com.hbm.block.bobble.BobbleContent;
import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
/** Exact source ItemRenderBase + bobble inventory translate(0,-3.5,0), scale10 and common scale .5. */
public final class BobbleItemRenderer extends BlockEntityWithoutLevelRenderer {
    public BobbleItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        poses.pushPose();if(LegacyItemTransforms.isHeld(context))LegacyItemTransforms.held(poses,context);else poses.translate(.5,.5,.5);
        if(context==ItemDisplayContext.GUI){poses.scale(1F/16,-1F/16,1F/16);poses.translate(0,2,0);poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);poses.translate(0,-3.5,0);poses.scale(10,10,10);}
        else{boolean entity=context==ItemDisplayContext.GROUND||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.NONE;if(entity)poses.scale(1.5F,1.5F,1.5F);else poses.translate(.5,.25,0);poses.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)poses.mulPose(Axis.YP.rotationDegrees(90));}
        // Source ItemRenderLibrary uses RenderBobble.instance, a separate renderer from ClientProxy's world renderer;
        // its time field remains zero. Retain those exact static item poses/glow rather than adding new animations.
        poses.scale(.5F,.5F,.5F);BobbleRenderer.draw(BobbleContent.type(stack),poses,buffers,light,overlay,0);poses.popPose();
    }
}
