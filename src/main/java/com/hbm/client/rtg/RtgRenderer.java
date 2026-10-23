package com.hbm.client.rtg;

import com.hbm.block.cable.NativeCableBlock;
import com.hbm.client.model.*;
import com.hbm.machine.rtg.RtgBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Original noSmooth Gen plus only the actually connected four horizontal Connector parts. */
public final class RtgRenderer implements BlockEntityRenderer<RtgBlockEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/rtg.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/rtg.png");
    public RtgRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(RtgBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);var consumer=buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(180));
        LegacyObjRenderer.renderOnly(mesh,poses,consumer,light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR_FLAT,"Gen");
        var level=machine.getLevel();if(level!=null)for(var direction:Direction.Plane.HORIZONTAL){
            if(!NativeCableBlock.canConnectTo(level,machine.getBlockPos().relative(direction),direction))continue;
            float angle=switch(direction){case WEST->180;case NORTH->90;case SOUTH->-90;default->0;};poses.pushPose();poses.mulPose(Axis.YP.rotationDegrees(angle));LegacyObjRenderer.renderOnly(mesh,poses,consumer,light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR_FLAT,"Connector");poses.popPose();
        }
        poses.popPose();
    }
    @Override public AABB getRenderBoundingBox(RtgBlockEntity machine){return new AABB(machine.getBlockPos());}
}
