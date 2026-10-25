package com.hbm.client.entity.crab;
import com.hbm.client.tesla.TeslaArcRenderer;
import com.hbm.entity.mob.crab.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
/** Original RenderCyberCrab/RenderTeslaCrab with shadow opacity0 and the shared original BeamPronter path. */
public final class CrabRenderer extends MobRenderer<CyberCrab,CrabModel>{
 private final boolean tesla;public CrabRenderer(EntityRendererProvider.Context context,boolean tesla){super(context,new CrabModel(tesla),1);this.tesla=tesla;shadowStrength=0;}
 @Override public ResourceLocation getTextureLocation(CyberCrab crab){return ResourceLocation.parse(tesla?"hbm:textures/entity/teslacrab.png":"hbm:textures/entity/crab.png");}
 @Override public void render(CyberCrab crab,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){if(crab instanceof TeslaCrab coil){poses.pushPose();poses.translate(0,1,0);var origin=new Vec3(crab.getX(),crab.getY()+1,crab.getZ());for(var target:coil.targets){var delta=target.subtract(origin);TeslaArcRenderer.beamWithDepth(delta,poses,buffers,(int)(crab.level().getGameTime()%1000+1),(int)(delta.length()*5));}poses.popPose();}super.render(crab,yaw,partial,poses,buffers,light);}
}
