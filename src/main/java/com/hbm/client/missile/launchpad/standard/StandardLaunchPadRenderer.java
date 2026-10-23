package com.hbm.client.missile.launchpad.standard;
import com.hbm.client.model.*;
import com.hbm.client.missile.completion.MissileRenderer;import com.hbm.missile.completion.MissileContent;
import com.hbm.missile.launchpad.standard.StandardLaunchPadBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
/** Exact original Forge pad OBJ and optional actual HFR missile at y+1. */
public final class StandardLaunchPadRenderer implements BlockEntityRenderer<StandardLaunchPadBlockEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/weapons/launch_pad_silo.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/launchpad/silo.png");
    public StandardLaunchPadRenderer(BlockEntityRendererProvider.Context context){}
    static void pad(PoseStack poses,MultiBufferSource buffers,int light,int overlay){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);LegacyObjRenderer.renderAll(mesh,poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.FORGE);}
    @Override public void render(StandardLaunchPadBlockEntity pad,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){float rotation=switch(DummyableBlock.facing(pad.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);LegacyContactShadows.render(pad,mesh,new Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(rotation)),poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(rotation));pad(poses,buffers,light,overlay);var kind=MissileContent.kind(pad.toRender);if(kind!=null){poses.translate(0,1,0);MissileRenderer.model(kind,poses,buffers,light,overlay,true);}poses.popPose();}
    @Override public boolean shouldRenderOffScreen(StandardLaunchPadBlockEntity pad){return true;}@Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(StandardLaunchPadBlockEntity pad){var p=pad.getBlockPos();return new AABB(p.getX()-2,p.getY(),p.getZ()-2,p.getX()+3,p.getY()+15,p.getZ()+3);}
}
