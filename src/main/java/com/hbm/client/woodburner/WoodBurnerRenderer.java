package com.hbm.client.woodburner;
import com.hbm.client.model.*;
import com.hbm.machine.woodburner.WoodBurnerBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
public final class WoodBurnerRenderer implements BlockEntityRenderer<WoodBurnerBlockEntity> {
    static final ResourceLocation MODEL=ResourceLocation.fromNamespaceAndPath("hbm","models/machines/wood_burner.obj"),TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/models/machines/wood_burner.png");
    public WoodBurnerRenderer(BlockEntityRendererProvider.Context context) {}
    static void mesh(PoseStack poses,MultiBufferSource buffers,int light,int overlay) { LegacyObjRenderer.renderAll(LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR); }
    @Override public void render(WoodBurnerBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {int contactAngle=switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->180;case WEST->270;case EAST->90;default->0;};com.hbm.client.model.LegacyContactShadows.render(machine,LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(contactAngle)).translate(-.5F,0,-.5F),poses,buffers); poses.pushPose(); poses.translate(.5,0,.5); int angle=switch(DummyableBlock.facing(machine.getBlockState())) { case NORTH->180; case WEST->270; case EAST->90; default->0; }; poses.mulPose(Axis.YP.rotationDegrees(angle)); poses.translate(-.5,0,-.5); mesh(poses,buffers,light,overlay); poses.popPose(); }
    @Override public boolean shouldRenderOffScreen(WoodBurnerBlockEntity machine) { return true; }
    @Override public int getViewDistance() { return 256; }
    @Override public AABB getRenderBoundingBox(WoodBurnerBlockEntity machine) { var p=machine.getBlockPos(); return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+6,p.getZ()+2); }
}
