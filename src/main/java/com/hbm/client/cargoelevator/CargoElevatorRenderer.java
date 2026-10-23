package com.hbm.client.cargoelevator;

import com.hbm.client.model.*;
import com.hbm.machine.cargoelevator.CargoElevatorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Exact source Base/Platform/Piston/Guides matrices, including fractional repeated piston pieces. */
public final class CargoElevatorRenderer implements BlockEntityRenderer<CargoElevatorEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/elevator.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/elevator.png");
    public CargoElevatorRenderer(BlockEntityRendererProvider.Context context){}
    static void part(PoseStack poses,MultiBufferSource buffers,int light,int overlay,String name){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);LegacyObjRenderer.render(mesh,poses,buffers.getBuffer(RenderType.entitySolid(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,WavefrontMesh.legacyPart(name));}
    @Override public void render(CargoElevatorEntity lift,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        poses.pushPose();poses.translate(.5,0,.5);
        if(lift.renderPlatform){double extension=lift.prevExtension+(lift.extension-lift.prevExtension)*partial;part(poses,buffers,light,overlay,"Base");poses.pushPose();poses.translate(0,extension,0);part(poses,buffers,light,overlay,"Platform");for(int i=0;i<extension+1;i++){part(poses,buffers,light,overlay,"Piston");poses.translate(0,-1,0);}poses.popPose();}
        poses.pushPose();for(int i=0;i<=lift.height;i++){part(poses,buffers,light,overlay,"Guides");poses.translate(0,1,0);}poses.popPose();poses.popPose();
    }
    @Override public AABB getRenderBoundingBox(CargoElevatorEntity lift){var p=lift.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+1+lift.height,p.getZ()+2);}
    @Override public int getViewDistance(){return 256;}
}
