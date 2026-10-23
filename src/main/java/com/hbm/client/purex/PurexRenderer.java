package com.hbm.client.purex;

import com.hbm.client.model.*;
import com.hbm.machine.purex.PurexBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Original HFR Base/overhead Frame, fan at(1.5,1.25,0), sps(anim*.25)*.5 pump. */
public final class PurexRenderer implements BlockEntityRenderer<PurexBlockEntity> {
    static final ResourceLocation MODEL=id("models/machines/purex.obj"),TEXTURE=id("textures/models/machines/purex.png");
    private static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public PurexRenderer(BlockEntityRendererProvider.Context context){}
    private static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    private static void parts(PoseStack poses,MultiBufferSource buffers,int light,int overlay,String...parts){LegacyObjRenderer.renderOnly(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,parts);}
    static void inventory(PoseStack poses,MultiBufferSource buffers,int light,int overlay){parts(poses,buffers,light,overlay,"Base","Frame","Fan","Pump");}
    private static double sps(double x){return Math.sin(Math.PI/2*Math.cos(x));}
    private static int angle(PurexBlockEntity machine){return switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};}
    @Override public void render(PurexBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        LegacyContactShadows.render(machine,model(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(angle(machine))),poses,buffers);
        var state=PurexAnimation.get(machine);float anim=state.animation(partial);
        poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(angle(machine)));parts(poses,buffers,light,overlay,"Base");if(state.frame)parts(poses,buffers,light,overlay,"Frame");
        poses.pushPose();poses.translate(1.5,1.25,0);poses.mulPose(Axis.ZP.rotationDegrees(anim*45));poses.translate(-1.5,-1.25,0);parts(poses,buffers,light,overlay,"Fan");poses.popPose();
        poses.pushPose();poses.translate(sps(anim*.25)*.5,0,0);parts(poses,buffers,light,overlay,"Pump");poses.popPose();poses.popPose();
    }
    @Override public boolean shouldRenderOffScreen(PurexBlockEntity machine){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(PurexBlockEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-2,p.getY(),p.getZ()-2,p.getX()+3,p.getY()+5,p.getZ()+3);}
}
