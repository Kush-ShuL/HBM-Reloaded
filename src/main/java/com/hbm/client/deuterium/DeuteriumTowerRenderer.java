package com.hbm.client.deuterium;

import com.hbm.client.model.*;
import com.hbm.machine.deuterium.DeuteriumTowerEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

/** Original smooth HFR mesh and exact four RenderDeuteriumTower matrices, including its non-centred origin. */
public final class DeuteriumTowerRenderer implements BlockEntityRenderer<DeuteriumTowerEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/machine_deuterium_tower.obj");
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/machine_deuterium_tower.png");
    public DeuteriumTowerRenderer(BlockEntityRendererProvider.Context context){}
    private static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    private static void all(PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.render(model(),pose,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,face->true);}
    static void inventory(PoseStack pose,MultiBufferSource buffers,int light,int overlay){pose.mulPose(Axis.YP.rotationDegrees(180));pose.scale(.5F,.5F,.5F);all(pose,buffers,light,overlay);}
    private static Matrix4f world(Direction direction){var matrix=new Matrix4f().rotateY((float)Math.PI);switch(direction){case NORTH->matrix.translate(0,0,-1);case SOUTH->matrix.rotateY((float)Math.PI).translate(1,0,0);case WEST->matrix.rotateY((float)(Math.PI/2)).translate(1,0,-1);case EAST->matrix.rotateY((float)(Math.PI*1.5));default->{}}return matrix;}
    @Override public void render(DeuteriumTowerEntity tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var transform=world(DummyableBlock.facing(tile.getBlockState()));LegacyContactShadows.render(tile,model(),transform,pose,buffers);pose.pushPose();pose.mulPose(transform);all(pose,buffers,light,overlay);pose.popPose();}
    @Override public boolean shouldRenderOffScreen(DeuteriumTowerEntity tile){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(DeuteriumTowerEntity tile){var p=tile.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+10,p.getZ()+2);}
}
