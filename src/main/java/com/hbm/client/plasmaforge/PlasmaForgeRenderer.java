package com.hbm.client.plasmaforge;

import com.hbm.client.model.*;
import com.hbm.fluid.HbmFluids;
import com.hbm.machine.plasmaforge.*;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.AABB;

/** Original HFR groups, authored pivots and three plasma passes; no replacement geometry. */
public final class PlasmaForgeRenderer implements BlockEntityRenderer<PlasmaForgeBlockEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/fusion/plasma_forge.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/fusion/plasma_forge.png"),TORUS=ResourceLocation.parse("hbm:models/fusion/torus.obj"),TORUS_TEXTURE=ResourceLocation.parse("hbm:textures/models/fusion/torus.png"),PLASMA=ResourceLocation.parse("hbm:textures/models/fusion/plasma.png"),GLOW=ResourceLocation.parse("hbm:textures/models/fusion/plasma_glow.png");
    public PlasmaForgeRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh model(ResourceLocation id){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),id);}
    static void part(PoseStack pose,MultiBufferSource buffers,int light,int overlay,String... names){LegacyObjRenderer.renderOnly(model(MODEL),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,names);}
    static void inventory(PoseStack pose,MultiBufferSource buffers,int light,int overlay){pose.scale(.5F,.5F,.5F);pose.mulPose(Axis.YP.rotationDegrees(90));LegacyObjRenderer.renderAllExcept(model(MODEL),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,"Plasma");blackPlasma(pose,buffers,light,overlay);}
    private static void blackPlasma(PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderOnly(model(MODEL),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xff000000,LegacyObjRenderer.Convention.HFR,"Plasma");}
    @Override public void render(PlasmaForgeBlockEntity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        float yaw=switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->180;case WEST->270;case SOUTH->360;default->90;};
        LegacyContactShadows.render(machine,model(MODEL),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),pose,buffers);
        var state=PlasmaForgeAnimation.get(machine);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));
        if(machine.connected){pose.pushPose();pose.translate(-2,0,0);LegacyObjRenderer.renderOnly(model(TORUS),pose,buffers.getBuffer(RenderType.entityCutout(TORUS_TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,"Bolts1");pose.popPose();}
        part(pose,buffers,light,overlay,"Body");var recipe=(PlasmaForgeRecipe)machine.module.getRecipe();plasma(machine,state,pose,buffers,light,overlay);item(machine,recipe,partial,pose,buffers,light,overlay);beam(machine,recipe,partial,pose,buffers);
        double[] striker=state.armStriker.getPositions(partial),jet=state.armJet.getPositions(partial);double rotor=state.prevRing+(state.ring-state.prevRing)*partial;
        pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees((float)rotor));pose.pushPose();
        part(pose,buffers,light,overlay,"SliderStriker");pivotZ(pose,-2.75,2.5,-striker[0]);part(pose,buffers,light,overlay,"ArmLowerStriker");pivotZ(pose,-2.75,3.75,-striker[1]);part(pose,buffers,light,overlay,"ArmUpperStriker");pivotZ(pose,-1.5,3.75,-striker[2]);part(pose,buffers,light,overlay,"StrikerMount");
        pose.pushPose();pivotX(pose,3.375,.5,striker[3]);part(pose,buffers,light,overlay,"StrikerRight");pose.translate(0,-striker[4],0);part(pose,buffers,light,overlay,"PistonRight");pose.popPose();
        pose.pushPose();pivotX(pose,3.375,-.5,-striker[3]);part(pose,buffers,light,overlay,"StrikerLeft");pose.translate(0,-striker[5],0);part(pose,buffers,light,overlay,"PistonLeft");pose.popPose();pose.popPose();
        pose.pushPose();part(pose,buffers,light,overlay,"SliderJet");pivotZ(pose,2.75,2.5,jet[0]);part(pose,buffers,light,overlay,"ArmLowerJet");pivotZ(pose,2.75,3.75,jet[1]);part(pose,buffers,light,overlay,"ArmUpperJet");pivotZ(pose,1.5,3.75,jet[2]);part(pose,buffers,light,overlay,"Jet");if(machine.didProcess&&state.armJet.angles[2]==state.armJet.prevAngles[2]&&state.armJet.angles[2]!=0)jet(machine,pose,buffers);pose.popPose();pose.popPose();pose.popPose();
    }
    private static void pivotZ(PoseStack pose,double x,double y,double angle){pose.translate(x,y,0);pose.mulPose(Axis.ZP.rotationDegrees((float)angle));pose.translate(-x,-y,0);}
    private static void pivotX(PoseStack pose,double y,double z,double angle){pose.translate(0,y,z);pose.mulPose(Axis.XP.rotationDegrees((float)angle));pose.translate(0,-y,-z);}
    private static void plasma(PlasmaForgeBlockEntity machine,PlasmaForgeAnimation state,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        if(machine.plasmaEnergySync<=0){blackPlasma(pose,buffers,light,overlay);return;}
        long time=System.currentTimeMillis()+state.timeOffset;float alpha=.5F+(float)(Math.sin(time/500D)*.25F);double main=Math.sin(Math.PI/2D*Math.cos(time/750D))%1D;
        plasmaPass(pose,buffers,overlay,PLASMA,(float)main,machine.plasmaRed*alpha,machine.plasmaGreen*alpha,machine.plasmaBlue*alpha,false,false);
        plasmaPass(pose,buffers,overlay,GLOW,(float)(Math.sin(time/1000D)%1D+time/10000D%1D),machine.plasmaRed*2,machine.plasmaGreen*2,machine.plasmaBlue*2,true,true);
        plasmaPass(pose,buffers,overlay,GLOW,(float)(Math.sin(time/600D+2)%1D+time/5000D%1D),machine.plasmaRed*2,machine.plasmaGreen*2,machine.plasmaBlue*2,true,true);
    }
    private static void plasmaPass(PoseStack pose,MultiBufferSource buffers,int overlay,ResourceLocation texture,float v,float r,float g,float b,boolean additive,boolean glow){var output=PlasmaForgeRenderTypes.uv(buffers.getBuffer(additive?PlasmaForgeRenderTypes.additive(texture,glow):PlasmaForgeRenderTypes.unlit(texture)),0,v);LegacyObjRenderer.renderOnly(model(MODEL),pose,output,LightTexture.FULL_BRIGHT,overlay,color(r,g,b,1),LegacyObjRenderer.Convention.HFR,"Plasma");}
    private static void item(PlasmaForgeBlockEntity machine,PlasmaForgeRecipe recipe,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){if(recipe==null)return;var client=Minecraft.getInstance();var player=client.player;var p=machine.getBlockPos();if(player==null||player.distanceToSqr(p.getX()+.5,p.getY()+1,p.getZ()+.5)>35*35)return;pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(90));pose.translate(0,1.75,0);var stack=recipe.icon().copyWithCount(1);if(stack.getItem() instanceof BlockItem){if(client.getItemRenderer().getModel(stack,machine.getLevel(),null,0).isGui3d())pose.translate(0,-.0625,0);else pose.scale(.5F,.5F,.5F);}else pose.mulPose(Axis.YP.rotationDegrees(90));pose.translate(0,Math.sin((player.tickCount+partial)*.1)*.0625,0);pose.scale(1.5F,1.5F,1.5F);client.getItemRenderer().renderStatic(stack,ItemDisplayContext.FIXED,light,overlay,pose,buffers,machine.getLevel(),0);pose.popPose();}
    private static void beam(PlasmaForgeBlockEntity machine,PlasmaForgeRecipe recipe,float partial,PoseStack pose,MultiBufferSource buffers){if(recipe==null)return;var player=Minecraft.getInstance().player;var p=machine.getBlockPos();if(player==null||player.distanceToSqr(p.getX()+.5,p.getY()+1,p.getZ()+.5)>50*50)return;var out=buffers.getBuffer(PlasmaForgeRenderTypes.additive(HbmFluids.STELLAR_FLUX.getTexture(),false));double offset=((player.tickCount+partial)/15D)%1D,in=.4375,b=1,t=1.5,h=b+t;
        beamVertex(pose,out,-in,b,in,offset+t,0,1);beamVertex(pose,out,-in,h,in,offset,0,0);beamVertex(pose,out,-in,h,-in,offset,1,0);beamVertex(pose,out,-in,b,-in,offset+t,1,1);
        beamVertex(pose,out,in,h,in,offset,0,0);beamVertex(pose,out,in,b,in,offset+t,0,1);beamVertex(pose,out,in,b,-in,offset+t,1,1);beamVertex(pose,out,in,h,-in,offset,1,0);
        beamVertex(pose,out,in,b,in,offset+t,0,1);beamVertex(pose,out,in,h,in,offset,0,0);beamVertex(pose,out,-in,h,in,offset,1,0);beamVertex(pose,out,-in,b,in,offset+t,1,1);
        beamVertex(pose,out,in,h,-in,offset,0,0);beamVertex(pose,out,in,b,-in,offset+t,0,1);beamVertex(pose,out,-in,b,-in,offset+t,1,1);beamVertex(pose,out,-in,h,-in,offset,1,0);
    }
    private static void beamVertex(PoseStack pose,VertexConsumer out,double x,double y,double z,double u,double v,float alpha){out.addVertex(pose.last(),(float)x,(float)y,(float)z).setUv((float)u,(float)v).setColor(1F,1F,1F,alpha);}
    private static void jet(PlasmaForgeBlockEntity machine,PoseStack pose,MultiBufferSource buffers){var player=Minecraft.getInstance().player;if(player==null)return;double length=1+player.getRandom().nextDouble()*.125;var out=buffers.getBuffer(PlasmaForgeRenderTypes.JET);double side=.125,near=1.375,far=1.625;for(int layer=0;layer<2;layer++){double narrow=layer==0?.01:.0625*1.5,len=layer==0?length:length*1.5;jetQuad(machine,pose,out,near,3,side,far,3,side,far-narrow,3-len,side-narrow,near+narrow,3-len,side-narrow);jetQuad(machine,pose,out,near,3,-side,far,3,-side,far-narrow,3-len,-side+narrow,near+narrow,3-len,-side+narrow);jetQuad(machine,pose,out,near,3,side,near,3,-side,near+narrow,3-len,-side+narrow,near+narrow,3-len,side-narrow);jetQuad(machine,pose,out,far,3,side,far,3,-side,far-narrow,3-len,-side+narrow,far-narrow,3-len,side-narrow);}}
    private static void jetQuad(PlasmaForgeBlockEntity machine,PoseStack pose,VertexConsumer out,double... xyz){for(int i=0;i<4;i++)out.addVertex(pose.last(),(float)xyz[i*3],(float)xyz[i*3+1],(float)xyz[i*3+2]).setColor(machine.plasmaRed,machine.plasmaGreen,machine.plasmaBlue,i<2?1:0);}
    private static int color(float r,float g,float b,float a){return (int)(Math.clamp(a,0F,1F)*255)<<24|(int)(Math.clamp(r,0F,1F)*255)<<16|(int)(Math.clamp(g,0F,1F)*255)<<8|(int)(Math.clamp(b,0F,1F)*255);}
    @Override public boolean shouldRenderOffScreen(PlasmaForgeBlockEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(PlasmaForgeBlockEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-5,p.getY(),p.getZ()-5,p.getX()+5,p.getY()+6,p.getZ()+6);}
}
