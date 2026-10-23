package com.hbm.client.entity.boat;
import com.hbm.entity.boat.EntityBoatRubber;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
/** Original static ModelBoat and hit-roll; local rider view follows the real interpolated source hull. */
public final class RubberBoatRenderer extends EntityRenderer<EntityBoatRubber> {
 private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/entity/boat_rubber.png");
 private final LegacyBoatModel model=new LegacyBoatModel();
 public RubberBoatRenderer(EntityRendererProvider.Context context){super(context);shadowRadius=.5F;}
 @Override public ResourceLocation getTextureLocation(EntityBoatRubber boat){return TEXTURE;}
 @Override public void render(EntityBoatRubber boat,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
  poses.pushPose();
  try{
   poses.mulPose(Axis.YP.rotationDegrees(180F-yaw));
   float hit=boat.getTimeSinceHit()-partial,damage=Math.max(boat.getDamageTaken()-partial,0F);
   if(hit>0)poses.mulPose(Axis.XP.rotationDegrees(Mth.sin(hit)*hit*damage/10F*boat.getForwardDirection()));
   var player=Minecraft.getInstance().player;
   if(player!=null&&boat.getFirstPassenger()==player){float delta=Mth.wrapDegrees(boat.getYRot()-boat.prevRenderYaw);player.setYRot(player.getYRot()+delta);player.yHeadRot+=delta;}
   boat.prevRenderYaw=boat.getYRot();
   poses.scale(.75F,.75F,.75F);poses.scale(1F/.75F,1F/.75F,1F/.75F);poses.scale(-1,-1,1);
   model.renderToBuffer(poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,OverlayTexture.NO_OVERLAY,-1,boat,0,0,-.1F,0,0,.0625F);
  }finally{poses.popPose();}
  super.render(boat,yaw,partial,poses,buffers,light);
 }
}
