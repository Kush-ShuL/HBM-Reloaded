package com.hbm.client.reactor.icf;
import com.hbm.client.weapon.energy.EnergyBeams;
import com.hbm.machine.reactor.icf.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.world.phys.AABB;
/** Source BeamPronter SPIRAL/SOLID with zero spiral radius,1segment,10layers and.125thickness. */
public final class IcfLaserRenderer implements BlockEntityRenderer<IcfLaserEntity>{
 public IcfLaserRenderer(BlockEntityRendererProvider.Context context){}@Override public void render(IcfLaserEntity laser,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){if(laser.laserLength<=0)return;poses.pushPose();poses.translate(.5,.5,.5);poses.mulPose(Axis.YP.rotationDegrees(switch(laser.getBlockState().getValue(IcfStructBlock.META)){case 4->90;case 3->180;case 5->270;default->0;}));poses.mulPose(Axis.XP.rotationDegrees(-90));poses.mulPose(Axis.YP.rotationDegrees(180));EnergyBeams.solid(poses,buffers,laser.laserLength,0x202020,0x100000,0,1,0,10,.125F);poses.popPose();}@Override public boolean shouldRenderOffScreen(IcfLaserEntity laser){return true;}@Override public AABB getRenderBoundingBox(IcfLaserEntity laser){var p=laser.getBlockPos();return new AABB(p.getX()+.5-50,p.getY(),p.getZ()+.5-50,p.getX()+.5+50,p.getY()+1,p.getZ()+.5+50);}
}
