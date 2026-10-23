package com.hbm.client.generator.steamengine;
import com.hbm.client.model.*;
import com.hbm.machine.generator.steamengine.SteamEngineBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original five HFR parts, source flywheel/shaft pivots and exact non-approximated piston linkage. */
public final class SteamEngineRenderer implements BlockEntityRenderer<SteamEngineBlockEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/steam_engine.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/steam_engine.png");
    public SteamEngineRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    private static void part(PoseStack poses,MultiBufferSource buffers,int light,int overlay,String name){LegacyObjRenderer.renderOnly(model(),poses,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,name);}
    static void common(double angle,PoseStack poses,MultiBufferSource buffers,int light,int overlay){part(poses,buffers,light,overlay,"Base");poses.pushPose();poses.translate(2,1.375,0);poses.mulPose(Axis.ZN.rotationDegrees((float)angle));poses.translate(-2,-1.375,0);part(poses,buffers,light,overlay,"Flywheel");poses.popPose();poses.pushPose();poses.translate(0,1.375,-.5);poses.mulPose(Axis.XP.rotationDegrees((float)(angle*2)));poses.translate(0,-1.375,.5);part(poses,buffers,light,overlay,"Shaft");poses.popPose();double sin=Math.sin(Math.toRadians(angle))*.25-.25,cos=Math.cos(Math.toRadians(angle))*.25,linkage=Math.acos(cos/1.875);poses.pushPose();poses.translate(sin,cos,0);poses.translate(2.25,1.375,0);poses.mulPose(Axis.ZN.rotationDegrees((float)(Math.toDegrees(linkage)-90)));poses.translate(-2.25,-1.375,0);part(poses,buffers,light,overlay,"Transmission");poses.popPose();poses.pushPose();double cath=Math.sqrt(3.515625-(cos*cos)/2);poses.translate(1.875-cath+sin,0,0);part(poses,buffers,light,overlay,"Piston");poses.popPose();}
    @Override public void render(SteamEngineBlockEntity engine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){Direction facing=DummyableBlock.facing(engine.getBlockState());int yaw=switch(facing){case SOUTH->90;case EAST->180;case NORTH->270;default->0;};LegacyContactShadows.render(engine,model(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)).translate(2,0,0),poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(yaw));poses.translate(2,0,0);common(engine.lastRotor+(engine.rotor-engine.lastRotor)*partial,poses,buffers,light,overlay);poses.popPose();}
    @Override public boolean shouldRenderOffScreen(SteamEngineBlockEntity engine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(SteamEngineBlockEntity engine){var pos=engine.getBlockPos();return new AABB(pos.getX()-6,pos.getY(),pos.getZ()-6,pos.getX()+7,pos.getY()+2.563,pos.getZ()+7);}
}
