package com.hbm.client.turbine;
import com.hbm.client.model.*;
import com.hbm.fluid.HbmFluids;
import com.hbm.machine.turbine.IndustrialTurbineBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original authored smooth HFR mesh, independent world gauge/flywheel matrices and exact facing. */
public final class IndustrialTurbineRenderer implements BlockEntityRenderer<IndustrialTurbineBlockEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/industrial_turbine.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/industrial_turbine.png");
    public IndustrialTurbineRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void part(PoseStack poses,MultiBufferSource buffers,int light,int overlay,String part){LegacyObjRenderer.renderOnly(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,part);}
    private static int yaw(Direction direction){return switch(direction){case NORTH->180;case WEST->270;case EAST->90;default->0;};}
    @Override public void render(IndustrialTurbineBlockEntity turbine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        int yaw=yaw(DummyableBlock.facing(turbine.getBlockState()));LegacyContactShadows.render(turbine,model(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),poses,buffers);
        poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(yaw));part(poses,buffers,light,overlay,"Turbine");
        poses.pushPose();poses.translate(0,1.5,0);poses.mulPose(Axis.ZP.rotationDegrees(135-(turbine.tanks[0].getTankType().getID()-HbmFluids.STEAM.getID())*90));poses.translate(0,-1.5,0);part(poses,buffers,light,overlay,"Gauge");poses.popPose();
        poses.pushPose();poses.translate(0,1.5,0);poses.mulPose(Axis.ZN.rotationDegrees(turbine.lastRotor+(turbine.rotor-turbine.lastRotor)*partial));poses.translate(0,-1.5,0);part(poses,buffers,light,overlay,"Flywheel");poses.popPose();poses.popPose();
    }
    @Override public boolean shouldRenderOffScreen(IndustrialTurbineBlockEntity turbine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(IndustrialTurbineBlockEntity turbine){var pos=turbine.getBlockPos();return new AABB(pos.getX()-3,pos.getY(),pos.getZ()-3,pos.getX()+4,pos.getY()+3,pos.getZ()+4);}
}
