package com.hbm.client.woodprocessing;
import com.hbm.client.model.*;
import com.hbm.machine.woodprocessing.SawmillEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Actual Forge sawmill OBJ: independent blade and both gear pivots and authored1.7UV inset. */
public final class SawmillRenderer implements BlockEntityRenderer<SawmillEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/sawmill.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/sawmill.png");
    public SawmillRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void part(String name,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.render(model(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.FORGE,WavefrontMesh.legacyPart(name));}
    static void common(float rotation,boolean blade,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        part("Main",pose,buffers,light,overlay);
        if(blade){pose.pushPose();pose.translate(0,1.375,0);pose.mulPose(Axis.ZN.rotationDegrees(rotation*2));pose.translate(0,-1.375,0);part("Blade",pose,buffers,light,overlay);pose.popPose();}
        pose.pushPose();pose.translate(.5625,1.375,0);pose.mulPose(Axis.ZP.rotationDegrees(rotation));pose.translate(-.5625,-1.375,0);part("GearLeft",pose,buffers,light,overlay);pose.popPose();
        pose.pushPose();pose.translate(-.5625,1.375,0);pose.mulPose(Axis.ZN.rotationDegrees(rotation));pose.translate(.5625,-1.375,0);part("GearRight",pose,buffers,light,overlay);pose.popPose();
    }
    @Override public void render(SawmillEntity saw,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){float yaw=switch(DummyableBlock.facing(saw.getBlockState())){case SOUTH->0;case EAST->90;case NORTH->180;default->270;};LegacyContactShadows.render(saw,model(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));common(saw.lastSpin+(saw.spin-saw.lastSpin)*partial,saw.hasBlade,pose,buffers,light,overlay);pose.popPose();}
    @Override public boolean shouldRenderOffScreen(SawmillEntity saw){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(SawmillEntity saw){var p=saw.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+2,p.getZ()+2);}
}
