package com.hbm.client.structure.industrial44;
import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.client.model.legacy.generated.ModelSteelRoof;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
/** ClientProxy614 ItemRenderDecoBlock: original first/third/dropped matrices; GUI remains the source flat icon. */
public final class SteelRoofItemRenderer extends BlockEntityWithoutLevelRenderer {
 private final ModelSteelRoof model=new ModelSteelRoof();public SteelRoofItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();if(LegacyItemTransforms.isHeld(context))LegacyItemTransforms.held(poses,context);else poses.translate(.5,.5,.5);if(context.firstPerson()){poses.mulPose(Axis.ZP.rotationDegrees(-135));poses.translate(-.6,-.6,-.1);poses.scale(.5F,.5F,.5F);}else if(context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){poses.scale(.5F,.5F,.5F);poses.translate(.8,-.3,.2);}else{poses.scale(.5F,.5F,.5F);poses.mulPose(Axis.XP.rotationDegrees(180));poses.translate(0,-1,0);}model.renderToBuffer(poses,buffers.getBuffer(RenderType.entityCutoutNoCull(SteelRoofRenderer.TEXTURE)),light,overlay,-1,.0625F);poses.popPose();}
}
