package com.hbm.client.specialvehicle;

import com.hbm.block.specialvehicle.*;
import com.hbm.client.model.LegacyContactShadows;
import com.hbm.weapon.specialvehicle.VehicleContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

/** Original RenderDecoBlock headings, including unplaced metadata0 and the boat's deliberate lack of yaw rotation. */
public final class VehicleDecoRenderer implements BlockEntityRenderer<VehicleDecoBlockEntity> {
    public VehicleDecoRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(VehicleDecoBlockEntity tile,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        boolean boat=tile.getBlockState().is(VehicleContent.BOAT.get());int meta=tile.getBlockState().getValue(VehicleDecoBlock.META);
        var model=new PoseStack();VehicleModels.blockPose(model,boat,meta);LegacyContactShadows.render(tile,VehicleModels.mesh(boat),new Matrix4f(model.last().pose()),poses,buffers);
        poses.pushPose();VehicleModels.blockPose(poses,boat,meta);VehicleModels.draw(boat,poses,buffers,light,overlay);poses.popPose();
    }
    @Override public boolean shouldRenderOffScreen(VehicleDecoBlockEntity tile){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(VehicleDecoBlockEntity tile){return AABB.INFINITE;}
}
