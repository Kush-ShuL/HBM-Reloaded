package com.hbm.client.devtesters;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
/** Source ItemRenderLibrary29: the item is actual Soyuz module4parts, not the placed dropship or soyuz_lander capsule. */
public final class ObjTesterItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/soyuz_module.obj");
    public ObjTesterItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        pose.pushPose();if(LegacyItemTransforms.isHeld(context))LegacyItemTransforms.held(pose,context);else pose.translate(.5,.5,.5);
        if(context==ItemDisplayContext.GUI){pose.scale(1F/16,-1F/16,1F/16);pose.translate(0,2,0);pose.mulPose(Axis.XP.rotationDegrees(-30));pose.mulPose(Axis.YP.rotationDegrees(45));pose.scale(-1,-1,-1);pose.translate(0,-4,0);pose.scale(2,2,2);}
        else{boolean entity=context==ItemDisplayContext.GROUND||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.NONE;if(entity)pose.scale(1.5F,1.5F,1.5F);else pose.translate(.5,.25,0);pose.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)pose.mulPose(Axis.YP.rotationDegrees(90));}
        var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
        part(model,pose,buffers,light,overlay,"Dome","dome");part(model,pose,buffers,light,overlay,"Capsule","lander");part(model,pose,buffers,light,overlay,"Propulsion","propulsion");part(model,pose,buffers,light,overlay,"Solar","solar");pose.popPose();
    }
    private void part(WavefrontMesh model,PoseStack pose,MultiBufferSource buffers,int light,int overlay,String part,String texture){LegacyObjRenderer.renderOnly(model,pose,buffers.getBuffer(RenderType.entityCutout(ResourceLocation.parse("hbm:textures/models/soyuz_capsule/module_"+texture+".png"))),light,overlay,-1,LegacyObjRenderer.Convention.HFR,part);}
}
