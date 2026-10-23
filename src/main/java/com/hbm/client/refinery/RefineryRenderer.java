package com.hbm.client.refinery;
import com.hbm.client.model.*;
import com.hbm.machine.refinery.RefineryBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Source full/refinery_exploded HFR meshes, authored smooth normals, original180° and tilt transforms. */
public final class RefineryRenderer implements BlockEntityRenderer<RefineryBlockEntity> {
    static final ResourceLocation MODEL=id("models/refinery.obj"),EXPLODED=id("models/refinery_exploded.obj"),TEXTURE=id("textures/models/refinery.png");
    private static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public RefineryRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh model(boolean exploded){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),exploded?EXPLODED:MODEL);}
    static void inventory(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(false),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
    @Override public void render(RefineryBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){var transform=new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.PI);if(machine.tilted)transform.translate(0,-.25F,0).rotateZ((float)Math.toRadians(10)).rotateY((float)Math.toRadians(5));LegacyContactShadows.render(machine,model(machine.hasExploded),transform,poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(180));if(machine.tilted){poses.translate(0,-.25,0);poses.mulPose(Axis.ZP.rotationDegrees(10));poses.mulPose(Axis.YP.rotationDegrees(5));}LegacyObjRenderer.renderAll(model(machine.hasExploded),poses,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);poses.popPose();}
    @Override public boolean shouldRenderOffScreen(RefineryBlockEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(RefineryBlockEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-2,p.getY()-.5,p.getZ()-2,p.getX()+3,p.getY()+10,p.getZ()+3);}
}
