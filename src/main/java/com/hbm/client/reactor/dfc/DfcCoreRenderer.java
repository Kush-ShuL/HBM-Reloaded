package com.hbm.client.reactor.dfc;
import com.hbm.client.model.*;
import com.hbm.machine.reactor.dfc.DfcCoreEntity;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.*;

/** Actual source standby sphere/six sparks,17 additive pulse shells and150 cumulative seeded flare fans. */
public final class DfcCoreRenderer implements BlockEntityRenderer<DfcCoreEntity> {
    private static final ResourceLocation UV=ResourceLocation.parse("hbm:models/sphere_uv.obj"),RUV=ResourceLocation.parse("hbm:models/sphere_ruv.obj");
    public DfcCoreRenderer(BlockEntityRendererProvider.Context context){}
    private static WavefrontMesh mesh(ResourceLocation model){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),model);}
    private static void sphere(ResourceLocation model,PoseStack pose,MultiBufferSource buffers,RenderType type,int rgb){LegacyObjRenderer.renderAll(mesh(model),pose,buffers.getBuffer(type),LightTexture.FULL_BRIGHT,0,0xff000000|rgb,LegacyObjRenderer.Convention.HFR);}
    @Override public void render(DfcCoreEntity core,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        if(core.heat==0)LegacyContactShadows.render(core,mesh(UV),new org.joml.Matrix4f().translation(.5F,.5F,.5F).scale(.25F),pose,buffers);
        pose.pushPose();pose.translate(.5,.5,.5);
        if(core.heat==0){
            pose.scale(.25F,.25F,.25F);sphere(UV,pose,buffers,DfcCoreRenderTypes.SOLID,0x7f7f7f);pose.scale(1.25F,1.25F,1.25F);sphere(UV,pose,buffers,DfcCoreRenderTypes.GLOW,0x191919);
            long time=System.currentTimeMillis();if(time/100%10==0)for(int i=0;i<3;i++){spark((int)time/100+i*10000,pose,buffers);spark((int)time/50+i*10000,pose,buffers);}
        }else if(core.meltdownTick){
            var copy=new PoseStack();copy.last().pose().set(pose.last().pose());copy.last().normal().set(pose.last().normal());int color=core.color;long time=core.getLevel().getGameTime();DfcCorePass.submit(buffers,()->flare(color,time,copy,buffers));
        }else{
            float scale=4.5F*(core.tanks[0].getFill()+core.tanks[1].getFill())/(core.tanks[0].getMaxFill()+core.tanks[1].getMaxFill())+.5F;pose.scale(scale,scale,scale);pose.scale(.25F,.25F,.25F);
            int rgb=colour((core.color>>16&255)/256F*.4F,(core.color>>8&255)/256F*.4F,(core.color&255)/256F*.4F);sphere(RUV,pose,buffers,DfcCoreRenderTypes.SOLID,rgb);
            float pulse=pulse(core.getLevel().getGameTime()*.1D);
            for(int i=0;i<=16;i++){pose.pushPose();float size=1F+.25F*i+(pulse*(20-i))*.125F;pose.scale(size,size,size);sphere(RUV,pose,buffers,DfcCoreRenderTypes.GLOW,rgb);pose.popPose();}
        }
        pose.popPose();
    }
    private static int colour(float r,float g,float b){return Math.clamp((int)(r*255),0,255)<<16|Math.clamp((int)(g*255),0,255)<<8|Math.clamp((int)(b*255),0,255);}
    private static float pulse(double angle){angle%=Math.PI*2D;double t=.8F;float value=(float)((1D/t)*Math.atan(t*Math.sin(angle)/(1-t*Math.cos(angle))));value+=1D;value/=2D;return value;}
    private static void flare(int color,long time,PoseStack pose,MultiBufferSource buffers){
        var random=new Random(432);float phase=time/200F,size=.875F+pulse(time*.2D)*.125F;pose.scale(size,size,size);var out=buffers.getBuffer(DfcCoreRenderTypes.FLARE);
        for(int i=0;i<150;i++){
            pose.mulPose(Axis.XP.rotationDegrees(random.nextFloat()*360));pose.mulPose(Axis.YP.rotationDegrees(random.nextFloat()*360));pose.mulPose(Axis.ZP.rotationDegrees(random.nextFloat()*360));pose.mulPose(Axis.XP.rotationDegrees(random.nextFloat()*360));pose.mulPose(Axis.YP.rotationDegrees(random.nextFloat()*360));pose.mulPose(Axis.ZP.rotationDegrees(random.nextFloat()*360+phase*90));
            float length=random.nextFloat()*2+5,width=random.nextFloat()+1;pose.scale(.999F,.999F,.999F);
            float[][] points={{(float)(-.866D*width),length,-.5F*width},{(float)(.866D*width),length,-.5F*width},{0,length,width}};
            for(int face=0;face<3;face++){out.addVertex(pose.last().pose(),0,0,0).setColor(0xff000000|color);float[] a=points[face],b=points[(face+1)%3];out.addVertex(pose.last().pose(),a[0],a[1],a[2]).setColor(color);out.addVertex(pose.last().pose(),b[0],b[1],b[2]).setColor(color);}
        }
    }
    private static void spark(int seed,PoseStack pose,MultiBufferSource buffers){
        var random=new Random(seed);var direction=new Vec3(random.nextDouble()-.5,random.nextDouble()-.5,random.nextDouble()-.5).normalize();var point=Vec3.ZERO;
        for(int i=0;i<5+random.nextInt(10);i++){var previous=point;var unit=direction.normalize();point=previous.add(unit.x*1.5F*random.nextFloat(),unit.y*1.5F*random.nextFloat(),unit.z*1.5F*random.nextFloat());line(pose,buffers,DfcCoreRenderTypes.WIDE,previous,point,0xffffff00);line(pose,buffers,DfcCoreRenderTypes.NARROW,previous,point,0xffffffff);}
    }
    private static void line(PoseStack pose,MultiBufferSource buffers,RenderType type,Vec3 a,Vec3 b,int color){var direction=b.subtract(a).normalize();var out=buffers.getBuffer(type);out.addVertex(pose.last().pose(),(float)a.x,(float)a.y,(float)a.z).setColor(color).setNormal(pose.last(),(float)direction.x,(float)direction.y,(float)direction.z);out.addVertex(pose.last().pose(),(float)b.x,(float)b.y,(float)b.z).setColor(color).setNormal(pose.last(),(float)direction.x,(float)direction.y,(float)direction.z);}
    @Override public boolean shouldRenderOffScreen(DfcCoreEntity core){return true;}@Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(DfcCoreEntity core){var p=core.getBlockPos();return new AABB(p.getX()-3.5,p.getY()-3.5,p.getZ()-3.5,p.getX()+5.5,p.getY()+5.5,p.getZ()+5.5);}
}
