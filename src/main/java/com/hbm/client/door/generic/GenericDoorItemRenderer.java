package com.hbm.client.door.generic;
import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.door.generic.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
/** Original ItemRenderLibraryDoors inventory/common transforms inside actual ItemRenderBase caller. */
public final class GenericDoorItemRenderer extends BlockEntityWithoutLevelRenderer {
    public GenericDoorItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        if(!(stack.getItem()instanceof BlockItem item)||!(item.getBlock()instanceof GenericDoorBlock block))return;
        poses.pushPose();LegacyItemTransforms.held(poses,context);
        if(context==ItemDisplayContext.GUI){poses.translate(0,1,.5);poses.scale(1F/16,-1F/16,1F/16);poses.translate(8,10,0);poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);poses.translate(0,block.declaration.siloHatch()?-2:-4,0);float scale=block.declaration==GenericDoorDeclaration.SILO_HATCH?2:block.declaration==GenericDoorDeclaration.SILO_HATCH_LARGE?1.5F:4;poses.scale(scale,scale,scale);}
        else {if(context==ItemDisplayContext.GROUND||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.NONE){poses.translate(.5,0,.5);poses.scale(1.5F,1.5F,1.5F);}else poses.translate(.5,.25,0);poses.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)poses.mulPose(Axis.YP.rotationDegrees(90));}
        if(block.declaration.siloHatch()){if(block.declaration==GenericDoorDeclaration.SILO_HATCH_LARGE)poses.translate(1,0,0);poses.mulPose(Axis.YP.rotationDegrees(90));GenericDoorRenderer.silo(block.declaration,poses,buffers,light,overlay,60,true);}
        else {poses.scale(.9F,.9F,.9F);poses.translate(0,.3,0);GenericDoorRenderer.cargo(poses,buffers,light,overlay,0,0,true);}
        poses.popPose();
    }
}
