package com.hbm.client.bomb.nuclear.programmable;
import com.hbm.bomb.nuclear.programmable.FallingCustomNuclearBomb;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;

/** RenderFallingNuke: source metadata then interpolated roll, never modern projectile auto-orientation. */
public final class FallingCustomNuclearRenderer extends EntityRenderer<FallingCustomNuclearBomb> {
 public FallingCustomNuclearRenderer(EntityRendererProvider.Context context){super(context);}
 @Override public void render(FallingCustomNuclearBomb bomb,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){pose.pushPose();CustomNuclearRenderer.sourceOrientation(pose,bomb.metadata());float roll=bomb.xRotO+(bomb.getXRot()-bomb.xRotO)*partial;if(roll< -80)roll=0;pose.mulPose(Axis.ZP.rotationDegrees(roll));CustomNuclearRenderer.draw(pose,buffers,light,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,true);pose.popPose();}
 @Override public ResourceLocation getTextureLocation(FallingCustomNuclearBomb bomb){return CustomNuclearRenderer.TEXTURE;}
}
