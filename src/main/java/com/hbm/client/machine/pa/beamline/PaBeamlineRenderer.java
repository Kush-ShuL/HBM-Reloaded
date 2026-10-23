package com.hbm.client.machine.pa.beamline;
import com.hbm.client.model.*;
import com.hbm.machine.pa.beamline.PaBeamlineEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
public final class PaBeamlineRenderer implements BlockEntityRenderer<PaBeamlineEntity>{
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/particleaccelerator/beamline.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/particleaccelerator/beamline.png");private static ResourceLocation white;
    public PaBeamlineRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}private static ResourceLocation white(){if(white==null){var image=new NativeImage(1,1,false);image.setPixelRGBA(0,0,-1);white=ResourceLocation.parse("hbm:pa_beamline_white");Minecraft.getInstance().getTextureManager().register(white,new DynamicTexture(image));}return white;}
    static void part(String name,PoseStack poses,MultiBufferSource buffers,int light,int overlay,int color,boolean solid){LegacyObjRenderer.renderOnly(model(),poses,buffers.getBuffer(RenderType.entityCutout(solid?white():TEXTURE)),light,overlay,color,LegacyObjRenderer.Convention.HFR,name);}
    @Override public void render(PaBeamlineEntity beamline,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){float yaw=switch(DummyableBlock.facing(beamline.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};var transform=new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw));LegacyContactShadows.render(beamline,model(),transform,poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(yaw));if(!beamline.window)part("Beamline",poses,buffers,light,overlay,-1,false);else{part("BeamlineWindow",poses,buffers,light,overlay,-1,false);float flash=beamline.prevLight+(beamline.light-beamline.prevLight)*partial;int rg=Math.clamp((int)(.9F*flash*255),0,255),blue=Math.clamp((int)(flash*255),0,255);part("BeamlineGlass",poses,buffers,LightTexture.FULL_BRIGHT,overlay,0xff000000|rg<<16|rg<<8|blue,true);}poses.popPose();}
    @Override public boolean shouldRenderOffScreen(PaBeamlineEntity beamline){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(PaBeamlineEntity beamline){var p=beamline.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+1,p.getZ()+2);}
}
