package com.hbm.client.structurewand;

import com.hbm.item.structurewand.StructureAuthoringBlock;
import com.hbm.item.structurewand.StructureAuthoringEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;

/** Original 12 white depth-tested edges above the save controller. */
public final class StructureAuthoringRenderer implements BlockEntityRenderer<StructureAuthoringEntity> {
    public StructureAuthoringRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(StructureAuthoringEntity tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        if(((StructureAuthoringBlock)tile.getBlockState().getBlock()).loading)return;
        LevelRenderer.renderLineBox(pose,buffers.getBuffer(RenderType.lines()),0,1,0,tile.sizeX,tile.sizeY+1,tile.sizeZ,1,1,1,1);
    }
    @Override public boolean shouldRenderOffScreen(StructureAuthoringEntity tile){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(StructureAuthoringEntity tile){return AABB.INFINITE;}
}
