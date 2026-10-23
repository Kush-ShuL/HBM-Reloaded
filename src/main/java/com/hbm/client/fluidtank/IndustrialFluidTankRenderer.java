package com.hbm.client.fluidtank;

import com.hbm.client.model.*;
import com.hbm.fluid.*;
import com.hbm.machine.fluidtank.IndustrialFluidTankBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

public final class IndustrialFluidTankRenderer implements BlockEntityRenderer<IndustrialFluidTankBlockEntity>{
    static final ResourceLocation INTACT=id("models/fluidtank.obj"),BROKEN=id("models/fluidtank_exploded.obj"),FRAME=id("textures/models/tank.png"),INNER=id("textures/models/tank/tank_inner.png");
    static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public IndustrialFluidTankRenderer(BlockEntityRendererProvider.Context context){}
    private static void part(ResourceLocation model,ResourceLocation texture,String part,PoseStack poses,MultiBufferSource buffers,int light,int overlay,int color){LegacyObjRenderer.renderOnly(LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),model),poses,buffers.getBuffer(RenderType.entityCutoutNoCull(texture)),light,overlay,color,LegacyObjRenderer.Convention.HFR,part);}
    static void model(PoseStack poses,MultiBufferSource buffers,int light,int overlay,HbmFluidType type,boolean exploded){var model=exploded?BROKEN:INTACT;part(model,FRAME,"Frame",poses,buffers,light,overlay,0xffffffff);if(exploded)part(model,INNER,"TankInner",poses,buffers,light,overlay,0xffffffff);String texture=type.renderWithTint?"NONE":type.isAntimatter()||type.isHighlyCorrosive()?"DANGER":type.getName();part(model,id("textures/models/tank/tank_"+texture.toLowerCase(Locale.ROOT)+".png"),"Tank",poses,buffers,light,overlay,type.renderWithTint?0xff000000|type.getTint():0xffffffff);}
    @Override public void render(IndustrialFluidTankBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){int contactAngle=switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->180;case WEST->270;case EAST->90;default->0;};com.hbm.client.model.LegacyContactShadows.render(machine,LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),machine.hasExploded?BROKEN:INTACT),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(contactAngle)),poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->180;case WEST->270;case EAST->90;default->0;}));var type=machine.tank.getTankType();model(poses,buffers,light,overlay,type,machine.hasExploded);if(type!=HbmFluids.NONE){surfaceDiamond(poses,buffers,light,type,-1);surfaceDiamond(poses,buffers,light,type,1);}poses.popPose();}
    /** User-requested attachment to the actual lower45-degree Tank facets, present in both source models.
     * Original renderer used the frame envelope at z=+/-1.501, leaving a gap to the cylindrical wall.
     * Authored facet endpoints: (y,abs(z))=(.368711,.821929),(.678071,1.131289).
     * Center the original.375-size decal exactly on that face; native polygon bias resolves depth ties.
     */
    private static void surfaceDiamond(PoseStack poses,MultiBufferSource buffers,int light,HbmFluidType type,int side){
        poses.pushPose();
        poses.translate(side*.25,(.368711+.678071)/2,side*((.821929+1.131289)/2));
        poses.mulPose(Axis.YP.rotationDegrees(-side*90));
        poses.mulPose(Axis.ZP.rotationDegrees(-45));
        poses.scale(1,.375F,.375F);
        TankDangerDiamondDecal.render(poses,buffers,light,type);
        poses.popPose();
    }
    @Override public boolean shouldRenderOffScreen(IndustrialFluidTankBlockEntity machine){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(IndustrialFluidTankBlockEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-2,p.getY(),p.getZ()-2,p.getX()+3,p.getY()+3,p.getZ()+3);}
}
