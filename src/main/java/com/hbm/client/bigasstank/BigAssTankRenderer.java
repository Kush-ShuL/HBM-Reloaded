package com.hbm.client.bigasstank;

import com.hbm.client.model.*;
import com.hbm.fluid.*;
import com.hbm.machine.bigasstank.BigAssTankBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Unchanged HFR geometry, original tilt/yaw, danger diamonds and scrolling pair of level windows. */
public final class BigAssTankRenderer implements BlockEntityRenderer<BigAssTankBlockEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/bigasstank.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/bigasstank.png");
    public BigAssTankRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void model(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(mesh(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
    @Override public void render(BigAssTankBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        int yaw=switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->270;case WEST->0;case SOUTH->90;default->180;};
        var transform=new org.joml.Matrix4f().translation(.5F,0,.5F);if(machine.tilted)transform.translate(0,-1,0).rotateZ((float)Math.toRadians(10)).rotateY((float)Math.toRadians(5));transform.rotateY((float)Math.toRadians(yaw));
        LegacyContactShadows.render(machine,mesh(),transform,poses,buffers);
        poses.pushPose();poses.translate(.5,0,.5);if(machine.tilted){poses.translate(0,-1,0);poses.mulPose(Axis.ZP.rotationDegrees(10));poses.mulPose(Axis.YP.rotationDegrees(5));}poses.mulPose(Axis.YP.rotationDegrees(yaw));
        model(poses,buffers,light,overlay);var type=machine.tank.getTankType();
        if(type!=HbmFluids.NONE){poses.pushPose();poses.mulPose(Axis.YP.rotationDegrees(22.5F));for(int side=0;side<2;side++){poses.pushPose();poses.translate(5.5,2,0);LegacyDangerDiamond.render(poses,buffers,LightTexture.FULL_BRIGHT,type);poses.popPose();poses.mulPose(Axis.YP.rotationDegrees(180));}poses.popPose();}
        double height=machine.tank.getFill()*1.5D/machine.tank.getMaxFill(),u=-((machine.getLevel().getGameTime()%250+partial)/250D)%1D;
        var consumer=buffers.getBuffer(BigAssTankRenderTypes.fluid(type.getTexture()));
        window(poses,consumer,light,-5.9375,height,u,u+.5);window(poses,consumer,light,5.9375,height,u+.5,u);
        poses.popPose();
    }
    private static void window(PoseStack poses,com.mojang.blaze3d.vertex.VertexConsumer out,int light,double x,double height,double leftU,double rightU){var matrix=poses.last().pose();out.addVertex(matrix,(float)x,1.75F,-.25F).setUv((float)leftU,0).setColor(-1).setLight(light);out.addVertex(matrix,(float)x,(float)(1.75+height),-.25F).setUv((float)leftU,(float)-height).setColor(-1).setLight(light);out.addVertex(matrix,(float)x,(float)(1.75+height),.25F).setUv((float)rightU,(float)-height).setColor(-1).setLight(light);out.addVertex(matrix,(float)x,1.75F,.25F).setUv((float)rightU,0).setColor(-1).setLight(light);}
    @Override public boolean shouldRenderOffScreen(BigAssTankBlockEntity machine){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(BigAssTankBlockEntity machine){var pos=machine.getBlockPos();return new AABB(pos.getX()-6,pos.getY()-1,pos.getZ()-6,pos.getX()+7,pos.getY()+6,pos.getZ()+7);}
}
