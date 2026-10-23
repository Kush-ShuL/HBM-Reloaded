package com.hbm.client.lpw2;

import com.hbm.client.model.*;
import com.hbm.machine.lpw2.Lpw2Entity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Original RenderLPW2 body mechanically translated; no invented operating state or animation gate. */
public final class Lpw2Renderer implements BlockEntityRenderer<Lpw2Entity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/lpw2.obj");
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/lpw2.png");
    private static final ResourceLocation ERROR=ResourceLocation.parse("hbm:textures/models/machines/lpw2_term_error.png");
    public Lpw2Renderer(BlockEntityRendererProvider.Context context){}
    private static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    private static void part(PoseStack pose,MultiBufferSource buffers,int light,int overlay,String part){LegacyObjRenderer.renderOnly(model(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,part);}
    private static void rotate(PoseStack pose,double degrees,double x,double y,double z){if(x!=0)pose.mulPose(Axis.XP.rotationDegrees((float)(degrees*x)));else if(y!=0)pose.mulPose(Axis.YP.rotationDegrees((float)(degrees*y)));else pose.mulPose(Axis.ZP.rotationDegrees((float)(degrees*z)));}
    private static double sps(double value){return Math.sin(Math.PI/2D*Math.cos(value));}
    @Override public void render(Lpw2Entity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        if(machine.getLevel()==null)return;
        int yaw=switch(machine.getBlockState().getValue(DummyableBlock.LEGACY_META)-10){case 2->90;case 4->180;case 3->270;default->0;};
        LegacyContactShadows.render(machine,model(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),pose,buffers);
        pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));
		long time = machine.getLevel().getGameTime() % 1000000;
		
		double swayTimer = ((time + partial) / 3D) % (Math.PI * 4);
		double sway = (Math.sin(swayTimer) + Math.sin(swayTimer * 2) + Math.sin(swayTimer * 4) + 2.23255D) * 0.5;

		double bellTimer = ((time + partial) / 5D) % (Math.PI * 4);
		double h = (Math.sin(bellTimer + Math.PI) + Math.sin(bellTimer * 1.5D)) / 1.90596D;
		double v = (Math.sin(bellTimer) + Math.sin(bellTimer * 1.5D)) / 1.90596D;

		double pistonTimer = ((time + partial) / 5D) % (Math.PI * 2);
		double piston = sps(pistonTimer);
		double rotorTimer = ((time + partial) / 5D) % (Math.PI * 16);
		double rotor = (sps(rotorTimer) + rotorTimer / 2D - 1) / 25.1327412287D;
		double turbine = ((time + partial) % 100) / 100D;

		
		part(pose,buffers,light,overlay,"Frame");
		
		renderMainAssembly(pose,buffers,light,overlay,sway,h,v,piston,rotor,turbine);
		
		pose.pushPose();
		pose.translate(-2.9375, 0, 2.375);
		rotate(pose,sway * 10,0,1,0);
		pose.translate(2.9375, 0, -2.375);
		part(pose,buffers,light,overlay,"WireLeft");
		pose.popPose();
		
		pose.pushPose();
		pose.translate(2.9375, 0, 2.375);
		rotate(pose,sway * -10,0,1,0);
		pose.translate(-2.9375, 0, -2.375);
		part(pose,buffers,light,overlay,"WireRight");
		pose.popPose();

		double coverTimer = ((time + partial) / 5D) % (Math.PI * 4);
		double cover = (Math.sin(coverTimer) + Math.sin(coverTimer * 2) + Math.sin(coverTimer * 4)) * 0.5;
		
		pose.pushPose();
		pose.translate(0, 0, -cover * 0.125);
		part(pose,buffers,light,overlay,"Cover");
		pose.popPose();
		
		pose.pushPose();
		pose.translate(0, 0, 3.5);
		pose.scale((float)(1),(float)(1),(float)((3 + cover * 0.125) / 3));
		pose.translate(0, 0, -3.5);
		part(pose,buffers,light,overlay,"SuspensionCoverFront");
		pose.popPose();
		
		pose.pushPose();
		pose.translate(0, 0, -5.5);
		pose.scale((float)(1),(float)(1),(float)((1.5 - cover * 0.125) / 1.5));
		pose.translate(0, 0, 5.5);
		part(pose,buffers,light,overlay,"SuspensionCoverBack");
		pose.popPose();
		
		pose.pushPose();
		pose.translate(0, 0, -9);
		pose.scale((float)(1),(float)(1),(float)((1.25 - sway * 0.125) / 1.25));
		pose.translate(0, 0, 9);
		part(pose,buffers,light,overlay,"SuspensionBackOuter");
		pose.popPose();
		
		pose.pushPose();
		pose.translate(0, 0, -9.5);
		pose.scale((float)(1),(float)(1),(float)((1.75 - sway * 0.125) / 1.75));
		pose.translate(0, 0, 9.5);
		part(pose,buffers,light,overlay,"SuspensionBackCenter");
		pose.popPose();

		double serverTimer = ((time + partial) / 2D) % (Math.PI * 4);
		double sx = (Math.sin(serverTimer + Math.PI) + Math.sin(serverTimer * 1.5D)) / 1.90596D;
		double sy = (Math.sin(serverTimer) + Math.sin(serverTimer * 1.5D)) / 1.90596D;

		double serverSway = 0.0625D * 0.25D;
		
		pose.pushPose();
		pose.translate(sx * serverSway, 0, sy * serverSway);
		part(pose,buffers,light,overlay,"Server1");
		pose.popPose();
		
		pose.pushPose();
		pose.translate(-sy * serverSway, 0, sx * serverSway);
		part(pose,buffers,light,overlay,"Server2");
		pose.popPose();
		
		pose.pushPose();
		pose.translate(sy * serverSway, 0, -sx * serverSway);
		part(pose,buffers,light,overlay,"Server3");
		pose.popPose();
		
		pose.pushPose();
		pose.translate(-sx * serverSway, 0, -sy * serverSway);
		part(pose,buffers,light,overlay,"Server4");
		pose.popPose();

		double errorTimer = ((time + partial) / 3D);
		
		pose.pushPose();
		pose.translate(sy * serverSway, 0, sx * serverSway);
		
		part(pose,buffers,light,overlay,"Monitor");

        // Original GL_TEXTURE translation is applied after HFR V flip, without changing Screen geometry.
        double scroll=(sps(errorTimer)+errorTimer/2D)%1;
        LegacyObjRenderer.renderOnly(model(),pose,new ScrollUv(buffers.getBuffer(RenderType.entityCutout(ERROR)),(float)scroll),light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Screen");

		pose.popPose();

		pose.popPose();
	}
	
	private static void renderMainAssembly(PoseStack pose,MultiBufferSource buffers,int light,int overlay,double sway,double h,double v,double piston,double rotor,double turbine) {
		pose.pushPose();
		pose.translate(0, 0, -sway * 0.125);
		part(pose,buffers,light,overlay,"Center");

		pose.pushPose();
		pose.translate(0, 3.5, 0);
		
		pose.pushPose();
		rotate(pose,rotor * 360,0,0,-1);
		pose.translate(0, -3.5, 0);
		part(pose,buffers,light,overlay,"Rotor");
		pose.popPose();

		pose.pushPose();
		rotate(pose,turbine * 360,0,0,1);
		pose.translate(0, -3.5, 0);
		part(pose,buffers,light,overlay,"TurbineFront");
		pose.popPose();
		
		pose.pushPose();
		rotate(pose,turbine * 360,0,0,-1);
		pose.translate(0, -3.5, 0);
		part(pose,buffers,light,overlay,"TurbineBack");
		pose.popPose();

		pose.popPose();
		
		pose.pushPose();
		pose.translate(0, 0, piston * 0.375D + 0.375D);
		part(pose,buffers,light,overlay,"Piston");
		pose.popPose();
		
		renderBell(pose,buffers,light,overlay,h,v);
		pose.popPose();
		
		renderShroud(pose,buffers,light,overlay,h,v);
	}
	
	private static void renderBell(PoseStack pose,MultiBufferSource buffers,int light,int overlay,double h,double v) {
		pose.pushPose();
		pose.translate(0, 3.5, 2.75);
		double magnitude = 2D;
		rotate(pose,v * magnitude,0,1,0);
		rotate(pose,h * magnitude,1,0,0);
		pose.translate(0, -3.5, -2.75);
		part(pose,buffers,light,overlay,"Engine");
		pose.popPose();
	}
	
	private static void renderShroud(PoseStack pose,MultiBufferSource buffers,int light,int overlay,double h,double v) {

		double magnitude = 0.125D;
		double rotation = 5D;
		double offset = 10D;
		
		pose.pushPose();
		pose.translate(0, -h * magnitude, 0);
		part(pose,buffers,light,overlay,"ShroudH");

		renderFlap(pose,buffers,light,overlay,90 + 22.5D, rotation * v + offset);
		renderFlap(pose,buffers,light,overlay,90 - 22.5D, rotation * v + offset);
		renderFlap(pose,buffers,light,overlay,270 + 22.5D, rotation * -v + offset);
		renderFlap(pose,buffers,light,overlay,270 - 22.5D, rotation * -v + offset);
		
		pose.popPose();
		
		pose.pushPose();
		pose.translate(v * magnitude, 0, 0);
		part(pose,buffers,light,overlay,"ShroudV");

		renderFlap(pose,buffers,light,overlay,22.5D, rotation * h + offset);
		renderFlap(pose,buffers,light,overlay,-22.5D, rotation * h + offset);
		renderFlap(pose,buffers,light,overlay,180 + 22.5D, rotation * -h + offset);
		renderFlap(pose,buffers,light,overlay,180 - 22.5D, rotation * -h + offset);
		
		pose.popPose();
		
		double length = 0.6875D;
		
		pose.pushPose();
		pose.translate(-2.625D, 0, 0);
		pose.scale((float)((length + v * magnitude) / length),(float)( 1),(float)( 1));
		pose.translate(2.625D, 0, 0);
		part(pose,buffers,light,overlay,"SuspensionLeft");
		pose.popPose();
		
		pose.pushPose();
		pose.translate(2.625D, 0, 0);
		pose.scale((float)((length - v * magnitude) / length),(float)( 1),(float)( 1));
		pose.translate(-2.625D, 0, 0);
		part(pose,buffers,light,overlay,"SuspensionRight");
		pose.popPose();
		
		pose.pushPose();
		pose.translate(0, 6.125D, 0);
		pose.scale((float)(1),(float)( (length + h * magnitude) / length),(float)( 1));
		pose.translate(0, -6.125D, 0);
		part(pose,buffers,light,overlay,"SuspensionTop");
		pose.popPose();
		
		pose.pushPose();
		pose.translate(0, 0.875D, 0);
		pose.scale((float)(1),(float)( (length - h * magnitude) / length),(float)( 1));
		pose.translate(0, -0.875D, 0);
		part(pose,buffers,light,overlay,"SuspensionBottom");
		pose.popPose();
	}
	
	private static void renderFlap(PoseStack pose,MultiBufferSource buffers,int light,int overlay,double position,double rotation) {
		pose.pushPose();
		
		pose.translate(0, 3.5D, 0);
		rotate(pose,position,0,0,1);
		pose.translate(0, -3.5D, 0);
		
		pose.translate(0, 6.96875D, 8.5D);
		rotate(pose,rotation,1,0,0);
		pose.translate(0, -6.96875D, -8.5D);
		
		part(pose,buffers,light,overlay,"Flap");
		pose.popPose();
	}
    @Override public boolean shouldRenderOffScreen(Lpw2Entity entity){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(Lpw2Entity entity){var pos=entity.getBlockPos();return new AABB(pos.getX()-10,pos.getY(),pos.getZ()-10,pos.getX()+11,pos.getY()+7,pos.getZ()+11);}
    /** The one original texture-matrix translation; all original per-vertex attributes still use the shared emitter. */
    private record ScrollUv(VertexConsumer delegate,float scroll) implements VertexConsumer {
        @Override public VertexConsumer addVertex(float x,float y,float z){delegate.addVertex(x,y,z);return this;}
        @Override public VertexConsumer setColor(int r,int g,int b,int a){delegate.setColor(r,g,b,a);return this;}
        @Override public VertexConsumer setUv(float u,float v){delegate.setUv(u,v+scroll);return this;}
        @Override public VertexConsumer setUv1(int u,int v){delegate.setUv1(u,v);return this;}
        @Override public VertexConsumer setUv2(int u,int v){delegate.setUv2(u,v);return this;}
        @Override public VertexConsumer setNormal(float x,float y,float z){delegate.setNormal(x,y,z);return this;}
    }
}
