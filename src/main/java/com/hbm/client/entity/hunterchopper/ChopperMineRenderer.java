package com.hbm.client.entity.hunterchopper;
import com.hbm.client.model.legacy.*;
import com.hbm.client.model.legacy.generated.ModelChopperMine;
import com.hbm.entity.mob.hunterchopper.ChopperMine;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
public final class ChopperMineRenderer extends EntityRenderer<ChopperMine> {
 private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/models/chopper_bomb.png");private final ModelChopperMine model=new ModelChopperMine();public ChopperMineRenderer(EntityRendererProvider.Context context){super(context);}@Override public ResourceLocation getTextureLocation(ChopperMine mine){return TEXTURE;}
 @Override public void render(ChopperMine mine,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){pose.pushPose();pose.scale(1.5F,1.5F,1.5F);pose.mulPose(Axis.XP.rotationDegrees(180));try(var scope=LegacyModelRenderContext.push(pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,OverlayTexture.NO_OVERLAY,-1)){model.renderAll(.0625F);}pose.popPose();}
}
