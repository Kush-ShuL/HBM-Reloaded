package com.hbm.client.generator.steamengine;
import com.hbm.client.model.*;
import com.hbm.machine.generator.steamengine.LargeGearItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
/** Actual ItemRenderLibrary gear_large Cog geometry, Forge normals/UV and original GUI spinning matrix. */
public final class LargeGearRenderer extends BlockEntityWithoutLevelRenderer {
    public LargeGearRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();if(LegacyItemTransforms.isHeld(context))LegacyItemTransforms.held(poses,context);else poses.translate(.5,.5,.5);if(context==ItemDisplayContext.GUI){poses.scale(1F/16,-1F/16,1F/16);poses.translate(0,2,0);poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(45));poses.scale(-1,-1,-1);poses.translate(0,-7,0);poses.scale(6,6,6);poses.mulPose(Axis.YN.rotationDegrees(45));poses.mulPose(Axis.XP.rotationDegrees(30));poses.translate(0,1.375,0);poses.mulPose(Axis.ZP.rotationDegrees(System.currentTimeMillis()%3600*.1F));poses.translate(0,-1.375,0);}else{boolean entity=context==ItemDisplayContext.GROUND||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.NONE;if(entity)poses.scale(1.5F,1.5F,1.5F);else poses.translate(.5,.25,0);poses.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)poses.mulPose(Axis.YP.rotationDegrees(90));}poses.translate(0,0,-.875);var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/machines/stirling.obj"));var texture=ResourceLocation.parse("hbm:textures/models/machines/"+(LargeGearItem.metadata(stack)==0?"stirling":"stirling_steel")+".png");LegacyObjRenderer.renderOnly(model,poses,buffers.getBuffer(RenderType.entityCutout(texture)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.FORGE,"Cog");poses.popPose();}
}
