package com.hbm.client.door.family;

import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.door.family.GenericDoorFamilyContent;
import com.hbm.door.generic.GenericDoorBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;

/** Actual ItemRenderLibraryDoors per-declaration inventory/common matrices and whole original closed meshes, cycling original skins. */
public final class GenericDoorFamilyItemRenderer extends BlockEntityWithoutLevelRenderer {
    public GenericDoorFamilyItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        if(!GenericDoorFamilyContent.containsItem(stack.getItem())||!(stack.getItem() instanceof BlockItem item)||!(item.getBlock() instanceof GenericDoorBlock block))return;
        pose.pushPose();try{
            LegacyItemTransforms.held(pose,context);
            if(context==ItemDisplayContext.GUI){pose.translate(0,1,.5);pose.scale(1F/16,-1F/16,1F/16);pose.translate(8,10,0);pose.mulPose(Axis.XP.rotationDegrees(-30));pose.mulPose(Axis.YP.rotationDegrees(45));pose.scale(-1,-1,-1);pose.translate(0,GenericDoorFamilyModels.inventoryY(block.declaration),0);float scale=GenericDoorFamilyModels.inventoryScale(block.declaration);pose.scale(scale,scale,scale);}
            else{if(context==ItemDisplayContext.GROUND||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.NONE){pose.translate(.5,0,.5);pose.scale(1.5F,1.5F,1.5F);}else pose.translate(.5,.25,0);pose.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)pose.mulPose(Axis.YP.rotationDegrees(90));}
            GenericDoorFamilyModels.item(block.declaration,pose,buffers,light,overlay);
        }finally{pose.popPose();}
    }
}
