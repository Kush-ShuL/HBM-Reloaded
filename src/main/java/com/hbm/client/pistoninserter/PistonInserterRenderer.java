package com.hbm.client.pistoninserter;

import com.hbm.client.excavator.MovingItemRenderer;
import com.hbm.client.model.*;
import com.hbm.machine.pistoninserter.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.AABB;

/** Exact source Frame/Piston transforms and interpolated .9375 stroke, with the sole source item-in-frame kernel. */
public final class PistonInserterRenderer implements BlockEntityRenderer<PistonInserterEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/piston_inserter.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/piston_inserter.png");
    public PistonInserterRenderer(BlockEntityRendererProvider.Context context){}
    static void model(PoseStack poses,MultiBufferSource buffers,int light,int overlay,String part){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);var consumer=buffers.getBuffer(RenderType.entitySolid(TEXTURE));if(part==null)LegacyObjRenderer.renderAll(mesh,poses,consumer,light,overlay,-1,LegacyObjRenderer.Convention.FORGE);else LegacyObjRenderer.render(mesh,poses,consumer,light,overlay,-1,LegacyObjRenderer.Convention.FORGE,WavefrontMesh.legacyPart(part));}
    @Override public void render(PistonInserterEntity piston,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        poses.pushPose();poses.translate(.5,.5,.5);
        switch(piston.getBlockState().getValue(PistonInserterBlock.META)){
            case 0->poses.mulPose(Axis.XP.rotationDegrees(180));
            case 2->{poses.mulPose(Axis.XP.rotationDegrees(-90));poses.mulPose(Axis.YP.rotationDegrees(180));}
            case 3->poses.mulPose(Axis.XP.rotationDegrees(90));
            case 4->{poses.mulPose(Axis.ZP.rotationDegrees(90));poses.mulPose(Axis.YP.rotationDegrees(-90));}
            case 5->{poses.mulPose(Axis.ZP.rotationDegrees(-90));poses.mulPose(Axis.YP.rotationDegrees(90));}
            default->{}
        }
        poses.translate(0,-.5,0);model(poses,buffers,light,overlay,"Frame");
        double extension=(piston.lastExtend+(piston.renderExtend-piston.lastExtend)*partial)/PistonInserterEntity.MAX_EXTEND;
        poses.translate(0,extension*.9375,0);model(poses,buffers,light,overlay,"Piston");
        if(!piston.slot.isEmpty()){
            var stack=piston.slot.copyWithCount(1);
            if(stack.getItem()instanceof BlockItem)poses.translate(0,1.125,0);
            else{poses.translate(0,1.0625,.1);if(!Minecraft.useFancyGraphics())poses.translate(0,.01,0);poses.mulPose(Axis.XP.rotationDegrees(-90));}
            // Original renderer also creates an unspawned EntityItem for RenderDecoItem/renderInFrame.
            var display=new ItemEntity(piston.getLevel(),0,0,0,stack);
            var model=Minecraft.getInstance().getItemRenderer().getModel(stack,piston.getLevel(),null,0);
            MovingItemRenderer.renderSourceFrame(display,stack,model,poses,buffers,light);
        }
        poses.popPose();
    }
    @Override public AABB getRenderBoundingBox(PistonInserterEntity piston){var direction=PistonInserterBlock.direction(piston.getBlockState());return new AABB(piston.getBlockPos()).expandTowards(direction==null?0:direction.getStepX(),direction==null?0:direction.getStepY(),direction==null?0:direction.getStepZ());}
}
