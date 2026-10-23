package com.hbm.client.reactor.watz;

import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.machine.reactor.watz.WatzContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;

/** Exact original ItemRenderBase + Watz library(-1,2scale)/Pump provider(-1.5,5scale) inventory matrices. */
public final class WatzItemRenderer extends BlockEntityWithoutLevelRenderer {
    public WatzItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack pose,MultiBufferSource buffers,int light,int overlay){boolean pump=stack.is(WatzContent.PUMP.get().asItem());pose.pushPose();LegacyItemTransforms.held(pose,context);if(context==ItemDisplayContext.GUI){pose.translate(0,1,.5);pose.scale(1F/16,-1F/16,1F/16);pose.translate(8,10,0);pose.mulPose(Axis.XP.rotationDegrees(-30));pose.mulPose(Axis.YP.rotationDegrees(45));pose.scale(-1,-1,-1);pose.translate(0,pump?-1.5:-1,0);float scale=pump?5:2;pose.scale(scale,scale,scale);}else{if(context==ItemDisplayContext.GROUND){pose.translate(.5,0,.5);pose.scale(1.5F,1.5F,1.5F);}else pose.translate(.5,.25,0);pose.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)pose.mulPose(Axis.YP.rotationDegrees(90));}if(pump)WatzPumpRenderer.draw(pose,buffers,light,overlay);else WatzRenderer.draw(pose,buffers,light,overlay);pose.popPose();}
}
