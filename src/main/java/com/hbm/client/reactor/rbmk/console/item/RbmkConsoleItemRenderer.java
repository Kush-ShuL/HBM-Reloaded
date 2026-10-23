package com.hbm.client.reactor.rbmk.console.item;
import com.hbm.client.model.*;import com.mojang.blaze3d.vertex.PoseStack;import com.mojang.math.Axis;import net.minecraft.client.Minecraft;import net.minecraft.client.renderer.*;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.item.*;
/** ItemRenderLibrary669/681, original ItemRenderBase matrices and source HFR meshes; no world-only screen data or crane assembly. */
public final class RbmkConsoleItemRenderer extends BlockEntityWithoutLevelRenderer {
 private final boolean crane;
 public RbmkConsoleItemRenderer(boolean crane){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());this.crane=crane;}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
  pose.pushPose();LegacyItemTransforms.held(pose,context);
  if(context==ItemDisplayContext.GUI){pose.translate(0,1,.5);pose.scale(1F/16,-1F/16,1F/16);pose.translate(8,10,0);pose.mulPose(Axis.XP.rotationDegrees(-30));pose.mulPose(Axis.YP.rotationDegrees(45));pose.scale(-1,-1,-1);pose.translate(0,-3,0);float scale=crane?3.5F:2.5F;pose.scale(scale,scale,scale);}
  else{if(context==ItemDisplayContext.GROUND){pose.translate(.5,0,.5);pose.scale(1.5F,1.5F,1.5F);}else pose.translate(.5,.25,0);pose.scale(.25F,.25F,.25F);if(context!=ItemDisplayContext.THIRD_PERSON_LEFT_HAND&&context!=ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)pose.mulPose(Axis.YP.rotationDegrees(90));}
  var model=ResourceLocation.parse(crane?"hbm:models/rbmk/crane_console.obj":"hbm:models/rbmk/rbmk_console.obj");var texture=ResourceLocation.parse(crane?"hbm:textures/models/machines/crane_console.png":"hbm:textures/models/machines/rbmk_control.png");var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),model);var out=buffers.getBuffer(RenderType.entityCutout(texture));
  if(crane)LegacyObjRenderer.render(mesh,pose,out,light,overlay,-1,LegacyObjRenderer.Convention.HFR,face->java.util.Set.of("Console_Coonsole","JoyStick","Meter1","Meter2").contains(face.legacyPart()));else LegacyObjRenderer.renderAll(mesh,pose,out,light,overlay,-1,LegacyObjRenderer.Convention.HFR);pose.popPose();
 }
}
