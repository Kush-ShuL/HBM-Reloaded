package com.hbm.client.taint;
import com.hbm.client.tesla.TeslaArcRenderer;
import com.hbm.world.taint.TaintCrab;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
public final class TaintCrabRenderer extends MobRenderer<TaintCrab,TaintCrabModel> {
 public TaintCrabRenderer(EntityRendererProvider.Context context){super(context,new TaintCrabModel(),1);shadowStrength=0;}
 @Override public ResourceLocation getTextureLocation(TaintCrab entity){return ResourceLocation.parse("hbm:textures/entity/taintcrab.png");}
 @Override public void render(TaintCrab crab,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){pose.pushPose();pose.translate(0,1.25,0);var origin=new Vec3(crab.getX(),crab.getY()+1.25,crab.getZ());for(var target:crab.targets){var delta=target.subtract(origin);TeslaArcRenderer.beam(delta,pose,buffers,(int)(crab.level().getGameTime()%1000+1),(int)(delta.length()*5));}pose.popPose();super.render(crab,yaw,partial,pose,buffers,light);}
}
