package com.hbm.machine.reactor.client;
import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.machine.reactor.ReactorContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
/** Source ItemRenderBase around exact per-machine inventory transforms. */
public final class ReactorItemRenderer extends BlockEntityWithoutLevelRenderer {
    public ReactorItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        boolean breeder=stack.is(ReactorContent.BREEDING_ITEM.get());poses.pushPose();LegacyItemTransforms.held(poses,context);
        if(context==ItemDisplayContext.GUI){poses.translate(0,1,.5);poses.scale(1F/16,-1F/16,1F/16);poses.translate(8,10,0);poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);poses.translate(0,breeder?-4.5:-4,0);float scale=breeder?4.5F:4F;poses.scale(scale,scale,scale);}
        else{if(context==ItemDisplayContext.GROUND){poses.translate(.5,0,.5);poses.scale(1.5F,1.5F,1.5F);}else poses.translate(.5,.25,0);poses.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)poses.mulPose(Axis.YP.rotationDegrees(90));}
        if(breeder)BreedingReactorRenderer.inventory(poses,buffers,light,overlay);else ResearchReactorRenderer.inventory(poses,buffers,light,overlay);poses.popPose();
    }
}
