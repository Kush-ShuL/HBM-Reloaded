package com.hbm.client.oilfracking;
import com.hbm.client.model.*;
import com.hbm.machine.oilfracking.FrackingEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Exact source fixed180Y tower plus four original uncolored silver pipe HFR parts atY.5. */
public final class FrackingRenderer implements BlockEntityRenderer<FrackingEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/fracking_tower.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/fracking_tower.png"),PIPE=ResourceLocation.parse("hbm:models/blocks/pipe_neo.obj"),PIPE_TEXTURE=ResourceLocation.parse("hbm:textures/block/pipe_silver.png");
    public FrackingRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh mesh(ResourceLocation id){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),id);}
    static void tower(PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(mesh(MODEL),pose,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR);}
    @Override public void render(FrackingEntity entity,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyContactShadows.render(entity,mesh(MODEL),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.PI),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(180));tower(pose,buffers,light,overlay);pose.translate(0,.5,0);LegacyObjRenderer.render(mesh(PIPE),pose,buffers.getBuffer(RenderType.entityCutoutNoCull(PIPE_TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,WavefrontMesh.legacyPart("pX").or(WavefrontMesh.legacyPart("nX")).or(WavefrontMesh.legacyPart("pZ")).or(WavefrontMesh.legacyPart("nZ")));pose.popPose();}
    @Override public boolean shouldRenderOffScreen(FrackingEntity entity){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(FrackingEntity entity){var p=entity.getBlockPos();return new AABB(p.getX()-4,p.getY(),p.getZ()-4,p.getX()+5,p.getY()+26,p.getZ()+5);}
}
