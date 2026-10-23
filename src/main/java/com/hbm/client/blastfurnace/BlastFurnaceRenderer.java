package com.hbm.client.blastfurnace;

import com.hbm.client.model.*;
import com.hbm.machine.blastfurnace.BlastFurnaceBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Exact source HFR noSmooth OBJ, metadata rotation, tilt transform and no-cull drawing. */
public final class BlastFurnaceRenderer implements BlockEntityRenderer<BlastFurnaceBlockEntity> {
    static final ResourceLocation MODEL=ResourceLocation.fromNamespaceAndPath("hbm","models/machines/blast_furnace.obj"),TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/models/machines/blast_furnace.png");
    public BlastFurnaceRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void draw(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),poses,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR_FLAT);}
    @Override public void render(BlastFurnaceBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        int angle=switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};
        var transform=new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(angle));if(machine.tilted)transform.translate(0,-.25F,0).rotateZ((float)Math.toRadians(10)).rotateY((float)Math.toRadians(5));LegacyContactShadows.render(machine,model(),transform,poses,buffers);
        poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(angle));if(machine.tilted){poses.translate(0,-.25,0);poses.mulPose(Axis.ZP.rotationDegrees(10));poses.mulPose(Axis.YP.rotationDegrees(5));}draw(poses,buffers,light,overlay);poses.popPose();
    }
    @Override public boolean shouldRenderOffScreen(BlastFurnaceBlockEntity furnace){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(BlastFurnaceBlockEntity furnace){var p=furnace.getBlockPos();var bounds=new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+7,p.getZ()+2);return furnace.tilted?bounds.inflate(1.5,1,1.5):bounds;}
}
