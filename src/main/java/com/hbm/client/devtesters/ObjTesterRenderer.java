package com.hbm.client.devtesters;
import com.hbm.block.devtesters.ObjTesterEntity;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Only the actual called renderTileEntityAt; the spinning/emissive renderTileEntityAt2 is deliberately not activated. */
public final class ObjTesterRenderer implements BlockEntityRenderer<ObjTesterEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/dropship.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/entity/dropship.png");
    private static final AABB INFINITE=new AABB(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);
    public ObjTesterRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(ObjTesterEntity entity,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);var out=buffers.getBuffer(RenderType.entityCutout(TEXTURE));pose.pushPose();pose.translate(.5,0,.5);
        LegacyObjRenderer.renderOnly(model,pose,out,light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Pod");
        for(int i=0;i<4;i++){pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(45+90*i));pose.translate(.5,1.75,0);pose.mulPose(Axis.ZP.rotationDegrees(150));pose.translate(-.5,-1.75,0);LegacyObjRenderer.renderOnly(model,pose,out,light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Leg");pose.popPose();}
        pose.popPose();
    }
    @Override public AABB getRenderBoundingBox(ObjTesterEntity entity){return INFINITE;}
    @Override public int getViewDistance(){return 256;}
    @Override public boolean shouldRenderOffScreen(ObjTesterEntity entity){return true;}
}
