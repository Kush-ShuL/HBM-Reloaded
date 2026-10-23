package com.hbm.client.missile.completion;
import com.hbm.client.model.LegacyItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
/** Original ItemRenderMissileGeneric.TYPE_NUCLEAR: GUI1.375/offset1.5, wall-clock turn and actual held/entity matrices. */
public final class MissileItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final com.hbm.missile.completion.MissileContent.Kind kind;
    public MissileItemRenderer(com.hbm.missile.completion.MissileContent.Kind kind){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());this.kind=kind;}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        poses.pushPose();if(LegacyItemTransforms.isHeld(context))LegacyItemTransforms.held(poses,context);else poses.translate(.5,.5,.5);
        if(context==ItemDisplayContext.GUI){poses.scale(1F/16,-1F/16,1F/16);poses.translate(-8,-8,0);poses.scale(kind.guiScale(),kind.guiScale(),kind.guiScale());poses.mulPose(Axis.ZP.rotationDegrees(135));poses.mulPose(Axis.YP.rotationDegrees(System.currentTimeMillis()/15%360));poses.translate(0,-16+kind.guiOffset(),0);}
        else if(context==ItemDisplayContext.FIRST_PERSON_LEFT_HAND||context==ItemDisplayContext.FIRST_PERSON_RIGHT_HAND){poses.translate(.5,.25,0);poses.scale(.1F,.1F,.1F);}
        else if(context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){poses.translate(.5,-.25,0);poses.scale(.15F,.15F,.15F);}
        else poses.scale(.15F,.15F,.15F);
        MissileRenderer.model(kind,poses,buffers,light,overlay,false);poses.popPose();
    }
}
