package com.hbm.client.generator.diesel;
import com.hbm.client.model.*;
import com.hbm.machine.generator.diesel.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
public final class DieselRenderer implements BlockEntityRenderer<DieselBlockEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/dieselgen.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/dieselgen.png");
    public DieselRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void all(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
    private static void part(PoseStack poses,MultiBufferSource buffers,int light,int overlay,String part){LegacyObjRenderer.renderOnly(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,part);}
    @Override public void render(DieselBlockEntity engine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){Direction direction=engine.getBlockState().getValue(DieselBlock.FACING);int yaw=switch(direction){case SOUTH->270;case NORTH->90;case WEST->180;default->0;};LegacyContactShadows.render(engine,model(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(yaw));part(poses,buffers,light,overlay,"Generator");if(engine.isOn&&engine.hasAcceptableFuel()&&engine.tank.getFill()>0){double side=Math.sin(System.currentTimeMillis()/50D)*.005,front=Math.sin(System.currentTimeMillis()/25D)*.005;poses.translate(front,0,side);}part(poses,buffers,light,overlay,"Engine");poses.popPose();}
    @Override public boolean shouldRenderOffScreen(DieselBlockEntity engine){return true;}@Override public AABB getRenderBoundingBox(DieselBlockEntity engine){return new AABB(engine.getBlockPos()).inflate(.006);}
}
