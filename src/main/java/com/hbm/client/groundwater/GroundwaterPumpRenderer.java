package com.hbm.client.groundwater;

import com.hbm.client.model.*;
import com.hbm.machine.groundwater.*;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

/** RenderPump's unchanged smooth HFR OBJ, four independent part matrices and original rod linkage equation. */
public final class GroundwaterPumpRenderer<T extends GroundwaterPumpEntity> implements BlockEntityRenderer<T> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/pump.obj");
    private static final ResourceLocation STEAM=ResourceLocation.parse("hbm:textures/models/machines/pump_steam.png");
    private static final ResourceLocation ELECTRIC=ResourceLocation.parse("hbm:textures/models/machines/pump_electric.png");
    public GroundwaterPumpRenderer(BlockEntityRendererProvider.Context context){}
    private static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    private static void part(String group,GroundwaterPumpKind kind,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        var texture=kind==GroundwaterPumpKind.STEAM?STEAM:ELECTRIC;
        LegacyObjRenderer.render(model(),pose,buffers.getBuffer(RenderType.entityCutoutNoCull(texture)),light,overlay,-1,
                LegacyObjRenderer.Convention.HFR,WavefrontMesh.legacyPart(group));
    }
    static void common(double rotation,GroundwaterPumpKind kind,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        part("Base",kind,pose,buffers,light,overlay);
        pose.pushPose();pose.translate(0,2.25,0);pose.mulPose(Axis.ZP.rotationDegrees((float)(rotation-90)));pose.translate(0,-2.25,0);
        part("Rotor",kind,pose,buffers,light,overlay);pose.popPose();
        double sin=Math.sin(rotation*Math.PI/180D)*.5D-.5D;
        double cos=Math.cos(rotation*Math.PI/180D)*.5D;
        double angle=Math.acos(cos/2D),cath=Math.sqrt(1+(cos*cos)/2);
        pose.pushPose();pose.translate(0,1-cath+sin,0);pose.translate(0,4.75,0);
        pose.mulPose(Axis.ZN.rotationDegrees((float)(angle*180D/Math.PI-90D)));pose.translate(0,-4.75,0);
        part("Arms",kind,pose,buffers,light,overlay);pose.popPose();
        pose.pushPose();pose.translate(0,1-cath+sin,0);part("Piston",kind,pose,buffers,light,overlay);pose.popPose();
    }
    private static int rotation(Direction direction){return switch(direction){case SOUTH->90;case EAST->180;case NORTH->270;default->0;};}
    @Override public void render(T pump,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        int rotation=rotation(DummyableBlock.facing(pump.getBlockState()));
        var transform=new Matrix4f().translate(.5F,0,.5F).rotateY((float)Math.toRadians(rotation));
        LegacyContactShadows.render(pump,model(),transform,pose,buffers);
        pose.pushPose();pose.mulPose(transform);
        common(pump.lastRotor+(pump.rotor-pump.lastRotor)*partial,pump.kind,pose,buffers,light,overlay);pose.popPose();
    }
    @Override public boolean shouldRenderOffScreen(T pump){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(T pump){var p=pump.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+5,p.getZ()+2);}
}
